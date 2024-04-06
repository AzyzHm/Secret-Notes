package com.example.secretnotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class verify_lock extends AppCompatActivity {
    EditText pin;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_lock);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        pin = findViewById(R.id.pin);
        confirm = findViewById(R.id.confirm_button);
        confirm.setOnClickListener(v -> confirm());
    }
    private void confirm() {
        String pin = this.pin.getText().toString();
        boolean isValid = isPinValid(pin);
        if(!isValid) {
            return;
        }
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String storedPin = preferences.getString("pin", null);
        if(storedPin.equals(pin)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            this.pin.setError("Pin is incorrect");
            this.pin.requestFocus();
        }
    }
    boolean isPinValid(String pin) {
        if (pin.isEmpty()) {
            this.pin.setError("Pin is required");
            this.pin.requestFocus();
            return false;
        }
        return true;
    }
}