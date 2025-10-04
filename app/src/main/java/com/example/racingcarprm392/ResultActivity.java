package com.example.racingcarprm392; // Thay bằng package của bạn

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.racingcarprm392.Utils.SoundPlayer;

public class ResultActivity extends AppCompatActivity {

    TextView tvResultTitle, tvWinnings;
    Button btnPlayAgain;
    private SoundPlayer soundPlayer;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPlayer.stopSound();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvWinnings = findViewById(R.id.tvWinnings);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        soundPlayer = new SoundPlayer(this);

        Intent intent = getIntent();
        boolean playerWon = intent.getBooleanExtra("PLAYER_WON", false);
        int newBalance = intent.getIntExtra("NEW_BALANCE", 100);
        int totalBet = intent.getIntExtra("TOTAL_BET", 0);
        int payout = intent.getIntExtra("PAYOUT", 0);

        int netGain = payout - totalBet;

        if (netGain > 0) {
            // 🟢 Player wins
            tvResultTitle.setText("BẠN THẮNG CƯỢC!");
            tvResultTitle.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_light));
            String resultText = "Tiền thắng: " + payout + "$\n"
                    + "Tổng cược: " + totalBet + "$\n"
                    + "Lời: +" + netGain + "$";
            tvWinnings.setText(resultText);
            soundPlayer.playSound(R.raw.win_sound, false);
        } else if (netGain == 0) {
            // 🟡 Break even
            tvResultTitle.setText("HÒA CƯỢC!");
            tvResultTitle.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_light));
            String resultText = "Tiền trả: " + payout + "$\n"
                    + "Tổng cược: " + totalBet + "$\n"
                    + "Không lời, không lỗ.";
            tvWinnings.setText(resultText);
            soundPlayer.playSound(R.raw.win_sound, false); // optional, you can skip or use a neutral sound
        } else {
            // 🔴 Player loses
            tvResultTitle.setText("BẠN THUA CƯỢC!");
            tvResultTitle.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            String resultText = "Tiền trả: " + payout + "$\n"
                    + "Tổng cược: " + totalBet + "$\n"
                    + "Lỗ: " + netGain + "$";
            tvWinnings.setText(resultText);
            soundPlayer.playSound(R.raw.lose_sound, false);
        }

        btnPlayAgain.setOnClickListener(v -> {
            Intent backToGame = new Intent(ResultActivity.this, MainActivity.class);
            backToGame.putExtra("UPDATED_BALANCE", newBalance);
            startActivity(backToGame);
            finish();
        });
    }

}