package com.berry.oss.common.constant;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Berry_Cooper.
 * @date 2018-03-26
 * Description 公共常量
 */
public final class CommonConstant {

    /**
     * 默认分页查询记录条数
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final String INIT = "INIT";

    public static final String UNKNOWN = "UNKNOWN";

    public static final String SUCCESS = "SUCCESS";

    /**
     * 申请理事费用，固定位5000元
     */
    public static final Integer COUNCIL_FEE = 5000;

    /**
     * 支付类型键值对枚举
     */
    public enum PayType {

        /**
         *
         */
        DONATION("DONATION", "捐赠"),
        TIPS("TIPS", "活动打赏"),
        ACTIVITY_ATTEND("ACTIVITY_ATTEND", "活动报名"),
        APPLY_POSITION("APPLY_POSITION", "申请理事");

        private final String code;

        private final String desc;

        private static Map<String, String> param = new HashMap<>();

        static {
            for (PayType payType : PayType.values()) {
                param.put(payType.getCode(), payType.getDesc());
            }
        }

        public static Map<String, String> getParam() {
            return param;
        }

        PayType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public String getCode() {
            return code;
        }

    }

    public enum AclType {
        /**
         * 权限
         */
        PRIVATE("私有"),
        PUBLIC_READ("公共读"),
        PUBLIC("公开");

        private String name;

        AclType(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    /**
     * 是否有效
     */
    public enum IsActive {
        /**
         * True ,有效
         * False ,无效
         */
        True(1, "有效"), False(2, "无效");

        private Integer code;
        private String msg;

        IsActive(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMeg() {
            return msg;
        }

        public static String getLocaleDesc(Integer value) {
            if (value != null) {
                for (IsActive str : IsActive.values()) {

                    if (str.code.intValue() == value.intValue()) {
                        return str.getMeg();
                    }
                }
            }
            return "";
        }
    }


}