package com.example.projet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UnemployedSignUp extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextBirth, editTextTextEmailAddress, editTextTextPassword, editTextPhone, editTextAddress, editTextZip, editTextComment;
    private Spinner spinnerNationality;
    private CheckBox checkboxAgree;
    private Button buttonConfirm;
    private TextView textViewBtnLogin, textView_hello, textViewEmail, textViewPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputLayout inputLayout;
    private LinearLayout loginLayout;

    private boolean isEditMode = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_unemployed_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextBirth = findViewById(R.id.editTextBirth);
        editTextTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        editTextTextPassword = findViewById(R.id.editTextTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        spinnerNationality = findViewById(R.id.spinnerNationality);
        editTextAddress = findViewById(R.id.editTexAddress);
        editTextZip = findViewById(R.id.editTexZip);
        editTextComment = findViewById(R.id.editTextComment);
        checkboxAgree = findViewById(R.id.checkboxAgree);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        textViewBtnLogin = findViewById(R.id.textViewBtnLogin);
        textView_hello = findViewById(R.id.textView_hello);

        textViewEmail = findViewById(R.id.textViewEmail);
        textViewPassword = findViewById(R.id.textViewPassword);
        inputLayout = findViewById(R.id.inputLayout);
        loginLayout = findViewById(R.id.loginLayout);

        Intent intent = getIntent();
        if (intent.hasExtra("isEditMode")) {
            isEditMode = intent.getBooleanExtra("isEditMode", false);
            prefillForm(intent);
        }

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditMode) {
                    updateWorkerProfile();
                } else {
                    registerWorker();
                }
            }
        });

        textViewBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UnemployedSignUp.this, UnemployedLogin.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void prefillForm(Intent intent) {

        textView_hello.setText("Edit profile");
        textViewEmail.setVisibility(View.GONE);
        textViewPassword.setVisibility(View.GONE);
        inputLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.GONE);


        editTextFirstName.setText(intent.getStringExtra("firstName"));
        editTextLastName.setText(intent.getStringExtra("lastName"));
        editTextBirth.setText(intent.getStringExtra("birthDate"));
        editTextTextEmailAddress.setText(intent.getStringExtra("email"));
        editTextTextPassword.setVisibility(View.GONE); // Hide password field in edit mode
        editTextTextEmailAddress.setVisibility(View.GONE); // Hide email field in edit mode
        editTextPhone.setText(intent.getStringExtra("phone"));
        // Set nationality spinner value
        editTextAddress.setText(intent.getStringExtra("address"));
        editTextZip.setText(intent.getStringExtra("zip"));
        editTextComment.setText(intent.getStringExtra("comment"));
        //checkboxAgree.setChecked(true); // Assume user agrees to terms in edit mode

        checkboxAgree.setVisibility(View.GONE);
    }

    private void updateWorkerProfile() {
        String userId = mAuth.getCurrentUser().getUid();

        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String birthDate = editTextBirth.getText().toString().trim();
        //String email = editTextTextEmailAddress.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String nationality = spinnerNationality.getSelectedItem().toString();
        String address = editTextAddress.getText().toString().trim();
        String zip = editTextZip.getText().toString().trim();
        String comment = editTextComment.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            editTextFirstName.setError("First Name is required");
            editTextFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError("Last Name is required");
            editTextLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(birthDate)) {
            editTextBirth.setError("Date of Birth is required");
            editTextBirth.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Phone Number is required");
            editTextPhone.requestFocus();
            return;
        }

//        if (TextUtils.isEmpty(address)) {
//            editTextAddress.setError("Address is required");
//            editTextAddress.requestFocus();
//            return;
//        }

        if (TextUtils.isEmpty(zip)) {
            editTextZip.setError("Zip Code is required");
            editTextZip.requestFocus();
            return;
        }

        Map<String, Object> worker = new HashMap<>();
        worker.put("firstName", firstName);
        worker.put("lastName", lastName);
        worker.put("birthDate", birthDate);
        //worker.put("email", email);
        worker.put("phone", phone);
        worker.put("nationality", nationality);
        worker.put("address", address);
        worker.put("zip", zip);
        worker.put("comment", comment);

        db.collection("workers").document(userId).update(worker)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UnemployedSignUp.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    // Navigate to another activity
                    startActivity(new Intent(UnemployedSignUp.this, ViewJobsOffers.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UnemployedSignUp.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void registerWorker() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String birthDate = editTextBirth.getText().toString().trim();
        String email = editTextTextEmailAddress.getText().toString().trim();
        String password = editTextTextPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String nationality = spinnerNationality.getSelectedItem().toString();
        String address = editTextAddress.getText().toString().trim();
        String zip = editTextZip.getText().toString().trim();
        String comment = editTextComment.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            editTextFirstName.setError("First Name is required");
            editTextFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError("Last Name is required");
            editTextLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(birthDate)) {
            editTextBirth.setError("Date of Birth is required");
            editTextBirth.requestFocus();
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

        if (TextUtils.isEmpty(address)) {
            editTextAddress.setError("Address is required");
            editTextAddress.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(zip)) {
            editTextZip.setError("Zip Code is required");
            editTextZip.requestFocus();
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

                        Map<String, Object> worker = new HashMap<>();
                        worker.put("firstName", firstName);
                        worker.put("lastName", lastName);
                        worker.put("birthDate", birthDate);
                        worker.put("email", email);
                        worker.put("phone", phone);
                        worker.put("nationality", nationality);
                        worker.put("address", address);
                        worker.put("zip", zip);
                        worker.put("comment", comment);

                        db.collection("workers").document(userId).set(worker)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(UnemployedSignUp.this, "Worker registered successfully", Toast.LENGTH_SHORT).show();
                                    // Navigate to another activity
                                    startActivity(new Intent(UnemployedSignUp.this, ViewJobsOffers.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(UnemployedSignUp.this, "Failed to register worker", Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(UnemployedSignUp.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
