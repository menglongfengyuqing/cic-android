package com.ztmg.cicmorgan.account.entity;

import java.io.Serializable;

/**
 * 消息和公告
 * @author pc
 *
 */
public class MessageNoticeEntity implements Serializable{
	private String id;
	private String body;
	private String title;
	private String letterType;// 信件类型（1、投资，2、还款，3、充值，4、提现）
	private String userId;
	private String sendTime;
	private String state;// 信件状态（1、未读，2、已读）
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLetterType() {
		return letterType;
	}
	public void setLetterType(String letterType) {
		this.letterType = letterType;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

}
