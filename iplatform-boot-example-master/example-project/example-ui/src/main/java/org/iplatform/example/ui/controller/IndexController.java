package org.iplatform.example.ui.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.iplatform.example.ui.feign.IndexClient;
import org.iplatform.microservices.core.security.UserDetailsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
	
	@Autowired
	private UserDetailsUtil userDetailsUtil;

	@Autowired
	private IndexClient indexClient;
	
	@RequestMapping("/")
	public String index(ModelMap map,Principal principal) throws Exception {	
		//每次进入首页都清除用户缓存信息，以便于后续操作会重新从认证服务器中获取后再次缓存
		userDetailsUtil.removeUserDetails(principal.getName());		
		return "index";		
	}
	
	@RequestMapping(value = "/jump", method = RequestMethod.POST)
	public String jump(ModelMap map,HttpServletRequest request,Principal principal) throws Exception {	
		String menuInfo = request.getParameter("menuInfo");
		map.put("menuInfo", menuInfo);
		return "menu";
	}

	/**
	 * 仅演示用
	 */
	@RequestMapping("/hello")
	@ResponseBody
	public String hello() throws Exception{
		return indexClient.hello().getBody().getData();
	}
}