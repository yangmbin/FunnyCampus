package com.funnycampus.socket;

import java.io.Serializable;

public class BackCommentMSG implements Serializable {
	private String nickname;
	private String head_img;
	private String time;
	private String comment;
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getNickname() {
		return this.nickname;
	}
	
	public void setHeadIMG(String head_img) {
		this.head_img = head_img;
	}
	public String getHeadIMG() {
		return this.head_img;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	public String getTime() {
		return this.time;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getComment() {
		return this.comment;
	}
}
