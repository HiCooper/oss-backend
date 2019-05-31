package com.berry.oss.common.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2018-12-04 22:26
 * fileName：PinyinUtils
 * Use：
 */
public class PinyinUtils {


    /**
     * Default Format 默认输出格式 小或大写、 没有音调数字、u显示
     *
     * @return
     */
    private static HanyuPinyinOutputFormat getDefaultFormat() {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        // 小写
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 没有音调数字
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        return format;
    }

    /**
     * * 汉字转为拼音
     * * @param chinese
     * * @return
     */
    public static String getPinyin(String chinese) {
        StringBuilder pinyinStr = new StringBuilder();
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = getDefaultFormat();
        for (char aNewChar : newChar) {
            if (Character.toString(aNewChar).matches("[\\u4E00-\\u9FA5]+")) {
                try {
                    pinyinStr.append(PinyinHelper.toHanyuPinyinStringArray(aNewChar, defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinStr.append(aNewChar);
            }
        }
        return pinyinStr.toString();
    }

    /**
     * 返回中文的首字母
     *
     * @param chinese
     * @return
     */
    public static String getPinYinHeadChar(String chinese) {
        StringBuilder pinyin = new StringBuilder();
        if (chinese != null && !chinese.trim().equalsIgnoreCase("")) {
            for (int j = 0; j < chinese.length(); j++) {
                char word = chinese.charAt(j);
                String[] pinyinArray = PinyinHelper
                        .toHanyuPinyinStringArray(word);
                if (pinyinArray != null) {
                    pinyin.append(pinyinArray[0].charAt(0));
                } else {
                    pinyin.append(word);
                }
            }
        }
        return pinyin.toString();
    }

}
