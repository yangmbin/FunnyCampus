package com.funnycampus.socket;
import java.io.Serializable;
import java.util.Set;


public class RegisterMSG implements Serializable {
	private String name;
	private String password1, password2;
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	public void setPassword1(String password1) {
		this.password1 = password1;
	}
	public String getPassword1() {
		return this.password1;
	}
	
	public void setPassword2(String password2) {
		this.password2 = password2;
	}
	public String getPassword2() {
		return this.password2;
	}
	
}
