package boot.spring.controller;

import javax.servlet.http.HttpSession;

import boot.spring.pagemodel.AjaxResult;
import boot.spring.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import boot.spring.service.LoginService;



@Controller
public class Login {
	@Autowired
	LoginService loginservice;
	
	@RequestMapping(value="/loginvalidate",method=RequestMethod.POST)
	public String loginvalidate(@RequestParam("username") String username,@RequestParam("password") String pwd,HttpSession httpSession){
		if(username==null)
			return "login";
		
		// 使用users表进行登录验证
		User user = loginservice.getUserByUsername(username);
		if(user != null && pwd.equals(user.getPassword()) && "active".equals(user.getStatus()))
		{
			httpSession.setAttribute("currentUser", username);
			httpSession.setAttribute("userId", user.getUserId());
			httpSession.setAttribute("userInfo", user);
			
			// 检查是否为admin用户，决定跳转页面
			boolean isAdmin = false;
			
			// 方式1：通过用户名判断
			if ("admin".equals(username)) {
				isAdmin = true;
			}
			
			// 方式2：通过用户等级判断
			if (user.getUserLevel() != null && user.getUserLevel() >= 5) {
				isAdmin = true;
			}
			
			// 方式3：通过用户ID判断（admin通常是第一个用户）
			if (user.getUserId() != null && user.getUserId().equals(1L)) {
				isAdmin = true;
			}
			
			// 根据用户类型跳转到不同页面
			if (isAdmin) {
				return "rich/index"; // admin用户跳转到包含管理面板的页面
			} else {
				return "user/index"; // 普通用户跳转到普通页面
			}
		}else
			return "fail";
	}
	
	
	@RequestMapping(value="/logout",method = RequestMethod.GET)
	public String logout(HttpSession httpSession){
		httpSession.removeAttribute("currentUser");
		httpSession.removeAttribute("userId");
		httpSession.removeAttribute("userInfo");
		return "login";
	}
	
	@RequestMapping(value="/login",method = RequestMethod.GET)
	String login(){
		return "login";
	}
	
	@RequestMapping(value="/currentuser",method = RequestMethod.GET)
	@ResponseBody
	AjaxResult curruntuser(HttpSession httpSession){
		User userInfo = (User) httpSession.getAttribute("userInfo");
		String username = (String) httpSession.getAttribute("currentUser");
		if(userInfo != null) {
			return AjaxResult.success(userInfo);
		} else if(username != null) {
			return AjaxResult.success(username);
		} else {
			return AjaxResult.error("未登录");
		}
	}

	@RequestMapping(value="/sse",method = RequestMethod.GET)
	public String sse(){
		return "sse";
	}
  }
