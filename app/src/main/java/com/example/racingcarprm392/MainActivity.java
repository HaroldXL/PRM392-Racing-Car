package com.example.racingcarprm392; // Thay bằng package của bạn

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // THAY ĐỔI BIẾN KHAI BÁO
    ImageView car1, car2, car3;
    Button btnStartRace, btnTopUp;
    TextView tvBalance;
    CheckBox cbCar1, cbCar2, cbCar3; // Đổi RadioGroup thành 3 CheckBox
    View controlPanel;

    // Định nghĩa số tiền cược cố định cho mỗi xe
    private static final int BET_CAR_1 = 10;
    private static final int BET_CAR_2 = 10;
    private static final int BET_CAR_3 = 15;
    private static final int PAYOUT_MULTIPLIER = 3; // Tỷ lệ trả thưởng (x3)

    private int playerBalance = 100;
    private int totalBetAmount = 0;
    private ArrayList<Integer> chosenCars = new ArrayList<>(); // Lưu danh sách xe đã cược

    private boolean isRaceRunning = false;
    private float initialCarX;

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
        controlPanel = findViewById(R.id.control_panel);
        cbCar1 = findViewById(R.id.cbCar1);
        cbCar2 = findViewById(R.id.cbCar2);
        cbCar3 = findViewById(R.id.cbCar3);

        car1.post(() -> initialCarX = car1.getX());

        Intent intent = getIntent();
        if (intent.hasExtra("UPDATED_BALANCE")) {
            playerBalance = intent.getIntExtra("UPDATED_BALANCE", 100);
        }
        updateBalanceText();

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

        btnStartRace.setOnClickListener(v -> {
            if (isRaceRunning) return;
            if (calculateAndValidateBet()) {
                playerBalance -= totalBetAmount;
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
        cbCar1.setChecked(false);
        cbCar2.setChecked(false);
        cbCar3.setChecked(false);
    }

    private boolean calculateAndValidateBet() {
        totalBetAmount = 0;
        chosenCars.clear();

        if (cbCar1.isChecked()) {
            totalBetAmount += BET_CAR_1;
            chosenCars.add(1);
        }
        if (cbCar2.isChecked()) {
            totalBetAmount += BET_CAR_2;
            chosenCars.add(2);
        }
        if (cbCar3.isChecked()) {
            totalBetAmount += BET_CAR_3;
            chosenCars.add(3);
        }

        if (totalBetAmount == 0) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một xe để cược!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (totalBetAmount > playerBalance) {
            Toast.makeText(this, "Bạn không đủ tiền! Tổng cược là " + totalBetAmount + "$", Toast.LENGTH_SHORT).show();
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
        if (!isRaceRunning) return;
        isRaceRunning = false;

        Intent intent = new Intent(MainActivity.this, ResultActivity.class);

        int payout = 0;
        boolean playerWonSomething = false;

        if (chosenCars.contains(winningCar)) {
            playerWonSomething = true;
            if (winningCar == 1) payout = BET_CAR_1 * PAYOUT_MULTIPLIER;
            else if (winningCar == 2) payout = BET_CAR_2 * PAYOUT_MULTIPLIER;
            else if (winningCar == 3) payout = BET_CAR_3 * PAYOUT_MULTIPLIER;
        }

        int newBalance = playerBalance + payout;

        intent.putExtra("PLAYER_WON", playerWonSomething);
        intent.putExtra("NEW_BALANCE", newBalance);
        intent.putExtra("TOTAL_BET", totalBetAmount);
        intent.putExtra("PAYOUT", payout);

        startActivity(intent);
        finish();
    }
}