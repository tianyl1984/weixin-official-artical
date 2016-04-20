package com.tianyl.weixin.model;

import com.tianyl.core.orm.annotation.Table;

@Table("wx_officialaccount")
public class OfficialAccount {

	private Integer id;

	private String name;

	private String wxId;

	public OfficialAccount() {

	}

	public OfficialAccount(Integer id, String name, String wxId) {
		super();
		this.id = id;
		this.name = name;
		this.wxId = wxId;
	}

	public OfficialAccount(String name, String wxId) {
		super();
		this.name = name;
		this.wxId = wxId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWxId() {
		return wxId;
	}

	public void setWxId(String wxId) {
		this.wxId = wxId;
	}

}
