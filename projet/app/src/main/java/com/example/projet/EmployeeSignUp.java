package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EmployeeSignUp extends AppCompatActivity {
    private EditText editTextCompanyName, editTextTextEmailAddress, editTextTextPassword, editTextPhone, editTextAbout, editTextWebsite, editTextLinkedIn, editTextFacebook;
    private CheckBox checkboxAgree;
    private Button buttonConfirm;
    private TextView textViewBtnSignIn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(EmployeeSignUp.this, EmployeeJobsOffers.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextCompanyName = findViewById(R.id.editTextCompanyName);
        editTextTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        editTextTextPassword = findViewById(R.id.editTextTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAbout = findViewById(R.id.editTextAbout);
        editTextWebsite = findViewById(R.id.editTextWebsite);
        editTextLinkedIn = findViewById(R.id.editTextlinkedIn);
        editTextFacebook = findViewById(R.id.editTextFacebook);
        checkboxAgree = findViewById(R.id.checkboxAgree);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        textViewBtnSignIn = findViewById(R.id.textViewBtnSignIn);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerCompany();
            }
        });

        textViewBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EmployeeSignUp.this, EmployeeSignIn.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void registerCompany() {
        String companyName = editTextCompanyName.getText().toString().trim();
        String email = editTextTextEmailAddress.getText().toString().trim();
        String password = editTextTextPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String about = editTextAbout.getText().toString().trim();
        String website = editTextWebsite.getText().toString().trim();
        String linkedIn = editTextLinkedIn.getText().toString().trim();
        String facebook = editTextFacebook.getText().toString().trim();

        if (TextUtils.isEmpty(companyName)) {
            editTextCompanyName.setError("Company Name is required");
            editTextCompanyName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextTextEmailAddress.setError("Valid Email is required");
            editTextTextEmailAddress.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            editTextTextPassword.setError("Password should be at least 6 characters");
            editTextTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Phone Number is required");
            editTextPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(about)) {
            editTextAbout.setError("About your company is required");
            editTextAbout.requestFocus();
            return;
        }

        if (!checkboxAgree.isChecked()) {
            Toast.makeText(this, "You must agree to the Terms of Service and Privacy Policy", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();

                        Map<String, Object> company = new HashMap<>();
                        company.put("companyName", companyName);
                        company.put("email", email);
                        company.put("phone", phone);
                        company.put("about", about);
                        company.put("website", website);
                        company.put("linkedIn", linkedIn);
                        company.put("facebook", facebook);

                        db.collection("companies").document(userId).set(company)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(EmployeeSignUp.this, "Company registered successfully", Toast.LENGTH_SHORT).show();
                                    // Navigate to another activity if needed
                                    Intent intent = new Intent(EmployeeSignUp.this, EmployeeJobsOffers.class);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(EmployeeSignUp.this, "Failed to register company", Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(EmployeeSignUp.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}