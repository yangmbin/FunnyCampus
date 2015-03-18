package com.funnycampus.socket;

import java.io.Serializable;

public class PeopleAroundInfo implements Serializable {
private String headimg, username, nickname, distance;
	
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
	
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getDistance() {
		return this.distance;
	}
}
