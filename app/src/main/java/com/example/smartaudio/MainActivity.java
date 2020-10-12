package com.example.smartaudio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.audioplayer.AudioPlayerManger;
import com.singlecode.audiorecord.project1.AudioRecorder;
import com.singlecode.audiorecord.project1.FileUtils;
import com.singlecode.audiorecord.project1.RecordStreamListener;

public class MainActivity extends AppCompatActivity {
    Button btn_record_start,btn_record_end,btn_play_record;
    private String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        btn_record_start = findViewById(R.id.btn_record_start);
        btn_record_end = findViewById(R.id.btn_record_end);
        btn_play_record = findViewById(R.id.btn_play_record);
        btn_record_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = System.currentTimeMillis()+"";
                AudioRecorder.getInstance().createDefaultAudio(fileName);
                AudioRecorder.getInstance().startRecord(new RecordStreamListener() {
                    @Override
                    public void recordOfByte(byte[] data, int begin, int end) {

                    }
                });
            }
        });
        btn_record_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioRecorder.getInstance().stopRecord();

            }
        });
        btn_play_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioPlayerManger.getInstance().startMediaPlayer(MainActivity.this, FileUtils.getWavFileAbsolutePath(fileName),false);
            }
        });
    }
}
