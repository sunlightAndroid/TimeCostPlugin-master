package me.eric.biz.video

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.eric.cost_annotation.TimeCost

class VideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        playVideo()
    }


    @TimeCost()
    fun playVideo(){
        println("测试下")
    }
}