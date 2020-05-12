package com.ztmg.cicmorgan.test.entity;

import java.io.Serializable;

/**
 * 测试题目选项
 * @author pc
 *
 */
public class OptionEntity implements Serializable{
	private String id;
	private String name;
	private boolean flag;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
}
