package com.example.projet;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ViewJobsOffers extends AppCompatActivity {

    private LinearLayout cardContainer;
    private TextView textViewBtnSignUP, textViewApplicant, textViewLogout, textViewProfile;
    private Button buttonFilter;
    private EditText editTextSearch;
    private LinearLayout layoutLoginPrompt;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_jobs_offers);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        cardContainer = findViewById(R.id.cardContainer);
        buttonFilter = findViewById(R.id.buttonFilter);
        textViewBtnSignUP = findViewById(R.id.textViewBtnSignUP);
        editTextSearch = findViewById(R.id.editTextSearch);
        layoutLoginPrompt = findViewById(R.id.layoutLoginPrompt);
        textViewApplicant = findViewById(R.id.textViewApplicant);
        textViewLogout = findViewById(R.id.textViewLogout);
        textViewProfile = findViewById(R.id.textViewProfile);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            layoutLoginPrompt.setVisibility(View.GONE);
        } else {
            layoutLoginPrompt.setVisibility(View.VISIBLE);
        }

        Intent intent = getIntent();
        String job = intent.getStringExtra("job");
        String city = intent.getStringExtra("city");
        String periode = intent.getStringExtra("periode");
        String contactType = intent.getStringExtra("contactType");

        fetchJobs(null, job, city, periode, contactType);

        //fetchJobs(null);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                fetchJobs(charSequence.toString(), job, city, periode, contactType);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewJobsOffers.this, Filter.class);
                startActivity(i);
            }
        });

        textViewBtnSignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewJobsOffers.this, UnemployedLogin.class);
                startActivity(i);
                finish();
            }
        });

        textViewApplicant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewJobsOffers.this, MyApplications.class);
                startActivity(i);
            }
        });

        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ViewJobsOffers.this, MainActivity.class));
                finish();
            }
        });

        textViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null){
                    String userId = currentUser.getUid();
                    db.collection("workers").document(userId).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Intent intent = new Intent(ViewJobsOffers.this, UnemployedSignUp.class);
                                intent.putExtra("isEditMode", true);
                                intent.putExtra("firstName", document.getString("firstName"));
                                intent.putExtra("lastName", document.getString("lastName"));
                                intent.putExtra("birthDate", document.getString("birthDate"));
                                intent.putExtra("email", document.getString("email"));
                                intent.putExtra("phone", document.getString("phone"));
                                intent.putExtra("nationality", document.getString("nationality"));
                                intent.putExtra("address", document.getString("address"));
                                intent.putExtra("zip", document.getString("zip"));
                                intent.putExtra("comment", document.getString("comment"));
                                startActivity(intent);
                            } else {
                                Toast.makeText(ViewJobsOffers.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ViewJobsOffers.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }


    private void fetchJobs(@Nullable String query, @Nullable String job, @Nullable String city, @Nullable String periode, @Nullable String contactType) {
        Query jobQuery = db.collection("jobs");

        if (job != null && !job.isEmpty()) {
            jobQuery = jobQuery.whereEqualTo("jobTitle", job);
        }

        if (city != null && !city.isEmpty()) {
            jobQuery = jobQuery.whereEqualTo("city", city);
        }

        if (periode != null && !periode.isEmpty()) {
            jobQuery = jobQuery.whereEqualTo("startDate", periode);
        }

        if (contactType != null && !contactType.isEmpty() && !contactType.equals("Any")) {
            jobQuery = jobQuery.whereEqualTo("contractType", contactType);
        }

        jobQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cardContainer.removeAllViews();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String jobId = document.getId();
                    String companyId = document.getString("companyId");
                    String companyName = document.getString("companyName");
                    String pay = document.getString("salary");
                    String position = document.getString("jobTitle");
                    String location = document.getString("city");
                    String startDate = document.getString("startDate");
                    String description = document.getString("description");
                    String details = "Type of contract: " + document.getString("contractType");
                    if (query == null || position.toLowerCase().contains(query.toLowerCase())) {
                        fetchCompanyData(jobId, companyId, companyName, pay, position, location, startDate, description, details);
                    }
                }
            } else {
                Toast.makeText(ViewJobsOffers.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCompanyData(String jobId, String companyId, String companyName, String pay, String position, String location,
                                  String startDate, String description, String details) {
        db.collection("companies").document(companyId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String aboutCompany = document.getString("about");
                    String webSite = document.getString("website");
                    String email = document.getString("email");
                    String phone = document.getString("phone");
                    String fb = document.getString("facebook");
                    String linkedin = document.getString("linkedIn");

                    addJobCard(jobId, companyName, pay, position, location, startDate, description, details, aboutCompany, webSite, email, phone, fb, linkedin);
                }
            } else {
                Toast.makeText(ViewJobsOffers.this, "Error getting company data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addJobCard(String jobId, String companyName, String pay, String position, String location, String startDate,
                            String description, String details, String aboutCompany, String webSite, String email, String phone, String fb, String linkedin) {
        View jobCardView = getLayoutInflater().inflate(R.layout.card, cardContainer, false);

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
            Intent intent = new Intent(ViewJobsOffers.this, ViewJob.class);
            intent.putExtra("jobId", jobId);
            intent.putExtra("companyName", companyName);
            intent.putExtra("pay", pay);
            intent.putExtra("position", position);
            intent.putExtra("location", location);
            intent.putExtra("startDate", startDate);
            intent.putExtra("title", position + " at " + companyName);
            intent.putExtra("description", description);
            intent.putExtra("details", details);
            intent.putExtra("aboutCompany", aboutCompany);
            intent.putExtra("webSite", webSite);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            intent.putExtra("fb", fb);
            intent.putExtra("linkedin", linkedin);
            startActivity(intent);
        });

        cardContainer.addView(jobCardView);
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 40));
        cardContainer.addView(spacer);
    }
}
