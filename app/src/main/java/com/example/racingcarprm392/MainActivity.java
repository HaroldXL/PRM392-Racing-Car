package com.example.racingcarprm392; // Thay bằng package của bạn

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView car1, car2, car3;
    Button btnStartRace, btnTopUp;
    TextView tvBalance;
    EditText etBetAmount;
    RadioGroup rgCars;
    View controlPanel;

    private int playerBalance = 100;
    private int betAmount = 0;
    private int carChosen = 0;
    private boolean isRaceRunning = false;
    private float initialCarX; // Lưu vị trí X ban đầu của xe

    private ActivityResultLauncher<Intent> topUpLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Ánh xạ views
        car1 = findViewById(R.id.car1);
        car2 = findViewById(R.id.car2);
        car3 = findViewById(R.id.car3);
        btnStartRace = findViewById(R.id.btnStartRace);
        btnTopUp = findViewById(R.id.btnTopUp);
        tvBalance = findViewById(R.id.tvBalance);
        etBetAmount = findViewById(R.id.etBetAmount);
        rgCars = findViewById(R.id.rgCars);
        controlPanel = findViewById(R.id.control_panel);

        // Lấy vị trí ban đầu của xe để reset
        car1.post(() -> initialCarX = car1.getX());

        // Nhận lại số dư nếu người dùng chơi lại
        Intent intent = getIntent();
        if (intent.hasExtra("UPDATED_BALANCE")) {
            playerBalance = intent.getIntExtra("UPDATED_BALANCE", 100);
        }
        updateBalanceText();

        // Khởi tạo Launcher để nhận kết quả từ TopUpActivity
        topUpLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        int topUpAmount = result.getData().getIntExtra("TOPUP_AMOUNT", 0);
                        playerBalance += topUpAmount;
                        updateBalanceText();
                        Toast.makeText(this, "Nạp thành công " + topUpAmount + "$", Toast.LENGTH_SHORT).show();
                    }
                });

        // Gán sự kiện
        btnStartRace.setOnClickListener(v -> {
            if (isRaceRunning) return;
            if (validateBet()) {
                playerBalance -= betAmount;
                updateBalanceText();
                controlPanel.setVisibility(View.GONE);
                isRaceRunning = true;
                startRace();
            }
        });

        btnTopUp.setOnClickListener(v -> {
            Intent topUpIntent = new Intent(MainActivity.this, TopUpActivity.class);
            topUpLauncher.launch(topUpIntent);
        });

        resetRace();
    }

    private void updateBalanceText() {
        tvBalance.setText("Số dư: " + playerBalance + "$");
    }

    private void resetRace() {
        isRaceRunning = false;
        controlPanel.setVisibility(View.VISIBLE);
        car1.setX(initialCarX);
        car2.setX(initialCarX);
        car3.setX(initialCarX);
        rgCars.clearCheck();
        etBetAmount.setText("");
    }

    private boolean validateBet() {
        int selectedId = rgCars.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Vui lòng chọn xe để cược!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedId == R.id.rbCar1) carChosen = 1;
        else if (selectedId == R.id.rbCar2) carChosen = 2;
        else if (selectedId == R.id.rbCar3) carChosen = 3;

        try {
            betAmount = Integer.parseInt(etBetAmount.getText().toString());
            if (betAmount <= 0) {
                Toast.makeText(this, "Số tiền cược phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (betAmount > playerBalance) {
                Toast.makeText(this, "Bạn không đủ tiền! Vui lòng nạp thêm.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số tiền cược hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void startRace() {
        View finishLine = findViewById(R.id.finish_line);
        float finishX = finishLine.getX() - car1.getWidth();

        CountDownTimer raceTimer = new CountDownTimer(30000, 30) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isRaceRunning) {
                    this.cancel();
                    return;
                }
                Random random = new Random();
                car1.setX(car1.getX() + random.nextInt(12));
                car2.setX(car2.getX() + random.nextInt(12));
                car3.setX(car3.getX() + random.nextInt(12));

                if (car1.getX() >= finishX) {
                    this.cancel();
                    announceWinner(1);
                } else if (car2.getX() >= finishX) {
                    this.cancel();
                    announceWinner(2);
                } else if (car3.getX() >= finishX) {
                    this.cancel();
                    announceWinner(3);
                }
            }

            @Override
            public void onFinish() {
                if (isRaceRunning) {
                    // Xử lý hết giờ, ví dụ xe đi xa nhất thắng
                    float maxPos = Math.max(car1.getX(), Math.max(car2.getX(), car3.getX()));
                    if (maxPos == car1.getX()) announceWinner(1);
                    else if (maxPos == car2.getX()) announceWinner(2);
                    else announceWinner(3);
                }
            }
        };
        raceTimer.start();
    }

    private void announceWinner(int winningCar) {
        if (!isRaceRunning) return; // Đảm bảo chỉ thông báo 1 lần
        isRaceRunning = false;

        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        boolean playerWon = (winningCar == carChosen);
        int winnings = betAmount * 2;
        int newBalance = playerBalance;

        if (playerWon) {
            newBalance += winnings;
        }

        intent.putExtra("PLAYER_WON", playerWon);
        intent.putExtra("WINNINGS", winnings);
        intent.putExtra("NEW_BALANCE", newBalance);
        startActivity(intent);
        finish();
    }
}