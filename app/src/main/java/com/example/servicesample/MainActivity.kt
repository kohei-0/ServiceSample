package com.example.servicesample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onPlayButtonClick(view: View){
        //intet objを作成
        //TODO ::の意味再確認
        val intent = Intent(this@MainActivity, SoundManageService::class.java)
        //serviceを起動
        startService(intent)
        //再生ボタンをタップ不可に、停止ボタンをタップ可に変更
        val btPlay = findViewById<Button>(R.id.btPlay)
        val btStop = findViewById<Button>(R.id.btStop)
        btPlay.isEnabled = false
        btStop.isEnabled = true
    }

    fun onStopButtonClick(view: View){
        //intet objを作成
        val intent = Intent(this@MainActivity, SoundManageService::class.java)
        //serviceを停止
        stopService(intent)
        //再生ボタンをタップ不可に、停止ボタンをタップ可に変更
        val btPlay = findViewById<Button>(R.id.btPlay)
        val btStop = findViewById<Button>(R.id.btStop)
        btPlay.isEnabled = true
        btStop.isEnabled = false
    }

}

