package com.example.racingcarprm392; // Thay bằng package của bạn

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.racingcarprm392.Utils.SoundPlayer;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Khai báo các thành phần UI
    ImageView car1, car2, car3;
    MaterialButton btnStartRace, btnTopUp, btnBetSettings;
    TextView tvBalance, tvBetAnnoucement;
    CheckBox cbCar1, cbCar2, cbCar3;
    ConstraintLayout rootLayout;
    LinearLayout controlPanel;
    SoundPlayer carSoundPlayer = new SoundPlayer(this);
    SoundPlayer mainMusic = new SoundPlayer(this);

    // Các hằng số và biến cho logic game
    private static int BET_CAR_1 = 0;
    private static int BET_CAR_2 = 0;
    private static int BET_CAR_3 = 0;
    private static double PAYOUT_MULTIPLIER = 2; // Tỷ lệ trả thưởng

    private int playerBalance = 100;
    private int totalBetAmount = 0;
    private ArrayList<Integer> chosenCars = new ArrayList<>();

    private boolean isRaceRunning = false;
    private float initialCarX; // Lưu vị trí X ban đầu của xe

    private ActivityResultLauncher<Intent> topUpLauncher;
    private ActivityResultLauncher<Intent> betSettingsLauncher;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        carSoundPlayer.stopSound();
        mainMusic.stopSound();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Ánh xạ views từ file XML
        car1 = findViewById(R.id.car1);
        car2 = findViewById(R.id.car2);
        car3 = findViewById(R.id.car3);
        btnStartRace = findViewById(R.id.btnStartRace);
        btnTopUp = findViewById(R.id.btnTopUp);
        tvBalance = findViewById(R.id.tvBalance);
        cbCar1 = findViewById(R.id.cbCar1);
        cbCar2 = findViewById(R.id.cbCar2);
        cbCar3 = findViewById(R.id.cbCar3);
        rootLayout = findViewById(R.id.root_layout);
        controlPanel = findViewById(R.id.control_panel);
        btnBetSettings = findViewById(R.id.btnBetSettings);
        tvBetAnnoucement = findViewById(R.id.bet_announcement);

        mainMusic.playSound(R.raw.main_music, true);
        cbCar3.setClickable(false);
        cbCar3.setFocusable(false);
        cbCar2.setClickable(false);
        cbCar2.setFocusable(false);
        cbCar1.setClickable(false);
        cbCar1.setFocusable(false);

        // Kích hoạt nền gradient động
        AnimationDrawable animationDrawable = (AnimationDrawable) rootLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        // Chạy animation cho bảng điều khiển khi vào ứng dụng
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        controlPanel.startAnimation(slideIn);

        // Lấy vị trí ban đầu của xe
        car1.post(() -> initialCarX = car1.getX());

        // Nhận lại số dư khi "Chơi lại"
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

        betSettingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        BET_CAR_1 = data.getIntExtra("BET_CAR_1", BET_CAR_1);
                        BET_CAR_2 = data.getIntExtra("BET_CAR_2", BET_CAR_2);
                        BET_CAR_3 = data.getIntExtra("BET_CAR_3", BET_CAR_3);
                        int bettedCar = 0;
                        chosenCars.clear();
                        if(BET_CAR_1 > 0) {
                            ++bettedCar;
                            chosenCars.add(1);
                            cbCar1.setChecked(true);
                            cbCar1.setText( "Xe 1 (" + Integer.toString(BET_CAR_1) + "$)");
                        } else {
                            cbCar1.setChecked(false);
                            cbCar1.setText( "Xe 1" );
                        }
                        if(BET_CAR_2 > 0) {
                            ++bettedCar;
                            chosenCars.add(2);
                            cbCar2.setChecked(true);
                            cbCar2.setText( "Xe 2 (" + Integer.toString(BET_CAR_2) + "$)");
                        } else {
                            cbCar2.setChecked(false);
                            cbCar2.setText( "Xe 2" );
                        }
                        if(BET_CAR_3 > 0) {
                            ++bettedCar;
                            chosenCars.add(3);
                            cbCar3.setChecked(true);
                            cbCar3.setText( "Xe 3 (" + Integer.toString(BET_CAR_3) + "$)");
                        } else {
                            cbCar3.setChecked(false);
                            cbCar3.setText( "Xe 3" );
                        }
                        switch (bettedCar) {
                            case 0:
                                tvBetAnnoucement.setText("CƯỢC CỦA BẠN");
                                PAYOUT_MULTIPLIER = 0;
                                break;
                            case 1:
                                tvBetAnnoucement.setText("CƯỢC CỦA BẠN (x2)");
                                PAYOUT_MULTIPLIER = 2.0;
                                break;
                            case 2:
                                tvBetAnnoucement.setText("CƯỢC CỦA BẠN (x1.5)");
                                PAYOUT_MULTIPLIER = 1.5;
                                break;
                            case 3:
                                tvBetAnnoucement.setText("CƯỢC CỦA BẠN (x1.2)");
                                PAYOUT_MULTIPLIER = 1.2;
                                break;
                            default:
                                tvBetAnnoucement.setText("CƯỢC CỦA BẠN");
                                PAYOUT_MULTIPLIER = 0;
                                break;
                        }
                        Toast.makeText(this, "Cập nhật tiền cược thành công!", Toast.LENGTH_SHORT).show();
                    }
                });

        // Gán sự kiện cho nút "Đua ngay"
        btnStartRace.setOnClickListener(v -> {
            if (isRaceRunning) return;
            if (calculateAndValidateBet()) {
                playerBalance -= totalBetAmount;
                updateBalanceText();

                // Chạy animation trượt ra trước khi ẩn bảng điều khiển
                Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
                slideOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        controlPanel.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                controlPanel.startAnimation(slideOut);

                isRaceRunning = true;
                startRace();
            }
        });

        btnBetSettings.setOnClickListener(v -> {
            Intent betIntent = new Intent(MainActivity.this, BetSettingsActivity.class);
            betIntent.putExtra("BET_CAR_1", BET_CAR_1);
            betIntent.putExtra("BET_CAR_2", BET_CAR_2);
            betIntent.putExtra("BET_CAR_3", BET_CAR_3);
            betIntent.putExtra("BALANCE", playerBalance);
            betSettingsLauncher.launch(betIntent);
        });

        // Gán sự kiện cho nút "Nạp tiền"
        btnTopUp.setOnClickListener(v -> {
            Intent topUpIntent = new Intent(MainActivity.this, TopUpActivity.class);
            topUpLauncher.launch(topUpIntent);
        });
    }

    // Cập nhật hiển thị số dư
    private void updateBalanceText() {
        tvBalance.setText(playerBalance + "$");
    }

    // Đưa game về trạng thái ban đầu
    private void resetRace() {
        isRaceRunning = false;
        controlPanel.setVisibility(View.VISIBLE);

        // Chạy lại animation trượt vào khi reset
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        controlPanel.startAnimation(slideIn);

        // Đưa xe về vạch xuất phát
        car1.setX(initialCarX);
        car2.setX(initialCarX);
        car3.setX(initialCarX);

        // Bỏ chọn tất cả checkbox
        cbCar1.setChecked(false);
        cbCar2.setChecked(false);
        cbCar3.setChecked(false);
    }

    // Tính tổng tiền cược và kiểm tra hợp lệ
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

    // Bắt đầu cuộc đua
    private void startRace() {
        View finishLine = findViewById(R.id.finish_line);
        carSoundPlayer.playSound(R.raw.racing, false);
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

    // Thông báo người thắng và chuyển màn hình
    private void announceWinner(int winningCar) {
        if (!isRaceRunning) return;
        isRaceRunning = false;

        carSoundPlayer.stopSound();
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);

        int payout = 0;
        boolean playerWonSomething = false;

        if (chosenCars.contains(winningCar)) {
            playerWonSomething = true;
            if (winningCar == 1) payout = (int)(BET_CAR_1 * PAYOUT_MULTIPLIER);
            else if (winningCar == 2) payout = (int)(BET_CAR_2 * PAYOUT_MULTIPLIER);
            else if (winningCar == 3) payout = (int)(BET_CAR_3 * PAYOUT_MULTIPLIER);
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