package com.tianyl.weixin.service;

import com.tianyl.core.ioc.annotation.Autowired;
import com.tianyl.core.ioc.annotation.Service;
import com.tianyl.weixin.dao.ArticalDAO;

@Service
public class ArticalService {

	@Autowired
	private ArticalDAO articalDAO;

	public void m1() {
		articalDAO.m1();
	}

}
