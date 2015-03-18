package com.funnycampus.socket;

import java.io.Serializable;

public class CommentMSG implements Serializable {
	private String comment;
	private String time;
	private int id;
	private String name;
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getComment() {
		return this.comment;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	public String getTime() {
		return this.time;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	public int getID() {
		return this.id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
}
