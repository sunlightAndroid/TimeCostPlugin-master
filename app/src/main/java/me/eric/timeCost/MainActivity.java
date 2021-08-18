package me.eric.timeCost;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.eric.cost_annotation.TimeCost;

/**
 * @Author: eric
 * @CreateDate: 8/17/21 7:58 PM
 * @Description: java类作用描述
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testTime();
    }

    @TimeCost(description = "测试首页testTime方法耗时")
    private void testTime() {
        Log.e("TAG","我打印一下看看");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
