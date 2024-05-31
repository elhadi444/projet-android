package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class ApplicantProfile extends AppCompatActivity {
    private TextView textViewName, textViewPosition, textViewEmail, textViewPhone, textViewCV, textViewMotivation, textViewComment;
    private Button buttonAccept, buttonRefuse;

    private FirebaseFirestore db;
    private String applicationId, jobId, userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_applicant_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        textViewName = findViewById(R.id.textViewName);
        textViewPosition = findViewById(R.id.textViewPosition);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewPhone = findViewById(R.id.textViewPhone);
        textViewCV = findViewById(R.id.textViewcv);
        textViewMotivation = findViewById(R.id.textViewmotivation);
        textViewComment = findViewById(R.id.textViewcomment);
        buttonAccept = findViewById(R.id.buttonAccept);
        buttonRefuse = findViewById(R.id.buttonRefuse);


        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String position = intent.getStringExtra("position");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String cv = intent.getStringExtra("cv");
        String motivation = intent.getStringExtra("motivation");
        String comment = intent.getStringExtra("comment");

        applicationId = intent.getStringExtra("applicationId");
        jobId = intent.getStringExtra("jobId");
        userId = intent.getStringExtra("userId");

        textViewName.setText(name);
        textViewPosition.setText(position);
        textViewEmail.setText(" " + email);
        textViewPhone.setText(" " + phone);
        textViewCV.setText(cv);
        textViewMotivation.setText(motivation);
        textViewComment.setText(comment);

        buttonAccept.setOnClickListener(v -> updateApplicationStatus("Accepted"));
        buttonRefuse.setOnClickListener(v -> updateApplicationStatus("Refused"));

    }

    private void updateApplicationStatus(String status) {
        db.collection("applications").document(applicationId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ApplicantProfile.this, "Application " + status, Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after updating the status
                })
                .addOnFailureListener(e -> Toast.makeText(ApplicantProfile.this, "Failed to update application status", Toast.LENGTH_SHORT).show());
    }
}