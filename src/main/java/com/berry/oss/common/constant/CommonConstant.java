package com.berry.oss.common.constant;


import java.util.Arrays;

/**
 * @author Berry_Cooper.
 * @date 2018-03-26
 * Description 公共常量
 */
public final class CommonConstant {

    public enum AclType {
        /**
         * 权限
         */
        EXTEND_BUCKET("继承 Bucket"),
        PRIVATE("私有"),
        PUBLIC_READ("公共读"),
        PUBLIC_READ_WRITE("公共读写");

        private final String desc;

        public static final String ALL_NAME = Arrays.toString(AclType.values());

        AclType(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }
}