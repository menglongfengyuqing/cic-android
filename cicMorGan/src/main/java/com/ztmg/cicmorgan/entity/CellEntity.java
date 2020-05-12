package com.ztmg.cicmorgan.entity;

import java.io.Serializable;

public class CellEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int x;
	private int y;

	public CellEntity(int x, int y) {
		this.x=x;
		this.y=y;
	}
public CellEntity() {
	// TODO Auto-generated constructor stub
}
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	
	@Override
	public String toString() {
		return "CellEntity [x=" + x + ", y=" + y + "]";
	}

}
