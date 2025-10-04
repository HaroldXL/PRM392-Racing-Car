package com.example.racingcarprm392; // Thay bằng package của bạn

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class BetSettingsActivity extends AppCompatActivity {

    private EditText etCar1Bet, etCar2Bet, etCar3Bet ;
    private TextView currentAmount;
    private MaterialButton btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bet_settings);

        etCar1Bet = findViewById(R.id.etCar1Bet);
        etCar2Bet = findViewById(R.id.etCar2Bet);
        etCar3Bet = findViewById(R.id.etCar3Bet);
        currentAmount = findViewById(R.id.tvBalance);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Load current values (if any)
        Intent intent = getIntent();
        etCar1Bet.setText(String.valueOf(intent.getIntExtra("BET_CAR_1", 0)));
        etCar2Bet.setText(String.valueOf(intent.getIntExtra("BET_CAR_2", 0)));
        etCar3Bet.setText(String.valueOf(intent.getIntExtra("BET_CAR_3", 0)));
        currentAmount.setText(String.valueOf(intent.getIntExtra("BALANCE", 0)));

        btnSave.setOnClickListener(v -> {
            try {
                int bet1 = Integer.parseInt(etCar1Bet.getText().toString());
                int bet2 = Integer.parseInt(etCar2Bet.getText().toString());
                int bet3 = Integer.parseInt(etCar3Bet.getText().toString());
                int balance = Integer.parseInt(currentAmount.getText().toString());
                if (bet1 < 0 || bet2 < 0 || bet3 < 0) {
                    Toast.makeText(this, "Số tiền cược phải ≥ 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(bet1 + bet2 + bet3 > balance){
                    Toast.makeText(this, "Số dư không đủ để đặt cược", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent result = new Intent();
                result.putExtra("BET_CAR_1", bet1);
                result.putExtra("BET_CAR_2", bet2);
                result.putExtra("BET_CAR_3", bet3);
                setResult(Activity.RESULT_OK, result);
                finish();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập số hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
