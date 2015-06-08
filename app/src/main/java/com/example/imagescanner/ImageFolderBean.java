package com.example.imagescanner;


public class ImageFolderBean {
	
	private String folderImgPath;
	private String folderName;
	private String folderPath;
	private String folderSize;
	

	public ImageFolderBean(String folderImgPath, String folderName, String folderPath, String folderSize){
		this.folderImgPath = folderImgPath;
		this.folderName = folderName;
		this.folderPath = folderPath;
		this.folderSize = folderSize;
	}
	
	
	
	public String getFolderImgPath() {
		return folderImgPath;
	}



	public void setFolderImgPath(String folderImgPath) {
		this.folderImgPath = folderImgPath;
	}



	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}


	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public String getFolderSize() {
		return folderSize;
	}
	public void setFolderSize(String folderSize) {
		this.folderSize = folderSize;
	}



	@Override
	public String toString() {
		return "ImageFolderBean [folderImgPath=" + folderImgPath
				+ ", folderName=" + folderName + ", folderPath=" + folderPath
				+ ", folderSize=" + folderSize + "]";
	}

}

