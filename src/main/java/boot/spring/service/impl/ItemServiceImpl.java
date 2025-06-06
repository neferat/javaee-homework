package boot.spring.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;

import boot.spring.annotation.DataSource;
import boot.spring.mapper.ItemMapper;
import boot.spring.po.Item;
import boot.spring.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemMapper itemMapper;

    @Override
    @DataSource(DataSource.REST)
    public Item getItemById(Long id) {
        return itemMapper.selectByPrimaryKey(id);
    }

    @Override
    @DataSource(DataSource.REST)
    public List<Item> listItems() {
        return itemMapper.listItems();
    }

    @Override
    @DataSource(DataSource.REST)
    public List<Item> listItems(int pagenum, int pagesize) {
        PageHelper.startPage(pagenum,pagesize);
        List<Item> page = itemMapper.listItems();
        return page;
    }

}
