package com.example.racingcarprm392; // Thay bằng package của bạn

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.racingcarprm392.Utils.SoundPlayer;


public class LoginActivity extends AppCompatActivity {

    EditText etUsername;
    Button btnLogin;

    SoundPlayer soundPlayer;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPlayer.stopSound();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        btnLogin = findViewById(R.id.btnLogin);
        soundPlayer =  new SoundPlayer(this);
        soundPlayer.playSound(R.raw.login_music, true);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();

            if (username.trim().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập tên!", Toast.LENGTH_SHORT).show();
            } else {
                soundPlayer.stopSound();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ConstraintLayout rootLayout = findViewById(R.id.root_layout_login); // Dùng đúng id của layout
        AnimationDrawable animationDrawable = (AnimationDrawable) rootLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }
}