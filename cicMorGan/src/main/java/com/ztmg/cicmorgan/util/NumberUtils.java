
package com.ztmg.cicmorgan.util;
import java.math.BigDecimal;

/**
 * 
 * 类: NumberUtils <br>
 * 描述: 数字工具类. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年1月6日 下午1:30:19
 */
public class NumberUtils {

	/**
	 * 
	 * 方法: scaleDouble <br>
	 * 描述: 保留两位小数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月6日 下午1:31:18
	 * 
	 * @param dou
	 * @return
	 */
	public static Double scaleDouble(Double dou) {

		BigDecimal b = new BigDecimal(dou);
		// ROUND_HALF_EVEN，银行家舍入法，主要在美国使用，四舍六入，五，分两种情况，如果前一位为奇数，则入位，否则舍去。
		dou = b.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
		return dou;
	}

	/**
	 * 
	 * 方法: scaleThree <br>
	 * 描述: 保留三位小数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月15日 下午5:49:29
	 * 
	 * @param dou
	 * @return
	 */
	public static Double scaleThree(Double dou) {

		BigDecimal b = new BigDecimal(dou);
		// ROUND_HALF_EVEN，银行家舍入法，主要在美国使用，四舍六入，五，分两种情况，如果前一位为奇数，则入位，否则舍去。
		dou = b.setScale(3, BigDecimal.ROUND_HALF_EVEN).doubleValue();
		return dou;
	}

}
