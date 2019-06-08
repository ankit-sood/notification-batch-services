package org.notify.india.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="notification_master")
public class Notification implements Serializable{
	private static final long serialVersionUID = 5601549843318835751L;
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="notificationSequenceGen")
	@SequenceGenerator(name="notificationSequenceGen")
	@Column(name="notification_id")
	private String id;
	
	@Column(name="message")
	private String message;
	
	@Column(name="type")
	private String type;
	
	@Column(name="destiantion_address")
	private String destiantionAddress;
	
	@Column(name="processed")
	private boolean isProcessed;
	
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
	
	public Notification(String id, String message, String type, String destiantionAddress, boolean isProcessed) {
		super();
		this.id = id;
		this.message = message;
		this.type = type;
		this.destiantionAddress = destiantionAddress;
		this.isProcessed = isProcessed;
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

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	@Override
	public String toString() {
		return "Notification [id=" + id + ", message=" + message + ", type=" + type + ", destiantionAddress="
				+ destiantionAddress + ", isProcessed=" + isProcessed + "]";
	}
	
	
	
}
