package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class EmployeeJobsOffers extends AppCompatActivity {

    private LinearLayout cardContainer;
    private EditText editTextSearch;
    private Button buttonAdd;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    private TextView textViewLogout,textViewApplicant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_jobs_offers);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        cardContainer = findViewById(R.id.cardContainer);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonAdd = findViewById(R.id.buttonAdd);
        textViewApplicant = findViewById(R.id.textViewApplicant);
        textViewLogout = findViewById(R.id.textViewLogout);
        userId = mAuth.getCurrentUser().getUid();


        fetchJobs(null);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                fetchJobs(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        

        buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeJobsOffers.this, SubmitJob.class);
            startActivity(intent);
        });

        textViewApplicant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeJobsOffers.this, ViewApplications.class);
                startActivity(intent);
            }
        });

        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(EmployeeJobsOffers.this, MainActivity.class));
                finish();
            }
        });
    }

    private void fetchJobs(@Nullable String query) {
        db.collection("jobs")
                .whereEqualTo("companyId", userId)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(EmployeeJobsOffers.this, "Error fetching jobs", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    cardContainer.removeAllViews();
                    for (QueryDocumentSnapshot document : snapshots) {
                        String jobId = document.getId();
                        String companyName = document.getString("companyName");
                        String pay = document.getString("salary");
                        String position = document.getString("jobTitle");
                        String location = document.getString("city");
                        String startDate = document.getString("startDate");
                        String companyId = document.getString("companyId");
                        String contractType = document.getString("contractType");
                        String description = document.getString("description");

                        if (query == null || position.toLowerCase().contains(query.toLowerCase())) {
                            db.collection("companies").document(companyId).get()
                                    .addOnSuccessListener(companyDoc -> {
                                        String companyEmail = companyDoc.getString("email");
                                        String companyFacebook = companyDoc.getString("facebook");
                                        String companyLinkedin = companyDoc.getString("linkedIn");
                                        String companyPhone = companyDoc.getString("phone");
                                        String companyWebsite = companyDoc.getString("website");
                                        String companyAbout = companyDoc.getString("about");

                                        addJobCard(jobId, companyName, pay, position, location, startDate, companyEmail, companyFacebook, companyLinkedin, companyPhone, companyWebsite, companyAbout, contractType, description);
                                    });
                        }
                    }
                });
    }


    private void addJobCard(String jobId, String companyName, String pay, String position, String location, String startDate, String companyEmail, String companyFacebook, String companyLinkedin, String companyPhone, String companyWebsite, String companyAbout, String contractType, String description) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View jobCardView = inflater.inflate(R.layout.card, cardContainer, false);

        TextView textViewCompanyName = jobCardView.findViewById(R.id.textViewCompanyName);
        TextView textViewPay = jobCardView.findViewById(R.id.textViewPay);
        TextView textViewUser = jobCardView.findViewById(R.id.textViewUser);
        TextView textViewGps = jobCardView.findViewById(R.id.textViewGps);
        TextView textViewStarts = jobCardView.findViewById(R.id.textViewStarts);

        textViewCompanyName.setText(companyName);
        textViewPay.setText(pay);
        textViewUser.setText("Position: " + position);
        textViewGps.setText(location);
        textViewStarts.setText("Starts, " + startDate);

        jobCardView.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeJobsOffers.this, EmployeeJob.class);
            intent.putExtra("jobId", jobId);
            intent.putExtra("companyName", companyName);
            intent.putExtra("pay", pay);
            intent.putExtra("position", position);
            intent.putExtra("location", location);
            intent.putExtra("startDate", startDate);
            intent.putExtra("companyEmail", companyEmail);
            intent.putExtra("companyFacebook", companyFacebook);
            intent.putExtra("companyLinkedin", companyLinkedin);
            intent.putExtra("companyPhone", companyPhone);
            intent.putExtra("companyWebsite", companyWebsite);
            intent.putExtra("companyAbout", companyAbout);
            intent.putExtra("contractType", contractType);
            intent.putExtra("description", description);
            startActivity(intent);
        });

        cardContainer.addView(jobCardView);
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 40));
        cardContainer.addView(spacer);
    }
}
