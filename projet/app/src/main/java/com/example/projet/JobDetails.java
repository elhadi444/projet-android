package com.example.projet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class JobDetails extends AppCompatActivity {

    private TextView textViewCompanyName, textViewPay, textViewUser, textViewGps, textViewStarts;
    private TextView textViewTitle, textViewLocalisation, textViewDescriptionText, textViewDetailsText;
    private TextView textViewAboutCompanyText, textViewWebSite, textViewEmail, textViewPhone;
    private TextView textViewFb, textViewLinkedin;
    private Button buttonRemoveApplication;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        db = FirebaseFirestore.getInstance();

        textViewCompanyName = findViewById(R.id.textViewCompanyName);
        textViewPay = findViewById(R.id.textViewPay);
        textViewUser = findViewById(R.id.textViewUser);
        textViewGps = findViewById(R.id.textViewGps);
        textViewStarts = findViewById(R.id.textViewStarts);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewLocalisation = findViewById(R.id.textViewLocalisation);
        textViewDescriptionText = findViewById(R.id.textViewDescriptionText);
        textViewDetailsText = findViewById(R.id.textViewDetailsText);
        textViewAboutCompanyText = findViewById(R.id.textViewAboutCompanyText);
        textViewWebSite = findViewById(R.id.textViewWebSite);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewPhone = findViewById(R.id.textViewPhone);
        textViewFb = findViewById(R.id.textViewFb);
        textViewLinkedin = findViewById(R.id.textViewLinkedin);
        buttonRemoveApplication = findViewById(R.id.buttonRemoveApplication);

        String jobId = getIntent().getStringExtra("jobId");
        String applicationId = getIntent().getStringExtra("applicationId");

        fetchJobDetails(jobId);
        buttonRemoveApplication.setOnClickListener(v -> removeApplication(applicationId));
    }

    private void fetchJobDetails(String jobId) {
        db.collection("jobs").document(jobId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    textViewCompanyName.setText(document.getString("companyName"));
                    textViewPay.setText(document.getString("salary"));
                    textViewUser.setText("Position: " + document.getString("jobTitle"));
                    textViewGps.setText(document.getString("city"));
                    textViewStarts.setText("Starts, " + document.getString("startDate"));
                    textViewTitle.setText(document.getString("jobTitle") + " at " + document.getString("companyName"));
                    textViewLocalisation.setText(document.getString("city"));
                    textViewDescriptionText.setText(document.getString("description"));
                    textViewDetailsText.setText("Type of contract: " + document.getString("contractType"));

                    String companyId = document.getString("companyId");
                    fetchCompanyDetails(companyId);
                } else {
                    Toast.makeText(JobDetails.this, "Job document does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(JobDetails.this, "Error getting job data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCompanyDetails(String companyId) {
        db.collection("companies").document(companyId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    textViewAboutCompanyText.setText(document.getString("about"));
                    textViewWebSite.setText(document.getString("website"));
                    textViewEmail.setText(document.getString("email"));
                    textViewPhone.setText(document.getString("phone"));
                    textViewFb.setText("Facebook " + document.getString("facebook"));
                    textViewLinkedin.setText("LinkedIn " + document.getString("linkedIn"));
                } else {
                    Toast.makeText(JobDetails.this, "Company document does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(JobDetails.this, "Error getting company data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeApplication(String applicationId) {
        db.collection("applications").document(applicationId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(JobDetails.this, "Application removed", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(JobDetails.this, "Error removing application", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
