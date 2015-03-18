package com.funnycampus.socket;

import java.io.Serializable;
import java.util.List;

public class BackCommentMSGList implements Serializable {
	private List<BackCommentMSG> list;
	
	public void setList(List<BackCommentMSG> list) {
		this.list = list;
	}
	public List<BackCommentMSG> getList() {
		return this.list;
	}
}
