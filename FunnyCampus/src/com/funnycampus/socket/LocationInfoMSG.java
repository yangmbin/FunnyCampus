package com.funnycampus.socket;

import java.io.Serializable;

public class LocationInfoMSG implements Serializable {
	private String username;
	private double latitude, longitude;
	
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return this.username;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLatitude() {
		return this.latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLongitude() {
		return this.longitude;
	}
}
