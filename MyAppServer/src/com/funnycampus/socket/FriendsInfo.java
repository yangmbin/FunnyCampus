package com.funnycampus.socket;

import java.io.Serializable;

public class FriendsInfo implements Serializable {
	private String headimg, username, nickname;
	
	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}
	public String getHeadimg() {
		return this.headimg;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return this.username;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getNickname() {
		return this.nickname;
	}
}
