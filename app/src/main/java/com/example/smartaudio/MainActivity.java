package com.example.smartaudio;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.functions.Consumer;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.audioplayer.AudioPlayerManger;
import com.singlecode.audiorecord.project1.AudioRecorder;
import com.singlecode.audiorecord.project1.AudioFileUtils;
import com.singlecode.audiorecord.project1.RecordStreamListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class MainActivity extends AppCompatActivity {
    Button btn_record_start,btn_record_end,btn_play_record,btn_record_pause,btn_record_resume;
    private String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

            }
        });
    }

    private void initView(){
        btn_record_start = findViewById(R.id.btn_record_start);
        btn_record_end = findViewById(R.id.btn_record_end);
        btn_play_record = findViewById(R.id.btn_play_record);
        btn_record_pause = findViewById(R.id.btn_record_pause);
        btn_record_resume = findViewById(R.id.btn_record_resume);
        btn_record_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioRecorder.getInstance().startRecord(null);
            }
        });
        btn_record_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioRecorder.getInstance().pauseRecord();
            }
        });
        btn_record_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = System.currentTimeMillis()+"";
                AudioRecorder.getInstance().createDefaultAudio(MainActivity.this,fileName);
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
                AudioPlayerManger.getInstance().startMediaPlayer(MainActivity.this, AudioFileUtils.getWavFileAbsolutePath(MainActivity.this,fileName),false);
            }
        });
    }
}
