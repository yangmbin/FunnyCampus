package com.funnycampus.socket;

import java.io.Serializable;

public class LikeAndDislikeMSG implements Serializable {
	private int freshNewsID;
	private String username;
	private String likeORdislike;
	private int clickPosition;
	
	public void setFreshNewsID(int ID) {
		this.freshNewsID = ID;
	}
	public int getFreshNewsID() {
		return this.freshNewsID;
	}
	
	public void setUsername(String name) {
		this.username = name;
	}
	public String getUsername() {
		return this.username;
	}
	
	public void setLikeOrdislike(String likeOrdislike) {
		this.likeORdislike = likeOrdislike;
	}
	public String getLikeORdislike() {
		return this.likeORdislike;
	}
	
	public void setClickPosition(int position) {
		this.clickPosition = position;
	}
	public int getClickPosition() {
		return this.clickPosition;
	}
}
