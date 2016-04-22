package com.tianyl.weixin.web;

import com.tianyl.core.ioc.annotation.Autowired;
import com.tianyl.core.mvc.annotation.Controller;
import com.tianyl.weixin.service.ArticalService;

@Controller("/wx")
public class WeiXinController {

	@Autowired
	private ArticalService articalService;

}
