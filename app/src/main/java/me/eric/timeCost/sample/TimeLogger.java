package me.eric.timeCost.sample;

/**
 * <pre>
 *     author : eric
 *     time   : 2021/08/14
 *     desc   :
 *     version:
 * </pre>
 */

public class TimeLogger {
    public static long time = 0;

    public static void start() {
        time = System.currentTimeMillis();
    }

    public static void end() {
        time = System.currentTimeMillis() - time;
        System.out.println("方法耗时：" + time);
    }
}
