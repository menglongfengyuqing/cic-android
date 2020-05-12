package com.ztmg.cicmorgan.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;

public class DownLoadItemImageAsync extends AsyncTask<String, Integer, Bitmap> {
		
		private String fILE_DIR;
		
		
		protected Bitmap doInBackground(String ... params) {
		Bitmap bitmap = getBitmap();
		if (bitmap != null) {
		    return bitmap;
		}
		return getItemPic(params[0]);
		}
		@Override
		protected void onPostExecute(Bitmap result) {
		
		
		}
		
		public Bitmap getItemPic(String picUrl) {
		URL url = null;
		URLConnection conn = null;
		InputStream in = null;
		Bitmap itemBitmap = null;
		try {
		    url = new URL(picUrl);
		    conn = url.openConnection();
		    conn.setConnectTimeout(3 * 1000);
		    conn.connect();
		
		    in = conn.getInputStream();
		
		    itemBitmap = BitmapFactory.decodeStream(in);
		    storeInSD(itemBitmap);
		} catch (MalformedURLException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    if (url != null) {
		        url = null;
		    }
		    if (conn != null) {
		        conn = null;
		    }
		    if (in != null) {
		        try {
		            in.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}
		return itemBitmap;
		}
		
		private void storeInSD(Bitmap bitmap) {
			fILE_DIR = Environment.getExternalStorageDirectory().getParent()+"/MyImage1";
		File file = new File(fILE_DIR);
		if (!file.exists()) {
		    file.mkdir();
		}
		File imageFile = new File(file,  "test.jpg");
		try {
		    imageFile.createNewFile();
		    FileOutputStream fos = new FileOutputStream(imageFile);
		    bitmap.compress(CompressFormat.JPEG, 50, fos);
		    fos.flush();
		    fos.close();
		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		}
		
		private Bitmap getBitmap() {
			
			fILE_DIR = Environment.getExternalStorageDirectory().getParent()+"/MyImage1";
		File imageFile = new File(fILE_DIR,  "test.jpg");
		Bitmap bitmap = null;
		if (imageFile.exists()) {
		    try {
		        bitmap = BitmapFactory.decodeStream(new FileInputStream(
		                imageFile));
		    } catch (FileNotFoundException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		}
		
		return bitmap;
		}
}
