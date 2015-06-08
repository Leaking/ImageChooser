package com.example.imagescanner;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore.Images.Media;

public class BitmapUtils {

	
	public static ArrayList<ImageFolderBean> getImageFolder(Context context) {
		ArrayList<ImageFolderBean> list = new ArrayList<ImageFolderBean>();
		ArrayList<String> folderList = new ArrayList<String>();
		String imgUrl;
		String folderName;
		String folderSize = "";
		Cursor cursor = context.getContentResolver().query(
				Media.EXTERNAL_CONTENT_URI, null, null, null, null);

		while (cursor.moveToNext()) {
			byte[] data = cursor.getBlob(cursor.getColumnIndex(Media.DATA));
			imgUrl = new String(data, 0, data.length - 1);
			File file = new File(imgUrl);
			folderName = file.getParentFile().getName();
			if (false == folderList.contains(folderName)) {
				String folderPath = file.getParentFile().getAbsolutePath();
				folderSize = getGalleryBitmapsIn_A_Folder(folderPath).size()
						+ "";
				folderList.add(folderName);
				ImageFolderBean bean = new ImageFolderBean(imgUrl,
						folderName, file.getParentFile().getAbsolutePath(),
						folderSize);
				list.add(bean);
			}
		}
		return list;
	}
	
	public static ArrayList<String> getGalleryBitmapsIn_A_Folder(String path) {
		ArrayList<String> list = new ArrayList<String>();
		String tempPath;
		File file = new File(path);
		for (File imgFile : file.listFiles()) {
			tempPath = imgFile.getAbsolutePath();
			if (isImgFile(tempPath))
				list.add(0, tempPath);
		}

		return list;
	}
	
	public static boolean isImgFile(String name) {
		if (name.endsWith("jpg") || name.endsWith("jpeg")
				|| name.endsWith("png") || name.endsWith("icon")) {
			return true;
		}
		return false;
	}
	
	
	
	public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
		if (null == bitmap || edgeLength <= 0) {
			return null;
		}

		Bitmap result = bitmap;
		int widthOrg = bitmap.getWidth();
		int heightOrg = bitmap.getHeight();

		if (widthOrg > edgeLength && heightOrg > edgeLength) {
			int longerEdge = (int) (edgeLength * Math.max(widthOrg, heightOrg) / Math
					.min(widthOrg, heightOrg));
			int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
			int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
			Bitmap scaledBitmap;

			try {
				scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
						scaledHeight, true);
			} catch (Exception e) {
				return null;
			}

			int xTopLeft = (scaledWidth - edgeLength) / 2;
			int yTopLeft = (scaledHeight - edgeLength) / 2;

			try {
				result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft,
						edgeLength, edgeLength);
				scaledBitmap.recycle();
			} catch (Exception e) {
				return null;
			}
		}

		return result;
	}
	
	
	
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
		
	
	
	public static Bitmap decodeBitmapFromFile(String filePath, int reqWidth,
			int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		options.inJustDecodeBounds = false;
		return 	BitmapFactory.decodeFile(filePath, options);

	}
	
}
