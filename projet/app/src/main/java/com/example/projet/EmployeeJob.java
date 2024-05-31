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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmployeeJob extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_job);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        Intent intent = getIntent();
        String jobId = intent.getStringExtra("jobId");
        String companyName = intent.getStringExtra("companyName");
        String pay = intent.getStringExtra("pay");
        String position = intent.getStringExtra("position");
        String location = intent.getStringExtra("location");
        String startDate = intent.getStringExtra("startDate");
        String companyEmail = intent.getStringExtra("companyEmail");
        String companyFacebook = intent.getStringExtra("companyFacebook");
        String companyLinkedin = intent.getStringExtra("companyLinkedin");
        String companyPhone = intent.getStringExtra("companyPhone");
        String companyWebsite = intent.getStringExtra("companyWebsite");
        String companyAbout = intent.getStringExtra("companyAbout");
        String contractType = intent.getStringExtra("contractType");
        String description = intent.getStringExtra("description");

        // Find views by ID
        TextView textViewCompanyName = findViewById(R.id.textViewCompanyName);
        TextView textViewPay = findViewById(R.id.textViewPay);
        TextView textViewUser = findViewById(R.id.textViewUser);
        TextView textViewGps = findViewById(R.id.textViewGps);
        TextView textViewStarts = findViewById(R.id.textViewStarts);
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewLocalisation = findViewById(R.id.textViewLocalisation);
        TextView textViewDescriptionText = findViewById(R.id.textViewDescriptionText);
        TextView textViewDetailsText = findViewById(R.id.textViewDetailsText);
        TextView textViewAboutCompanyText = findViewById(R.id.textViewAboutCompanyText);
        TextView textViewWebSite = findViewById(R.id.textViewWebSite);
        TextView textViewEmail = findViewById(R.id.textViewEmail);
        TextView textViewPhone = findViewById(R.id.textViewPhone);
        TextView textViewFb = findViewById(R.id.textViewFb);
        TextView textViewLinkedin = findViewById(R.id.textViewLinkedin);

        // Set data to views
        textViewCompanyName.setText(companyName);
        textViewPay.setText(pay);
        textViewUser.setText("Position: " + position);
        textViewGps.setText(location);
        textViewStarts.setText("Starts, " + startDate);
        textViewTitle.setText(position);
        textViewLocalisation.setText(location);
        textViewDescriptionText.setText(description);
        textViewDetailsText.setText("Type of contract: " + contractType);
        textViewAboutCompanyText.setText(companyAbout);
        textViewWebSite.setText(companyWebsite);
        textViewEmail.setText(companyEmail);
        textViewPhone.setText(companyPhone);
        textViewFb.setText("fecebook: " + companyFacebook);
        textViewLinkedin.setText("Linkedin: " + companyLinkedin);

        Button buttonDelete = findViewById(R.id.buttonDelete);
        Button buttonEdit = findViewById(R.id.buttonEdit);

        buttonDelete.setOnClickListener(v -> deleteJob(jobId));
        buttonEdit.setOnClickListener(v -> editJob(jobId, startDate));
    }

    private void deleteJob(String jobId) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        db.collection("jobs").document(jobId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EmployeeJob.this, "Job deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EmployeeJob.this, "Error deleting job", Toast.LENGTH_SHORT).show());
        finish();
    }

    private void editJob(String jobId, String startDate) {
        Intent intent = new Intent(EmployeeJob.this, SubmitJob.class);
        intent.putExtra("jobId", jobId);
        intent.putExtra("companyName", ((TextView) findViewById(R.id.textViewCompanyName)).getText().toString());
        intent.putExtra("pay", ((TextView) findViewById(R.id.textViewPay)).getText().toString());
        intent.putExtra("position", ((TextView) findViewById(R.id.textViewTitle)).getText().toString());
        intent.putExtra("location", ((TextView) findViewById(R.id.textViewLocalisation)).getText().toString());
        intent.putExtra("startDate", startDate);
        intent.putExtra("companyEmail", ((TextView) findViewById(R.id.textViewEmail)).getText().toString());
        intent.putExtra("companyFacebook", ((TextView) findViewById(R.id.textViewFb)).getText().toString());
        intent.putExtra("companyLinkedin", ((TextView) findViewById(R.id.textViewLinkedin)).getText().toString());
        intent.putExtra("companyPhone", ((TextView) findViewById(R.id.textViewPhone)).getText().toString());
        intent.putExtra("companyWebsite", ((TextView) findViewById(R.id.textViewWebSite)).getText().toString());
        intent.putExtra("companyAbout", ((TextView) findViewById(R.id.textViewAboutCompanyText)).getText().toString());
        intent.putExtra("contractType", ((TextView) findViewById(R.id.textViewDetailsText)).getText().toString());
        intent.putExtra("description", ((TextView) findViewById(R.id.textViewDescriptionText)).getText().toString());
        startActivity(intent);
        finish();
    }
}