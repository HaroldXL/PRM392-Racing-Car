package com.example.racingcarprm392.Utils;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {
    private Context context;
    private MediaPlayer mediaPlayer;
    public SoundPlayer(Context context){
        this.context = context;
    }
    public void playSound(int soundId, boolean loop) {
        // Kiểm tra xem file âm thanh có tồn tại không trước khi phát
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(context, soundId);
            mediaPlayer.setLooping(loop);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        } catch (Exception e) {
            e.printStackTrace(); // Bỏ qua nếu file âm thanh không tồn tại
        }
    }

    public void stopSound(){
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }catch (Exception e) {
            e.printStackTrace(); // just in case, avoid crash
        }
    }
}
