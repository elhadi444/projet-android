package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ViewApplications extends AppCompatActivity {

    private LinearLayout applicationsContainer;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    final String TAG = "zebi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_applications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        applicationsContainer = findViewById(R.id.applicationsContainer);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        String companyId = currentUser.getUid();

        Log.d(TAG, "Application");

        db.collection("applications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Successfully fetched applications");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String applicationId = document.getId();
                            String jobId = document.getString("jobId");
                            String userId = document.getString("userId");
                            String status = document.getString("status");
                            Log.d(TAG, "Application: applicationId=" + applicationId + ", jobId=" + jobId + ", userId=" + userId + ", status=" + status);
                            fetchJobData(applicationId, jobId, userId, status, companyId);
                        }
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                        Toast.makeText(ViewApplications.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void fetchJobData(String applicationId, String jobId, String userId, String status, String companyId) {
        db.collection("jobs").document(jobId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String fetchedCompanyId = document.getString("companyId");
                    String position = document.getString("jobTitle");
                    if (companyId.equals(fetchedCompanyId)) {
                        Log.d(TAG, "Job: position=" + position + ", companyId=" + fetchedCompanyId);
                        fetchUserData(applicationId, jobId, userId, position, status);
                    }
                } else {
                    Log.e(TAG, "Job document does not exist");
                }
            } else {
                Log.e(TAG, "Error getting job data: ", task.getException());
                Toast.makeText(ViewApplications.this, "Error getting job data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserData(String applicationId, String jobId, String userId, String position, String status) {
        db.collection("workers").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String firstName = document.getString("firstName");
                    String lastName = document.getString("lastName");
                    String email = document.getString("email");
                    String phone = document.getString("phone");
                    String cv = document.getString("cv");
                    String motivation = document.getString("motivation");
                    String comment = document.getString("comment");
                    Log.d(TAG, "User: firstName=" + firstName + ", lastName=" + lastName);
                    addApplicationCard(applicationId, jobId, userId, firstName, lastName, position, status, email, phone, cv, motivation, comment);
                } else {
                    Log.e(TAG, "User document does not exist");
                }
            } else {
                Log.e(TAG, "Error getting user data: ", task.getException());
                Toast.makeText(ViewApplications.this, "Error getting user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addApplicationCard(String applicationId, String jobId, String userId, String firstName, String lastName, String position, String status, String email, String phone, String cv, String motivation, String comment) {
        View applicationCardView = getLayoutInflater().inflate(R.layout.application_card, applicationsContainer, false);

        TextView textViewApplicantName = applicationCardView.findViewById(R.id.textViewApplicantName);
        TextView textViewPosition = applicationCardView.findViewById(R.id.textViewPosition);
        TextView textViewStatus = applicationCardView.findViewById(R.id.textViewStatus);

        textViewApplicantName.setText(firstName + " " + lastName);
        textViewPosition.setText("Position: " + position);
        textViewStatus.setText("Status: " + status);

        applicationCardView.setOnClickListener(v -> {
            Intent intent = new Intent(ViewApplications.this, ApplicantProfile.class);
            intent.putExtra("name", firstName + " " + lastName);
            intent.putExtra("position", position);
            intent.putExtra("status", status);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            intent.putExtra("cv", cv);
            intent.putExtra("motivation", motivation);
            intent.putExtra("comment", comment);
            intent.putExtra("applicationId", applicationId);
            intent.putExtra("jobId", jobId);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        applicationsContainer.addView(applicationCardView);
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 40));
        applicationsContainer.addView(spacer);
    }
}
