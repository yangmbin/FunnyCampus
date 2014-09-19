package com.funnycampus.socket;

import java.io.Serializable;

public class SetUserHeadImgMSG implements Serializable {
	private String name;
	private String headimg;
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}
	public String getHeadimg() {
		return this.headimg;
	}
}
