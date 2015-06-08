package com.example.imagescanner;

public class GridImageBean {
	private String path;
	private boolean check;
	private boolean clickable;

	public GridImageBean(String path, boolean check) {
		this.path = path;
		this.check = check;
		this.clickable = false;
	}

	public boolean isClickable() {
		return clickable;
	}

	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}
	@Override
	public String toString(){
		return this.path + " " + this.check + "\n";
	}
}
