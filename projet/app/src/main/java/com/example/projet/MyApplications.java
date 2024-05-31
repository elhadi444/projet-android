package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MyApplications extends AppCompatActivity {

    private static final String TAG = "MyApplications";

    private LinearLayout applicationsContainer;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        applicationsContainer = findViewById(R.id.applicationsContainer);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "You need to be logged in to view your applications", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        db.collection("applications")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String applicationId = document.getId();
                            String jobId = document.getString("jobId");
                            String status = document.getString("status");
                            Log.d(TAG, "Application: applicationId=" + applicationId + ", jobId=" + jobId);
                            fetchJobData(applicationId, jobId, status);
                        }
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                        Toast.makeText(MyApplications.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchJobData(String applicationId, String jobId, String status) {
        db.collection("jobs").document(jobId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String companyName = document.getString("companyName");
                    String pay = document.getString("salary");
                    String position = document.getString("jobTitle");
                    String location = document.getString("city");
                    String startDate = document.getString("startDate");
                    addApplicationCard(applicationId, jobId, companyName, pay, position, location, startDate, status);
                } else {
                    Log.e(TAG, "Job document does not exist");
                }
            } else {
                Log.e(TAG, "Error getting job data: ", task.getException());
                Toast.makeText(MyApplications.this, "Error getting job data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addApplicationCard(String applicationId, String jobId, String companyName, String pay, String position, String location, String startDate, String status) {
        View applicationCardView = getLayoutInflater().inflate(R.layout.my_application_card, applicationsContainer, false);

        TextView textViewCompanyName = applicationCardView.findViewById(R.id.textViewCompanyName);
        TextView textViewPosition = applicationCardView.findViewById(R.id.textViewPosition);
        TextView textViewPay = applicationCardView.findViewById(R.id.textViewPay);
        TextView textViewGps = applicationCardView.findViewById(R.id.textViewGps);
        TextView textViewStarts = applicationCardView.findViewById(R.id.textViewStarts);
        TextView textViewStatus = applicationCardView.findViewById(R.id.textViewStatus);

        textViewCompanyName.setText(companyName);
        textViewPosition.setText("Position: " + position);
        textViewPay.setText(pay);
        textViewGps.setText(location);
        textViewStarts.setText("Starts, " + startDate);
        textViewStatus.setText("Status: " + status);

        applicationCardView.setOnClickListener(v -> {
            Intent intent = new Intent(MyApplications.this, JobDetails.class);
            intent.putExtra("jobId", jobId);
            intent.putExtra("companyName", companyName);
            intent.putExtra("pay", pay);
            intent.putExtra("position", position);
            intent.putExtra("location", location);
            intent.putExtra("startDate", startDate);
            intent.putExtra("applicationId", applicationId);
            startActivity(intent);
        });

        applicationsContainer.addView(applicationCardView);
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 40));
        applicationsContainer.addView(spacer);
    }
}
