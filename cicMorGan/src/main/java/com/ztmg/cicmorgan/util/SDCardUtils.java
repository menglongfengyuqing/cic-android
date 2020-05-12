package com.ztmg.cicmorgan.util;

import android.os.Environment;
import android.os.StatFs;

/**
 *   SDCard帮助类
 * @author hunk
 *
 */
public class SDCardUtils {
	/*
	 * 判断SDCard是否挂载
	 */
	public static boolean isMounted() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/*
	 * 获取SDCard绝对物理路径
	 */
	public static String getPath() {
		if (isMounted()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			return null;
		}
	}

	/*
	 * 获取SDCard的全部的空间大小。返回MB字节
	 */
	@SuppressWarnings("deprecation")
	public static long getSize() {
		if (isMounted()) {
			StatFs fs = new StatFs(getPath());
			long size = fs.getBlockSize();
			long count = fs.getBlockCount();
			return size * count / 1024 / 1024;
		}
		return 0;
	}

	/*
	 * 获取SDCard的剩余的可用空间的大小。返回MB字节
	 */
	@SuppressWarnings("deprecation")
	public static long getFreeSize() {
		if (isMounted()) {
			StatFs fs = new StatFs(getPath());

			long size = fs.getBlockSize();
			long count = fs.getAvailableBlocks();

			return size * count / 1024 / 1024;
		}
		return 0;
	}

}
