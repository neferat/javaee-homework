package boot.spring.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import boot.spring.po.LableInfo;
import boot.spring.tools.TreeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class TreeController {


	@GetMapping(value="/tree")
	public List<LableInfo> tree(@RequestParam(required = false) String name, @RequestParam(required = false) String lable) {
		List<LableInfo> list  = new ArrayList<>();
		list.add(new LableInfo(1, 0, "项目指标", 2));
		list.add(new LableInfo(2, 0, "技术指标", 5));
		list.add(new LableInfo(3, 0, "产出指标", 6));
		list.add(new LableInfo(4, 1, "项目1", 3));
		list.add(new LableInfo(5, 2, "技术1", 1));
		list.add(new LableInfo(6, 3, "产出指标1", 1));
		list.add(new LableInfo(7, 3, "产出指标2", 1));
		list.add(new LableInfo(8, 4, "项目12", 5));
		list.add(new LableInfo(9, 4, "项目13", 3));
		list.add(new LableInfo(10, 8, "项目123", 2));

		List<LableInfo> tree = TreeUtil.makeTree(list,
				LableInfo::getPid,
				LableInfo::getId,
				x -> x.getPid() == 0,
				LableInfo::setChildren);

		// 累加
		TreeUtil.accumulateValuesToParent(tree, LableInfo::getChildren, LableInfo::getValue, LableInfo::getTotal, LableInfo::setTotal);

		// 搜索
		if (StringUtils.isNotEmpty(name)) {
			tree = TreeUtil.search(tree, x -> name.equals(x.getLable()), LableInfo::getChildren);
		}
		if (StringUtils.isNotEmpty(lable)) {
			tree = TreeUtil.filter(tree, x -> lable.equals(x.getLable()), LableInfo::getChildren);
		}
		return tree;
	}

	@GetMapping(value="/caculate")
	public List<LableInfo> caculate() {
		List<LableInfo> list  = new ArrayList<>();
		list.add(new LableInfo(1, 0, "项目指标", 2));
		list.add(new LableInfo(2, 0, "技术指标", 5));
		list.add(new LableInfo(3, 0, "产出指标", 6));
		list.add(new LableInfo(4, 1, "项目1", 3));
		list.add(new LableInfo(5, 2, "技术1", 1));
		list.add(new LableInfo(6, 3, "产出指标1", 1));
		list.add(new LableInfo(7, 3, "产出指标2", 1));
		list.add(new LableInfo(8, 4, "项目12", 5));
		list.add(new LableInfo(9, 4, "项目13", 3));
		list.add(new LableInfo(10, 8, "项目123", 2));

		List<LableInfo> tree = TreeUtil.makeTree(list,
				LableInfo::getPid,
				LableInfo::getId,
				x -> x.getPid() == 0,
				LableInfo::setChildren);

		// 累加
		TreeUtil.accumulateValuesToParent(tree, LableInfo::getChildren, LableInfo::getValue, LableInfo::getTotal, LableInfo::setTotal);
		// 清零
		TreeUtil.resetValueAndTotal(tree, LableInfo::getChildren, LableInfo::setValue, LableInfo::setTotal);
		// 赋值
		Map<String, Integer> map = new HashMap<>();
		map.put("项目指标", 11);
		map.put("项目1", 4);
		map.put("技术1", 7);
		map.put("项目13", 7);
		TreeUtil.setValueFromMap(tree, map, LableInfo::getLable, LableInfo::setValue, LableInfo::getChildren);
		// 累加
		TreeUtil.accumulateValuesToParent(tree, LableInfo::getChildren, LableInfo::getValue, LableInfo::getTotal, LableInfo::setTotal);
		return tree;
	}
}
