package me.eric.timeCost.sample;

/**
 * @Author: eric
 * @CreateDate: 8/18/21 4:56 PM
 * @Description: java类作用描述
 */
class Test {

    void addLog(){
        TimeLogger.start();
        System.out.println("原始的代码123");
        TimeLogger.end();
    }
}
