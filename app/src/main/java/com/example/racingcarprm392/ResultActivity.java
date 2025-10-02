package com.example.racingcarprm392; // Thay bằng package của bạn

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ResultActivity extends AppCompatActivity {

    TextView tvResultTitle, tvWinnings;
    Button btnPlayAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvWinnings = findViewById(R.id.tvWinnings);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);

        Intent intent = getIntent();
        boolean playerWon = intent.getBooleanExtra("PLAYER_WON", false);
        int newBalance = intent.getIntExtra("NEW_BALANCE", 100);
        int totalBet = intent.getIntExtra("TOTAL_BET", 0);
        int payout = intent.getIntExtra("PAYOUT", 0);

        if (playerWon) {
            tvResultTitle.setText("BẠN THẮNG CƯỢC!");
            tvResultTitle.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_light));
            // Tính toán lời/lỗ
            int netGain = payout - totalBet;
            String resultText = "Tiền thắng: " + payout + "$\n"
                    + "Tổng cược: " + totalBet + "$\n"
                    + "Lời/Lỗ: " + (netGain >= 0 ? "+" : "") + netGain + "$";
            tvWinnings.setText(resultText);
            playSound(R.raw.win_sound);
        } else {
            tvResultTitle.setText("BẠN THUA HẾT CƯỢC!");
            tvResultTitle.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            tvWinnings.setText("Bạn đã mất " + totalBet + "$. Chúc may mắn lần sau.");
            playSound(R.raw.lose_sound);
        }

        btnPlayAgain.setOnClickListener(v -> {
            Intent backToGame = new Intent(ResultActivity.this, MainActivity.class);
            backToGame.putExtra("UPDATED_BALANCE", newBalance);
            startActivity(backToGame);
            finish();
        });
    }

    private void playSound(int soundId) {
        // Kiểm tra xem file âm thanh có tồn tại không trước khi phát
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, soundId);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        } catch (Exception e) {
            e.printStackTrace(); // Bỏ qua nếu file âm thanh không tồn tại
        }
    }
}