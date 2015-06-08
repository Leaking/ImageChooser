package com.example.imagescanner;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;

public class ImageGridActivity extends Activity implements OnClickListener {

	
	private TextView cancel;
	private TextView confirm;
	private GridView gridview;
	private ArrayList<GridImageBean> dataItems = new ArrayList<GridImageBean>();
	private ImageGridAdapter adapter;
	private String path;
	private int choose_max_sum = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_grid);
		gridview = (GridView) findViewById(R.id.gridview);
		cancel = (TextView) findViewById(R.id.cancel);
		confirm = (TextView) findViewById(R.id.confirm);
		Bundle bundle = getIntent().getExtras();
		path = bundle.getString(ImageChooserConstants.INTENT_EXTRA.FOLDER_PATH);
		choose_max_sum = bundle.getInt(ImageChooserConstants.INTENT_EXTRA.CHOOSEN_MAX_SUM,1);
		adapter = new ImageGridAdapter(this, gridview,choose_max_sum, dataItems, R.layout.item_gd__gallery_photo);;
		gridview.setAdapter(adapter);
		updateOKbtn(0);
		if(dataItems.isEmpty()){
			loadData();
		}
		confirm.setOnClickListener(this);
	}
	
	
	public void loadData() {
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					adapter.notifyDataSetChanged();
				}
			}

		};
		new Thread(new Runnable() {

			@Override
			public void run() {
				ArrayList<String> tempList = BitmapUtils
						.getGalleryBitmapsIn_A_Folder(path);
				
				for (String bean : tempList) {
					dataItems.add(new GridImageBean(bean, false));
				}

				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void updateOKbtn(int count){
		if(count == 0){
			confirm.setVisibility(View.GONE);
		}else{
			confirm.setVisibility(View.VISIBLE);;
			confirm.setText("确定(" + count + ")");
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirm:
			Bundle bundle = new Bundle();
			bundle.putSerializable(ImageChooserConstants.INTENT_EXTRA.URI_LIST, adapter.getChooseURIlist());
			Intent intent = new Intent();
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish();
			break;

		default:
			break;
		}
	}
	
	
}
