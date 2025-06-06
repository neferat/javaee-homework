package boot.spring.controller;

import boot.spring.pagemodel.*;
import boot.spring.po.Item;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import boot.spring.service.ItemService;

@Controller
public class ItemController {

    @Autowired
    ItemService itemService;

    private static final Logger LOG = LoggerFactory.getLogger(ItemController.class);

    @RequestMapping(value="/item/{id}",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getItem(@PathVariable Long id){
        Item result = itemService.getItemById(id);
        return AjaxResult.success(result);
    }

    @RequestMapping(value="/pageItems",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult itemList(@RequestParam Integer pagenum, @RequestParam Integer pagesize){
        List<Item> result = itemService.listItems(pagenum, pagesize);
        int total = itemService.listItems().size();
        DataGrid<Item> grid = new DataGrid<Item>();
        grid.setCurrent(pagenum);
        grid.setRowCount(pagesize);
        grid.setTotal(total);
        grid.setRows(result);
        return AjaxResult.success(grid);
    }
}
