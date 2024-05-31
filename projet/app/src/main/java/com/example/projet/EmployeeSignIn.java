package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class EmployeeSignIn extends AppCompatActivity {
    private EditText editTextTextEmailAddress, editTextTextPassword;
    private Button buttonLogin;
    private TextView textViewBtnSignUP;

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(EmployeeSignIn.this, EmployeeJobsOffers.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        editTextTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        editTextTextPassword = findViewById(R.id.editTextTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewBtnSignUP = findViewById(R.id.textViewBtnSignUP);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        textViewBtnSignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EmployeeSignIn.this, EmployeeSignUp.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void loginUser() {
        String email = editTextTextEmailAddress.getText().toString().trim();
        String password = editTextTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextTextEmailAddress.setError("Valid Email is required");
            editTextTextEmailAddress.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextTextPassword.setError("Password is required");
            editTextTextPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EmployeeSignIn.this, "Login successful", Toast.LENGTH_SHORT).show();
                        // Navigate to the main activity or dashboard
                        Intent intent = new Intent(EmployeeSignIn.this, EmployeeJobsOffers.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(EmployeeSignIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}