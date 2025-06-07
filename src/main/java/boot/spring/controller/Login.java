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
			return "rich/index";
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
