package com.example.projet;

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

import java.util.HashMap;
import java.util.Map;

public class ViewJob extends AppCompatActivity {

    private TextView textViewCompanyName, textViewPay, textViewUser, textViewGps, textViewStarts;
    private TextView textViewTitle, textViewLocalisation, textViewDescriptionText, textViewDetailsText;
    private TextView textViewAboutCompanyText, textViewWebSite, textViewEmail, textViewPhone, textViewFb, textViewLinkedin;
    private Button buttonApply;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_job);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize the views
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
        buttonApply = findViewById(R.id.buttonApply);

        // Get job details from the intent

        jobId = getIntent().getStringExtra("jobId");

        String companyName = getIntent().getStringExtra("companyName");
        String pay = getIntent().getStringExtra("pay");
        String position = getIntent().getStringExtra("position");
        String location = getIntent().getStringExtra("location");
        String startDate = getIntent().getStringExtra("startDate");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String details = getIntent().getStringExtra("details");
        String aboutCompany = getIntent().getStringExtra("aboutCompany");
        String webSite = getIntent().getStringExtra("webSite");
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");
        String fb = getIntent().getStringExtra("fb");
        String linkedin = getIntent().getStringExtra("linkedin");

        // Set job details to the views
        textViewCompanyName.setText(companyName);
        textViewPay.setText(pay);
        textViewUser.setText("Position: " + position);
        textViewGps.setText(location);
        textViewStarts.setText("Starts, " + startDate);
        textViewTitle.setText(title);
        textViewLocalisation.setText(location);
        textViewDescriptionText.setText(description);
        textViewDetailsText.setText(details);
        textViewAboutCompanyText.setText(aboutCompany);
        textViewWebSite.setText(webSite);
        textViewEmail.setText(email);
        textViewPhone.setText(phone);
        textViewFb.setText("Facebook " + fb);
        textViewLinkedin.setText("Linkedin " + linkedin);

        buttonApply.setOnClickListener(v -> applyForJob());
    }

    private void applyForJob() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(ViewJob.this, "You need to be logged in to apply", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String userEmail = mAuth.getCurrentUser().getEmail();

        Map<String, Object> application = new HashMap<>();
        application.put("jobId", jobId);
        application.put("userId", userId);
        application.put("userEmail", userEmail);
        application.put("status", "Pending");  // You can add more fields as needed

        db.collection("applications").add(application)
                .addOnSuccessListener(documentReference -> Toast.makeText(ViewJob.this, "Application submitted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ViewJob.this, "Failed to submit application", Toast.LENGTH_SHORT).show());
    }
}