package com.example.racingcarprm392; // Thay bằng package của bạn

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TopUpActivity extends AppCompatActivity {

    EditText etTopUpAmount;
    Button btnConfirmTopUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        etTopUpAmount = findViewById(R.id.etTopUpAmount);
        btnConfirmTopUp = findViewById(R.id.btnConfirmTopUp);

        btnConfirmTopUp.setOnClickListener(v -> {
            try {
                int amount = Integer.parseInt(etTopUpAmount.getText().toString());
                if (amount > 0) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("TOPUP_AMOUNT", amount);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}