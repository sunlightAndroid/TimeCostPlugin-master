package me.eric.util;

/**
 * @Author: eric
 * @CreateDate: 8/24/21 7:13 PM
 * @Description: java类作用描述
 */
public class TimeLogger {

    public static long time = 0;

    public static void start(String name) {
        System.out.println("<<<<<<<<<<<<<<<<<<<<<start");
        time = System.currentTimeMillis();
        System.out.println("方法名：" + name);
    }


    public static void end() {
        time = System.currentTimeMillis() - time;
        System.out.println("方法耗时：" + time + " ms");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>end");
    }
}
