package com.tianyl.weixin.web;

import com.tianyl.core.mvc.annotation.Controller;

@Controller("/wx")
public class WeiXinController {

	public Object m1(String name) {
		return "中文" + name;
	}
}
