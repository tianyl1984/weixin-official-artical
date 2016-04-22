package com.tianyl.weixin.model;

import java.util.Date;

import com.tianyl.core.orm.annotation.Column;
import com.tianyl.core.orm.annotation.Table;

@Table("wx_artical")
public class Artical {

	private Integer id;

	@Column("id_officialaccount")
	private Integer officialAccountId;

	private String title;

	private String url;

	private Date publishDate;

	private String uuid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOfficialAccountId() {
		return officialAccountId;
	}

	public void setOfficialAccountId(Integer officialAccountId) {
		this.officialAccountId = officialAccountId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
