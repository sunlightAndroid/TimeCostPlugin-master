package me.eric.timeCost;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.eric.cost_annotation.TimeCost;

/**
 * @Author: eric
 * @CreateDate: 8/18/21 7:37 PM
 * @Description: java类作用描述
 */
class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TimeCost()
    void login(){
    }
}
