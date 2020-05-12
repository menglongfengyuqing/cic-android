package com.ztmg.cicmorgan.util;

import java.util.List;

/**
 * 判断list的工具类
 */

public class Lists {
    public Lists() {
    }

    /**
     * 判断是否list.size == 0
     *
     * @param list
     * @return
     */
    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    /**
     * 判断是否list.size > 0
     *
     * @param list
     * @return
     */
    public static boolean notEmpty(List list) {
        return list != null && list.size() > 0;
    }
}
