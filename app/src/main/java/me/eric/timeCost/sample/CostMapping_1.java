package me.eric.timeCost.sample;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: eric
 * @CreateDate: 8/18/21 8:18 PM
 * @Description: java类作用描述
 */
public class CostMapping_1 {

    public static Map<String, String> get() {
        Map<String, String> map = new HashMap<>();
        map.put("me.eric.timeCost.MainActivity", "testTime");
        //...
        return map;
    }
}
