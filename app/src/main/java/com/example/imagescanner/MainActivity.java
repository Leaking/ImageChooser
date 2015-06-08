package com.example.imagescanner;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity {

	ImageView img1;
	ImageView img2;
	ImageView img3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		img1 = (ImageView) findViewById(R.id.img1);
		img2 = (ImageView) findViewById(R.id.img2);
		img3 = (ImageView) findViewById(R.id.img3);
		img1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						ImageFolderActivity.class);
				intent.putExtra(
						ImageChooserConstants.INTENT_EXTRA.CHOOSEN_MAX_SUM, 1);
				MainActivity.this
						.startActivityForResult(
								intent,
								ImageChooserConstants.REQUEST_CODE.REQUEST_IMAGE_FOLDER);
			}
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case ImageChooserConstants.REQUEST_CODE.REQUEST_IMAGE_FOLDER:
			if (data == null)
				return;
			Bundle bundle = data.getExtras();
			ArrayList<String> uriList = (ArrayList<String>) bundle
					.getSerializable(ImageChooserConstants.INTENT_EXTRA.URI_LIST);
			img1.setImageBitmap(BitmapUtils.decodeBitmapFromFile(uriList.get(0), 100, 100));
//			img2.setImageBitmap(BitmapUtils.decodeBitmapFromFile(uriList.get(1), 100, 100));
//			img3.setImageBitmap(BitmapUtils.decodeBitmapFromFile(uriList.get(2), 100, 100));
			
			break;
		}

	}

}
