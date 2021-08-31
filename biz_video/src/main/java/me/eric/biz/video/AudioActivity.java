package me.eric.biz.video;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.eric.cost_annotation.TimeCost;

/**
 * @Author: eric
 * @CreateDate: 8/24/21 7:31 PM
 * @Description: java类作用描述
 */
public class AudioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        play();
    }

    @TimeCost()
    private void play(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("测试一下");
    }
}
