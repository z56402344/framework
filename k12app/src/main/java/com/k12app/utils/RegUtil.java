package com.k12app.utils;

/**
 * 说明：正则工具类
 * @author 51talk_yangxt
 * @version 创建时间：2014-11-12 上午11:42:03
 */

public class RegUtil {

	/**
	 * 验证是否是手机号码
	 * 
	 * @param num
	 * @return
	 */
	public static boolean isMobileNum(String num) {
		String reg = "^1[34578][0-9]{9}$";
		boolean matches = num.matches(reg);

		return matches;
	}

	/**
	 * 是否是skype账号
	 * 
	 * @param skypeNum
	 * @return
	 */
	public static boolean isSypeNum(String skypeNum) {
		String skypeReg = "^[A-Za-z]{1}[A-Za-z0-9\\_\\.-]{3,31}$";
		boolean matches = skypeNum.matches(skypeReg);
		return matches;
	}

	/**
	 * 是否是QQ号码
	 * 
	 * @param qqNum
	 * @return
	 */
	public static boolean isQQNum(String qqNum) {
		String qqReg = "[1-9]\\d{4,14}";
		boolean isQQ = qqNum.matches(qqReg);
		return isQQ;
	}
	
	/**
	 * 是不是有关键词
	 * 
	 * @param qqNum
	 * @return
	 */
	public static boolean isKeyWord(String strEn) {
//		i {love#爱} you
		String qqReg = "[1-9]\\d{4,14}";
		boolean isQQ = strEn.matches(qqReg);
		return isQQ;
	}
	
	/**
	 * 正则 中英文,数字和下划线（只能输入以上字符）
	 * @param strEn
	 * @return
	 */
	public static boolean isNewName(String strEn){
		String newNameReg = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$";
		boolean isNewName =  strEn.matches(newNameReg);
		return isNewName;
	}

}
