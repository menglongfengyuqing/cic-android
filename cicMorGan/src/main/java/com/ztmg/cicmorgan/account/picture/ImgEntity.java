package com.ztmg.cicmorgan.account.picture;

import java.io.Serializable;

import android.net.Uri;

public class ImgEntity  implements Serializable{

	private String id;
	private String name;
	private String path;
	private  String uri;
	private boolean bln;

	public ImgEntity(String id,String name,String path,String  uri,boolean bln) {
		this.id=id;
		this.name=name;
		this.path=path;
		this.uri=uri;
		this.bln=bln;
	}

	public ImgEntity(){

	}

	public boolean isBln() {
		return bln;
	}


	public void setBln(boolean bln) {
		this.bln = bln;
	}


	public String  getUri() {
		return uri;
	}

	public void setUri(String  uri) {
		this.uri = uri;
	}

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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

}
