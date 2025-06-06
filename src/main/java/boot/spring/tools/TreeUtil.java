package boot.spring.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TreeUtil {
    /**
     * 循环构建树
     *
     * @param menuList       需要合成树的List
     * @param pIdGetter      对象中获取父级ID方法,如:Node:getParentId
     * @param idGetter       对象中获取主键ID方法 ,如：Node:getId
     * @param rootCheck      判断对象是否根节点的方法，如： x->x.getParentId()==0,x->x.getParentMenuId()==0
     * @param setSubChildren 对象中设置下级数据列表方法，如： Menu::setSubMenus
     */
    public static <T, E> List<E> makeTree(List<E> menuList, Function<E, T> pIdGetter, Function<E, T> idGetter, Predicate<E> rootCheck, BiConsumer<E, List<E>> setSubChildren) {
        Map<T, List<E>> parentMenuMap = new HashMap<>();
        for (E node : menuList) {
            //获取父节点id
            T parentId = pIdGetter.apply(node);
            //节点放入父节点的value内
            parentMenuMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(node);
        }
        List<E> result = new ArrayList<>();
        for (E node : menuList) {
            //添加到下级数据中
            setSubChildren.accept(node, parentMenuMap.getOrDefault(idGetter.apply(node), new ArrayList<>()));
            //如里是根节点，加入结构
            if (rootCheck.test(node)) {
                result.add(node);
            }
        }
        return result;
    }


    /**
     * 树中查找
     * 当前节点不匹配predicate，只要其子节点列表匹配到即保留
     * @param tree  需要查找的树
     * @param predicate  过滤条件，根据业务场景自行修改
     * @param getSubChildren 获取下级数据方法，如：MenuVo::getSubMenus
     * @return List<E> 过滤后的树
     * @param <E> 泛型实体对象
     */
    public static <E> List<E> search(List<E> tree, Predicate<E> predicate, Function<E, List<E>> getSubChildren) {
        List<E> result = new ArrayList<>();

        for (E item : tree) {
            List<E> childList = getSubChildren.apply(item);
            List<E> filteredChildren = new ArrayList<>();

            if (childList != null && !childList.isEmpty()) {
                filteredChildren = search(childList, predicate, getSubChildren);
            }
            // 如果当前节点匹配，或者至少有一个子节点保留，就保留当前节点
            if (predicate.test(item) || !filteredChildren.isEmpty()) {
                result.add(item);
                // 还原下一级子节点如果有
                if (!filteredChildren.isEmpty()) {
                    getSubChildren.apply(item).clear();
                    getSubChildren.apply(item).addAll(filteredChildren);
                }
            }
        }
        return result;
    }

    /**
     * 树中过滤
     * 过滤满足条件的数据节点，如里当前节点不满足其所有子节点都会过滤掉。
     * @param tree  需要进行过滤的树
     * @param predicate  过滤条件判断
     * @param getChildren 获取下级数据方法，如：Menu::getSubMenus
     * @return List<E> 过滤后的树
     * @param <E> 泛型实体对象
     */
    public static <E> List<E> filter(List<E> tree, Predicate<E> predicate, Function<E, List<E>> getChildren) {
        return tree.stream().filter(item -> {
            if (predicate.test(item)) {
                List<E> children = getChildren.apply(item);
                if (children != null && !children.isEmpty()) {
                    filter(children, predicate, getChildren);
                }
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

    /**
     * 递归地更新树中每个节点的 total 值，使其等于本节点的 value 加上所有子节点的 total。
     *
     * @param tree          需要进行更新的树（以列表形式）
     * @param getSubChildren 获取子节点列表的方法
     * @param getValue      获取节点 value 的方法
     * @param getTotal      获取节点 total 的方法
     * @param setTotal      设置节点 total 的方法
     * @param <E>           泛型实体对象类型
     */
    public static <E> void accumulateValuesToParent(List<E> tree,
                                             Function<E, List<E>> getSubChildren,
                                             Function<E, Integer> getValue,
                                             Function<E, Integer> getTotal,
                                             BiConsumer<E, Integer> setTotal) {
        for (E node : tree) {
            // 递归地对每个节点的子节点进行值累加
            List<E> children = getSubChildren.apply(node);
            int sumOfChildren = 0;

            if (children != null && !children.isEmpty()) {
                accumulateValuesToParent(children, getSubChildren, getValue, getTotal, setTotal);
                sumOfChildren = children.stream().mapToInt(getTotal::apply).sum(); // 使用 getTotal
            }

            // 计算当前节点的新 total: 自身 value + 子节点 total 之和
            int currentValue = getValue.apply(node);
            int newTotal = currentValue + sumOfChildren;

            // 设置当前节点的 total
            setTotal.accept(node, newTotal);
        }
    }

    /**
     * 根据给定的 Map 设置树中节点的 value 字段。
     *
     * @param tree        需要进行更新的树（以列表形式）
     * @param valueMap    包含节点 name 和对应 value 的映射
     * @param getName     获取节点 name 的方法
     * @param setValue    设置节点 value 的方法
     * @param getChildren 获取子节点列表的方法
     * @param <E>         泛型实体对象类型
     */
    public static <E> void setValueFromMap(List<E> tree,
                                           Map<String, Integer> valueMap,
                                           Function<E, String> getName,
                                           BiConsumer<E, Integer> setValue,
                                           Function<E, List<E>> getChildren) {
        for (E node : tree) {
            // 获取当前节点的 name
            String nodeName = getName.apply(node);

            // 如果在 map 中找到了对应的 value，则设置节点的 value
            if (valueMap.containsKey(nodeName)) {
                setValue.accept(node, valueMap.get(nodeName));
            }

            // 递归地对每个节点的子节点进行值设置
            List<E> children = getChildren.apply(node);
            if (children != null && !children.isEmpty()) {
                setValueFromMap(children, valueMap, getName, setValue, getChildren);
            }
        }
    }

    /**
     * 递归地将树中所有节点的 value 和 total 字段设置为 0。
     *
     * @param tree          需要进行更新的树（以列表形式）
     * @param getSubChildren 获取子节点列表的方法
     * @param setValue      设置节点 value 的方法
     * @param setTotal      设置节点 total 的方法
     * @param <E>           泛型实体对象类型
     */
    public static <E> void resetValueAndTotal(List<E> tree,
                                              Function<E, List<E>> getSubChildren,
                                              BiConsumer<E, Integer> setValue,
                                              BiConsumer<E, Integer> setTotal) {
        for (E node : tree) {
            // 递归地对每个节点的子节点进行值重置
            List<E> children = getSubChildren.apply(node);
            if (children != null && !children.isEmpty()) {
                resetValueAndTotal(children, getSubChildren, setValue, setTotal);
            }

            // 将当前节点的 value 和 total 设置为 0
            setValue.accept(node, 0);
            setTotal.accept(node, 0);
        }
    }
}
