package org.notify.india.model;

import java.io.Serializable;

public class Notification implements Serializable{
	private static final long serialVersionUID = 5601549843318835751L;

	private String id;
	private String message;
	private String type;
	private String destiantionAddress;
	
	public Notification() {
		super();
	}

	public Notification(String id, String message, String type, String destiantionAddress) {
		super();
		this.id = id;
		this.message = message;
		this.type = type;
		this.destiantionAddress = destiantionAddress;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDestiantionAddress() {
		return destiantionAddress;
	}
	public void setDestiantionAddress(String destiantionAddress) {
		this.destiantionAddress = destiantionAddress;
	}

	@Override
	public String toString() {
		return "Notification [id=" + id + ", message=" + message + ", type=" + type + ", destiantionAddress="
				+ destiantionAddress + "]";
	}
	
}
