package boot.spring.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import boot.spring.exception.MathException;
import boot.spring.pagemodel.ActorGrid;
import boot.spring.pagemodel.AjaxResult;
import boot.spring.po.Actor;
import boot.spring.po.City;
import boot.spring.service.ActorService;
import boot.spring.service.CityService;
import boot.spring.tools.FtpUtil;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;




@Controller
public class ActorController {
	@Autowired
	private ActorService actorservice;
	
	@Autowired
	CityService cityservice;
	
	@Autowired
	FtpUtil ftpUtil;
	
	private static final Logger LOG = LoggerFactory.getLogger(ActorController.class);
	

	@RequestMapping(value="/actors",method = RequestMethod.GET)
	@ResponseBody
	public ActorGrid getactorlist(@RequestParam(value="current") int current,@RequestParam(value="rowCount") int rowCount){
		int total=actorservice.getactornum();
		List<Actor> list=actorservice.getpageActors(current,rowCount);
		ActorGrid grid=new ActorGrid();
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setRows(list);
		grid.setTotal(total);
		LOG.info("获取所有演员列表");
		return grid;
	}
	

	@RequestMapping(value="/actors",method = RequestMethod.PUT)
	@ResponseBody
	public Actor updateactor(@RequestBody Actor a){
		Actor actor=actorservice.updateactor(a);
		LOG.info("修改一个演员");
		return actor;
	}

	@RequestMapping(value="/actors/{id}",method = RequestMethod.GET)
	@ResponseBody
	public Actor getactorbyid(@PathVariable("id") short id){
		Actor a=actorservice.getActorByid(id);
		LOG.info("获取一个演员");
		return a;
	}
	

	@RequestMapping(value="/actors",method = RequestMethod.POST)
	@ResponseBody
	public Actor add(@RequestBody Actor a){
		Actor actor=actorservice.addactor(a);
		LOG.info("添加一个演员");
		return actor;
	}

	@RequestMapping(value="/actors/{id}",method = RequestMethod.DELETE)
	@ResponseBody
	public String delete(@PathVariable("id") String id){
		actorservice.delete(Short.valueOf(id));
		LOG.info("删除一个演员");
		return "success";
	}

	@RequestMapping(value="/exportactor",method = RequestMethod.POST)
	public void export(HttpServletResponse response) throws IOException{
		List<Actor> list = actorservice.getpageActors(1, 500);
		ExportParams exportParams = new ExportParams();
        exportParams.setSheetName("演员");
        exportParams.setType(ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, Actor.class, list);
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("actor.xlsx" , "UTF-8"));
        workbook.write(response.getOutputStream());
	}

	@RequestMapping(value="/exportTemplateactor",method = RequestMethod.POST)
	public void exportTemplateactor(HttpServletResponse response) throws IOException{
		List<Map<String, Object>> list = actorservice.listActorMap();
		TemplateExportParams params = new TemplateExportParams("static/basetemplate.xlsx", 0);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", list.size());
		map.put("maplist", list);
		Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("actor.xlsx" , "UTF-8"));
        workbook.write(response.getOutputStream());
	}

	@RequestMapping(value="/multiExport",method = RequestMethod.POST)
	public void multiExport(HttpServletResponse response) throws IOException{
		List<Map<String, Object>> sheets= new ArrayList<>();
		List<Actor> list = actorservice.getpageActors(1, 500);
		List<City> citys=cityservice.getpagecitylist(1,500);
		Map<String, Object> sheet1=new HashMap<>();
		ExportParams exportParams = new ExportParams();
        exportParams.setSheetName("演员");
        exportParams.setType(ExcelType.XSSF);
        sheet1.put("title", exportParams);
        sheet1.put("data", list);
        sheet1.put("entity", Actor.class);
        
        Map<String, Object> sheet2 = new HashMap<>();
		ExportParams params2 = new ExportParams();
		params2.setSheetName("国家");
		params2.setType(ExcelType.XSSF);
        sheet2.put("title", params2);
        sheet2.put("data", citys);
        sheet2.put("entity", City.class);
        
        sheets.add(sheet1);
        sheets.add(sheet2);
        Workbook workbook = ExcelExportUtil.exportExcel(sheets, ExcelType.XSSF);
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=multiexport.xlsx");
        workbook.write(response.getOutputStream());
	}

	@RequestMapping(value="/importactor",method = RequestMethod.POST)
	@ResponseBody
	public void importExcel(@RequestParam MultipartFile uploadfile) throws Exception{
		ImportParams params = new ImportParams();
		params.setHeadRows(1);
        List<Actor> list = ExcelImportUtil.importExcel(uploadfile.getInputStream(), Actor.class, params);
        list.forEach(a->System.out.println(a));
	}
	
	@RequestMapping(value="/showactor",method = RequestMethod.GET)
	String showactor(){
		return "showactor";
	}

	@RequestMapping(value="/downloadFTP",method = RequestMethod.GET)
	@ResponseBody
	public void downloadFTP() throws Exception{
		ftpUtil.downloadFiles("/wsz", "/测试1dd.png","D://pic");
	}	
	

	@RequestMapping(value="/exportFTP",method = RequestMethod.GET)
	@ResponseBody
	public void exportFTP(HttpServletResponse response) throws Exception{
    	InputStream is=ftpUtil.exportFile("/王", "/测试1.png");
		response.setContentType("application/x-png");
		response.setHeader("Content-Disposition","attachment;filename=1.png");
		ServletOutputStream output = response.getOutputStream();
		IOUtils.copy(is, output);
	}	
	

	@RequestMapping(value="/exportBase64",method = RequestMethod.GET)
	@ResponseBody
	public AjaxResult exportBase64(HttpServletResponse response) throws Exception{
    	String base64=ftpUtil.exportBase64("/王", "/测试1.png");
    	return AjaxResult.success(base64);
	}

	@RequestMapping(value="/exception/{id}",method = RequestMethod.GET)
	@ResponseBody
	public AjaxResult exception(@PathVariable Integer id) throws MathException {
		if (id == 1) {
			throw new RuntimeException("运行错误");
		} else {
			throw new MathException("计算错误");
		}
	}
	

	@RequestMapping(value="/groupactor",method = RequestMethod.GET)
	@ResponseBody
	public Map<String, List<Actor>> groupactor(){
		List<Actor> list=actorservice.getpageActors(1,888);
		Map<String, List<Actor>> collect = list.stream().collect(Collectors.groupingBy(Actor::getLast_update));
		collect.forEach((a, b) -> {
			System.out.println(a + ": " + b.stream().mapToInt(Actor::getActor_id).count());
		});
		return collect;
	}
	
}
