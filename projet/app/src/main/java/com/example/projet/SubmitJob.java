package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class SubmitJob extends AppCompatActivity {

    private EditText editTextJob, editTextDescription, editTextStartDate, editTextCity, editTextSalary, editTextCompanyName;
    private Spinner spinnerContract;
    private Button buttonSubmit;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    //private String companyName;
    private String jobId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_job);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        editTextCompanyName = findViewById(R.id.editTextCompanyName);
        editTextJob = findViewById(R.id.editTextJob);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextCity = findViewById(R.id.editTextCity);
        spinnerContract = findViewById(R.id.spinnerNationality);
        editTextSalary = findViewById(R.id.editTextSalary);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Check if it's an edit operation
        Intent intent = getIntent();
        if (intent.hasExtra("jobId")) {
            isEditMode = true;
            jobId = intent.getStringExtra("jobId");
            populateForm(intent);
        }

        buttonSubmit.setOnClickListener(view -> {
            if (isEditMode) {
                updateJob();
            } else {
                submitJob();
            }
        });
    }


    private void populateForm(Intent intent) {
        editTextCompanyName.setText(intent.getStringExtra("companyName"));
        editTextJob.setText(intent.getStringExtra("position"));
        editTextDescription.setText(intent.getStringExtra("description"));
        editTextStartDate.setText(intent.getStringExtra("startDate"));
        editTextCity.setText(intent.getStringExtra("location"));
        // You may need to set the spinner value appropriately
        editTextSalary.setText(intent.getStringExtra("pay"));
    }
    private void submitJob() {
        String companyName = editTextCompanyName.getText().toString().trim();
        String jobTitle = editTextJob.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String startDate = editTextStartDate.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String contractType = spinnerContract.getSelectedItem().toString();
        String salary = editTextSalary.getText().toString().trim();

        if (TextUtils.isEmpty(companyName)) {
            editTextCompanyName.setError("Company name is required");
            editTextCompanyName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(jobTitle)) {
            editTextJob.setError("Job title is required");
            editTextJob.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            editTextDescription.setError("Description is required");
            editTextDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(startDate)) {
            editTextStartDate.setError("Start date is required");
            editTextStartDate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(city)) {
            editTextCity.setError("City is required");
            editTextCity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(salary)) {
            editTextSalary.setError("Salary is required");
            editTextSalary.requestFocus();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> job = new HashMap<>();
        job.put("jobTitle", jobTitle);
        job.put("description", description);
        job.put("startDate", startDate);
        job.put("city", city);
        job.put("contractType", contractType);
        job.put("salary", salary);
        job.put("companyId", userId);
        job.put("companyName", companyName);

        db.collection("jobs").add(job)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(SubmitJob.this, "Job offer submitted successfully", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SubmitJob.this, "Failed to submit job offer", Toast.LENGTH_SHORT).show();
                });

        finish();
    }

    private void updateJob() {
        String companyName = editTextCompanyName.getText().toString().trim();
        String jobTitle = editTextJob.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String startDate = editTextStartDate.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String contractType = spinnerContract.getSelectedItem().toString();
        String salary = editTextSalary.getText().toString().trim();

        if (TextUtils.isEmpty(companyName)) {
            editTextCompanyName.setError("Company name is required");
            editTextCompanyName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(jobTitle)) {
            editTextJob.setError("Job title is required");
            editTextJob.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            editTextDescription.setError("Description is required");
            editTextDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(startDate)) {
            editTextStartDate.setError("Start date is required");
            editTextStartDate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(city)) {
            editTextCity.setError("City is required");
            editTextCity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(salary)) {
            editTextSalary.setError("Salary is required");
            editTextSalary.requestFocus();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> job = new HashMap<>();
        job.put("jobTitle", jobTitle);
        job.put("description", description);
        job.put("startDate", startDate);
        job.put("city", city);
        job.put("contractType", contractType);
        job.put("salary", salary);
        job.put("companyId", userId);
        job.put("companyName", companyName);

        db.collection("jobs").document(jobId).set(job)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(SubmitJob.this, "Job offer updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SubmitJob.this, "Failed to update job offer", Toast.LENGTH_SHORT).show();
                });

        finish();
    }

    private void clearForm() {
        editTextJob.setText("");
        editTextDescription.setText("");
        editTextStartDate.setText("");
        editTextCity.setText("");
        editTextSalary.setText("");
        spinnerContract.setSelection(0);
    }
}