package com.example.imagescanner;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageFolderAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<ImageFolderBean> dataItems;
	LayoutInflater listContainer;
	
	static class ListItemView {
		public ImageView iamge;
		public TextView folderName;
		public TextView folderSize;
	}
	
	public ImageFolderAdapter(Context context,ArrayList<ImageFolderBean> dataItems){
		this.context = context;
		this.dataItems = dataItems;
		System.out.println("adapter size = " + dataItems.size());
		this.listContainer = LayoutInflater.from(context);

	}
	
	
	@Override
	public int getCount() {
		return dataItems.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;
		if (convertView == null) {
			convertView = listContainer.inflate(R.layout.item_lv_image_folder, null);
			listItemView = new ListItemView();
			listItemView.iamge = (ImageView) convertView
					.findViewById(R.id.photofolder_img);
			listItemView.folderName = (TextView) convertView
					.findViewById(R.id.photofolder_name);
			listItemView.folderSize = (TextView) convertView
					.findViewById(R.id.photofolder_size);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		Bitmap bm = BitmapUtils.decodeBitmapFromFile(dataItems.get(position).getFolderImgPath(), 100, 100);
		listItemView.iamge.setImageBitmap(bm);
		listItemView.folderName.setText(dataItems.get(position).getFolderName());
		listItemView.folderSize.setText(dataItems.get(position).getFolderSize());
		return convertView;
	}

}
