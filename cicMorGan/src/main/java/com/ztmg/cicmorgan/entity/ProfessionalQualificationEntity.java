package com.ztmg.cicmorgan.entity;
import java.io.Serializable;

public class ProfessionalQualificationEntity implements Serializable {
	private String id;
	private String time;// 时间
	private String unit;// 单位
	private String name;// 名称
	private String zhengshu;// 证书原图
	private String zhengshu_thumb;// 证书缩略图
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getZhengshu() {
		return zhengshu;
	}
	public void setZhengshu(String zhengshu) {
		this.zhengshu = zhengshu;
	}
	public String getZhengshu_thumb() {
		return zhengshu_thumb;
	}
	public void setZhengshu_thumb(String zhengshu_thumb) {
		this.zhengshu_thumb = zhengshu_thumb;
	}
	@Override
	public String toString() {
		return "ProfessionalQualificationEntity [id=" + id + ", time=" + time
				+ ", unit=" + unit + ", name=" + name + ", zhengshu="
				+ zhengshu + ", zhengshu_thumb=" + zhengshu_thumb + "]";
	}
	
	
}
