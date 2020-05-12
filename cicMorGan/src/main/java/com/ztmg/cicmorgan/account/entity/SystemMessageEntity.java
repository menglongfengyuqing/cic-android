package com.ztmg.cicmorgan.account.entity;

import java.io.Serializable;
/**
 * 系统消息
 * @author pc
 *
 */
public class SystemMessageEntity implements Serializable{
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
