package com.berry.oss.common.constant;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    public enum ActionType {
        /**
         * 权限
         */
        ONLY_READ(1, "只读"),
        READ_WRITE(2, "读写"),
        FULL_ACCESS(3, "完全控制"),
        DENY(4, "拒绝访问");

        private final int code;
        private final String desc;

        ActionType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        private static Map<Integer, String> data = new HashMap<>(4);

        static {
            for (ActionType item : ActionType.values()) {
                data.put(item.code, item.desc);
            }
        }

        public static boolean checkByCode(int code) {
            return data.get(code) != null;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}