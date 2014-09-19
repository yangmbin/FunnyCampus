package com.funnycampus.socket;

import java.io.Serializable;
import java.util.List;

public class SchoolInfoMSGList implements Serializable {
	private List<SchoolInfoMSG> list;
	
	public List<SchoolInfoMSG> getList() 
	{
		return this.list;
	}
	public void setList(List<SchoolInfoMSG> list) 
	{
		this.list = list;
	}
}
