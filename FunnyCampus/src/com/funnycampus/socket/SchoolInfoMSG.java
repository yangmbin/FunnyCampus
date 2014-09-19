package com.funnycampus.socket;

import java.io.Serializable;

public class SchoolInfoMSG implements Serializable {
	private String name;
	private String content;
	private String headIMG;
	private String nickname;
	private String time;
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return this.content;
	}
	
	public void setHeadIMG(String headIMG) {
		this.headIMG = headIMG;
	}
	public String getHeadIMG() {
		return this.headIMG;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getNickname() {
		return this.nickname;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	public String getTime() {
		return this.time;
	}
}
