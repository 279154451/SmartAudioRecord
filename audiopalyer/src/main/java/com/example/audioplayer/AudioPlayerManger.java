package com.example.audioplayer;

/**
 * 创建时间：2020/8/10
 * 创建人：singleCode
 * 功能描述：
 **/

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;


/**
 * 参考博客链接 http://blog.csdn.net/u011516685/article/details/50510902
 * 这里注意使用stop和prepareAsync是耗时操作需要在线程里面执行，同时由于stop多次出错之后之前的MediaPlayer不能够被正常使用,所以这里才会采取每次新建一个MediaPlayer实例
 * 这里PlayMusicCompleteListener没有使用弱引用,因为在应用程序里面会存在被GC掉,所以这里使用Handler来避免内存泄露的方式实现
 * Created by Tangxb on 2016/9/1.
 */
public class AudioPlayerManger {
    private static AudioPlayerManger instance;
    private MediaPlayer mediaPlayer;
    private HandlerThread playHandlerThread;
    private Handler playHandler;
    /**
     * 播放
     */
    private static final int PLAY = 101;
    /**
     * 停止
     */
    private static final int STOP = 102;

    /**
     * 暂停
     */
    private static final int PAUSE = 103;

    /**
     * 继续
     */
    private static final int RESUME = 104;
    /**
     * 释放
     */
    private static final int RELEASE = 105;
    /**
     * 界面不可见
     */
    private static final int ONSTOP = 106;
    private Handler handler;
    private PlayMusicCompleteListener listener;
    private Context context;
    private Object url;
    private boolean Looping = true;
    /**
     * 播放一首完成的回调
     */
    public interface PlayMusicCompleteListener {
        void playMusicComplete(int position);
    }

    private void createHandlerThreadIfNeed() {
        if (playHandlerThread == null) {
            playHandlerThread = new HandlerThread("playMediaThread");
            playHandlerThread.start();
        }
    }

    private void createHandlerIfNeed() {
        if (playHandler == null) {
            playHandler = new Handler(playHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case PLAY:
                            playMusic01();
                            break;
                        case PAUSE:
                            pauseMediaPlayer1();
                            break;
                        case RESUME:
                            resumeMediaPlayer1();
                            break;
                        case STOP:
                            stopMediaPlayer02();
                            break;
                        case RELEASE:
                            releaseMediaPlayer02();
                            break;
                        case ONSTOP:
                            stopMediaPlayer03();
                            break;
                    }
                }
            };
        }
    }

    private void createPlayerIfNeed() {
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
    }

    private AudioPlayerManger() {
        handler = new Handler(Looper.getMainLooper());
        createHandlerThreadIfNeed();
        createHandlerIfNeed();
    }

    public static AudioPlayerManger getInstance() {
        if (instance == null) {
            instance = new AudioPlayerManger();
        }
        return instance;
    }

    /**
     * 开始播放
     * @param context
     * @param url
     * @param looping
     */
    public void startMediaPlayer(Context context,Object url,boolean looping) {
        this.url = url;
        this.Looping = looping;
        this.context = context.getApplicationContext();
        playHandler.sendEmptyMessageDelayed(PLAY, 0L);
    }

    /**
     * 暂停
     */
    public void pauseMediaPlayer(){
        playHandler.sendEmptyMessage(PAUSE);
    }

    /**
     * 继续
     */
    public void resumeMediaPlayer(){
        playHandler.sendEmptyMessage(RESUME);
    }

    /**
     * 停止
     */
    public void releaseMediaPlayer() {
        playHandler.sendEmptyMessage(RELEASE);
    }

    private void playMusic01() {
        createPlayerIfNeed();
        playMusic02();
    }

    private void playMusic02() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            if(url instanceof Integer){
                AssetFileDescriptor afd = context.getResources().openRawResourceFd((int)url);
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
            }else if(url instanceof String){
                mediaPlayer.setDataSource((String)url);
            }else if(url instanceof AssetFileDescriptor){
                AssetFileDescriptor afd = (AssetFileDescriptor) url;
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
            }else if(url instanceof Uri){
                mediaPlayer.setDataSource(context,(Uri)url);
            }
            mediaPlayer.setLooping(Looping);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer player) {
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer player) {
                    playMusicComplete();
                    stopMediaPlayer();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer player, int what, int extra) {
                    stopMediaPlayer();
                    return false;
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void stopMediaPlayer() {
        playHandler.sendEmptyMessage(STOP);
    }
    /**
     * 这里需要注意设置setOnPreparedListener和setOnCompletionListener为null,因为不设置它会调用上一个已经设置好的回调(经过测试,请注意)
     */
    private void stopMediaPlayer02() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(null);
            mediaPlayer.setOnCompletionListener(null);
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) {

            }
        }
        mediaPlayer = null;
    }

    /**
     * 这里需要注意设置setOnPreparedListener和setOnCompletionListener为null,因为不设置它会调用上一个已经设置好的回调(经过测试,请注意)
     */
    private void stopMediaPlayer03() {
        stopMediaPlayer02();
        playMusicComplete();
    }

    /**
     * 播放完成,需要在主线程里面更新UI
     */
    private void playMusicComplete() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.playMusicComplete(0);
                }
            }
        });
    }

    private void releaseMediaPlayer02() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (IllegalStateException e) {

            }
        }
        mediaPlayer = null;
        // 避免内存泄露
        listener = null;
        handler.removeCallbacksAndMessages(null);
    }

   private void pauseMediaPlayer1() {
        mediaPlayer.pause();
    }

    private void resumeMediaPlayer1() {
        mediaPlayer.start();
    }
}