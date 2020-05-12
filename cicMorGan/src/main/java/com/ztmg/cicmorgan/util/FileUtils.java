package com.ztmg.cicmorgan.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * File工具类
 * 
 * @author hunk
 * 
 */
public class FileUtils {
	private static String TAG = "FileUtils";

	/*
	 * 将bitmap保存指定的路径下
	 */
	public static void saveBitmap(Bitmap bm, String dirPath, String picName) {

		try {
			File dir = CreateDir(dirPath);
			File f = new File(dir.getAbsolutePath(), picName);
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			if (picName.contains(".PNG") || picName.contains(".png")) {
				bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			} else {
				bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			}
			out.flush();
			out.close();
			Log.i(TAG, "保存成功");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 将文件（byte[]）保存指定的路径下
	 */
	public static boolean saveFile(byte[] data, String dirPath, String filename) {
		BufferedOutputStream bos = null;
		File file = CreateDir(dirPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			bos = new BufferedOutputStream(new FileOutputStream(new File(file,
					filename)));
			bos.write(data, 0, data.length);
			bos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * 已知文件的路径,获取到该文件
	 */
	public static byte[] readFile(String filepath) {
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		File file = new File(filepath);
		if (file.exists()) {
			try {
				baos = new ByteArrayOutputStream();
				bis = new BufferedInputStream(new FileInputStream(file));
				byte[] buffer = new byte[1024 * 8];
				int c = 0;
				while ((c = bis.read(buffer)) != -1) {
					baos.write(buffer, 0, c);
					baos.flush();
				}
				return baos.toByteArray();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (bis != null) {
						bis.close();
						baos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/*
	 * 检查文件是否存在
	 */
	public static boolean exists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/*
	 * 删除文件
	 */
	public static void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
		}
	}

	/*
	 * 创建目录
	 */
	public static File CreateDir(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/*
	 * 删除该目录
	 */
	public static void deleteDir(File dir) {

		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		for (File file : dir.listFiles()) {
			// 删除所有文件
			if (file.isFile()) {

				file.delete();
			} else if (file.isDirectory()) {
				// 递规的方式删除文件夹
				deleteDir(file);
			}
		}
		// 删除目录本身
		dir.delete();
	}
}
