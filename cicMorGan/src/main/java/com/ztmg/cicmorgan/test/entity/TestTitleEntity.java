package com.ztmg.cicmorgan.test.entity;

import java.io.Serializable;
import java.util.List;
/**
 * 测试题目
 * @author pc
 *
 */
public class TestTitleEntity implements Serializable{
	private String id;
	private String name;
	private List<OptionEntity> testOption;
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
	public List<OptionEntity> getTestOption() {
		return testOption;
	}
	public void setTestOption(List<OptionEntity> testOption) {
		this.testOption = testOption;
	}

}
