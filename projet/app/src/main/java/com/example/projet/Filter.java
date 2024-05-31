package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class Filter extends AppCompatActivity {
    private EditText editTextJob, editTextCity, editTextPeriode;
    private Spinner spinnerContact;
    private Button buttonApply;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        editTextJob = findViewById(R.id.editTextJob);
        editTextCity = findViewById(R.id.editTextCity);
        editTextPeriode = findViewById(R.id.editTextPeriode);
        spinnerContact = findViewById(R.id.spinnerNationality);
        buttonApply = findViewById(R.id.buttonLogin);

        buttonApply.setOnClickListener(view -> applyFilters());
    }

    private void applyFilters() {
        String job = editTextJob.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String periode = editTextPeriode.getText().toString().trim();
        String contactType = spinnerContact.getSelectedItem().toString();

        Intent intent = new Intent(Filter.this, ViewJobsOffers.class);
        intent.putExtra("job", job);
        intent.putExtra("city", city);
        intent.putExtra("periode", periode);
        intent.putExtra("contactType", contactType);
        startActivity(intent);
        finish();
    }

}