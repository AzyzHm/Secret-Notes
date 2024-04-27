package com.example.secretnotes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button LoginButton;
    ProgressBar progressBar;
    TextView CreateAccountTextView;
    ImageView hide, show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        LoginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progress_bar);
        CreateAccountTextView = findViewById(R.id.create_account_button);
        hide = findViewById(R.id.hide_password);
        show = findViewById(R.id.show_password);

        hide.setOnClickListener(v -> {
            passwordEditText.setInputType(129);
            hide.setVisibility(View.GONE);
            show.setVisibility(View.VISIBLE);
        });
        show.setOnClickListener(v -> {
            passwordEditText.setInputType(1);
            show.setVisibility(View.GONE);
            hide.setVisibility(View.VISIBLE);
        });

        LoginButton.setOnClickListener(v -> login());
        CreateAccountTextView.setOnClickListener((v)->startActivity(new Intent(this, CreateAccountActivity.class)));
    }
    private void login() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        boolean isValid = isEmailValid(email, password);
        if(!isValid) {
            return;
        }
        loginInFirebase(email,password);
    }
    boolean isEmailValid(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email is invalid");
            emailEditText.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password should be at least 6 characters long");
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }
    private void loginInFirebase(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                            startActivity(new Intent(this, setting_lock.class));
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification(); // resend verification email
                            Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if(task.getException() != null)
                            Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}