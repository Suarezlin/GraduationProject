package com.suarezlin.utils;

public class UUID {

    /**
     * 根据字符串生成全局唯一 id
     * @param name 字符串
     * @return 生成的 uuid
     */
    public static String generateUUIDFromName(String name) {
        String uuid = java.util.UUID.nameUUIDFromBytes(name.getBytes()).toString().replace("-","").toUpperCase();
        return uuid;
    }

    /**
     * 生成随机 uuid
     * @return 生成的 uuid
     */
    public static String generateUUID() {
        String uuid = java.util.UUID.randomUUID().toString().replace("-","").toUpperCase();
        return uuid;
    }

    public static void main(String[] args) {
        for (int i=0; i<10; i++) {
            System.out.println(UUID.generateUUID());
        }
    }

}
