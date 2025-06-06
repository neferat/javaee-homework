package boot.spring.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import boot.spring.event.MessagePushEvent;
import boot.spring.pagemodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import boot.spring.service.ActorService;
import boot.spring.service.DwService;

import javax.annotation.Resource;

@Controller
public class TaskController {
	@Autowired
	ActorService actorService;
	
	@Autowired
	DwService dwService;
	
	@Autowired
	ThreadPoolExecutor executor;

	@Resource
	ApplicationEventPublisher publisher;
	
	private static final Logger LOG = LoggerFactory.getLogger(TaskController.class);

	@RequestMapping(value="/asyncTask",method = RequestMethod.GET)
	@ResponseBody
	public AjaxResult asyncTask(){
		LOG.info("开始异步任务");
		String result = actorService.task();
		LOG.info("结束异步任务");
		return AjaxResult.success(result);
	}

	@RequestMapping(value="/threadPool",method = RequestMethod.GET)
	@ResponseBody
	public AjaxResult threadPool() throws Exception {
		LOG.info("开始使用线程池");
		executor.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {
				String result = actorService.asyncTask();
				if (result.equals("ok")) {
					LOG.info("异步任务执行成功");
				}
				return result;
			}
			
		});
		LOG.info("结束使用线程池");
		return AjaxResult.success();
	}

	@RequestMapping(value="/threadPoolWithoutR",method = RequestMethod.GET)
	@ResponseBody
	public AjaxResult threadPoolWithoutR() {
		LOG.info("开始使用线程池");
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				actorService.asyncTask();
			}
		});
		LOG.info("结束使用线程池");
		return AjaxResult.success();
	}

	@RequestMapping(value="/pushMessage",method = RequestMethod.GET)
	@ResponseBody
	public AjaxResult pushMessage(){
		publisher.publishEvent(new MessagePushEvent(this, "充电时间已完成", "xxxx"));
		return AjaxResult.success("ok");
	}
}
