package com.funnycampus.socket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SchoolInfoMSG implements Serializable {
	private String name;
	private String content;
	private String headIMG;
	private String nickname;
	private String time;
	
    private List<String> photos = new ArrayList();
    private int type;
    
    private int id;
    
    //ÆÀÂÛ ÔÞ ²È
    private int commentNum, likeNum, dislikeNum;
	
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
	
	public void setPhotos(List<String> paramList) {
		for (int i = 0; ;i++)
	    {
	      if (i >= paramList.size())
	          return;
	      this.photos.add((String)paramList.get(i));
	    }
	}
	public List<String> getPhotos() {
	    return this.photos;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	public int getType() {
		return this.type;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	public int getID() {
		return this.id;
	}
	
	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}
	public int getCommnetNum() {
		return this.commentNum;
	}
	
	public void setLikeNum(int likeNum) {
		this.likeNum = likeNum;
	}
	public int getLikeNum() {
		return this.likeNum;
	}
	
	public void setDislikeNum(int dislikeNum) {
		this.dislikeNum = dislikeNum;
	}
	public int getDislikeNum() {
		return this.dislikeNum;
	}
}
