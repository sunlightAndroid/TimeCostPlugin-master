package me.eric.util;

import android.util.Log;

/**
 * @Author: eric
 * @CreateDate: 8/24/21 7:13 PM
 * @Description: java类作用描述
 */
public class TimeLogger {

    public static long time = 0;
    public static final String TAG = "TIME_COST";

    public static void start(String name) {
        Log.e(TAG,"<<<<<<<<<<<<<<<<<<<<<start");
        time = System.currentTimeMillis();
        Log.e(TAG,"方法名：" + name);
    }


    public static void end() {
        time = System.currentTimeMillis() - time;
        Log.e(TAG,"方法耗时：" + time + " ms");
        Log.e(TAG,">>>>>>>>>>>>>>>>>>>>>>>end");
    }
}
