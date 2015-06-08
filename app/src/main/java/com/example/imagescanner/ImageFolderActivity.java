package com.example.imagescanner;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ImageFolderActivity extends Activity {

	
	private ArrayList<ImageFolderBean> dataItems;
	private ListView listview;
	private ProgressBar bar;
	private ImageFolderAdapter adapter;
	private int choose_max_sum = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_folder);
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
			choose_max_sum = bundle.getInt(ImageChooserConstants.INTENT_EXTRA.CHOOSEN_MAX_SUM,1);
		listview = (ListView) findViewById(R.id.listview);
		bar = (ProgressBar) findViewById(R.id.progress);
		dataItems = new ArrayList<ImageFolderBean>();
		adapter = new ImageFolderAdapter(this, dataItems);
		listview.setAdapter(adapter);
		if(dataItems.isEmpty()){
			loadData();
		}
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(ImageFolderActivity.this,ImageGridActivity.class);
				intent.putExtra(ImageChooserConstants.INTENT_EXTRA.FOLDER_PATH, dataItems.get(position).getFolderPath());
				intent.putExtra(ImageChooserConstants.INTENT_EXTRA.CHOOSEN_MAX_SUM, choose_max_sum);
				ImageFolderActivity.this.startActivityForResult(intent, ImageChooserConstants.REQUEST_CODE.REQUEST_IMAGE_GRID);
			}
		});
		
	}
	
	public void loadData(){
		final Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				adapter.notifyDataSetChanged();
				bar.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
			}
			
		};
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				ArrayList<ImageFolderBean> tempList = BitmapUtils.getImageFolder(ImageFolderActivity.this);
				for(ImageFolderBean bean: tempList)
					dataItems.add(bean);
				handler.sendEmptyMessage(1);
			}
		}).start();
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case ImageChooserConstants.REQUEST_CODE.REQUEST_IMAGE_GRID:
			if(data == null)
				return;
			Bundle bundle = data.getExtras();
			ArrayList<String> urilist = (ArrayList<String>) bundle.getSerializable(ImageChooserConstants.INTENT_EXTRA.URI_LIST);
			System.out.println("selected uri = " + urilist.toString());
			this.setResult(RESULT_OK, data);
			finish();
			break;

		default:
			break;
		}
	}



}
