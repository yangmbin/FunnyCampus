package com.funnycampus.socket;

import java.io.Serializable;

public class SetUserNicknameMSG implements Serializable {
	private String name;
	private String nickname;
	
	public void setName(String name) { 
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getNickname() {
		return this.nickname;
	}
}
