package com.example.faizahmadfaiz.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.faizahmadfaiz.MainActivity;
import com.example.faizahmadfaiz.R;
import com.example.faizahmadfaiz.database.DatabaseHelper;
import com.example.faizahmadfaiz.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class EditActivity extends AppCompatActivity {

    DatabaseHelper mDBHelper;
    EditText edQuote;
    FloatingActionButton fabSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Quote");

        mDBHelper = new DatabaseHelper(this);
        edQuote = findViewById(R.id.editTextEditQuote);
        fabSave = findViewById(R.id.fab_save);

        // Get Data
        final String id = getIntent().getStringExtra(Constants.STRING_EXTRA_ID);
        final String quote = getIntent().getStringExtra(Constants.STRING_EXTRA_QUOTE);

        edQuote.setText(quote);

        fabSave.setOnClickListener(view -> {
            boolean isUpdated = mDBHelper.editQuote(id, edQuote.getText().toString().trim());
            if (isUpdated) {

                Snackbar.make(view, "Quote Updated", Snackbar.LENGTH_LONG).setTextColor(getResources()
                                .getColor(R.color.white, null))
                        .setBackgroundTint(Color.parseColor("#00C853")).show();
                edQuote.getText().clear();

                startActivity(new Intent(EditActivity.this, MainActivity.class));
                finishAffinity();

            } else {
                Snackbar.make(view, "Quote Not Updated", Snackbar.LENGTH_LONG).setTextColor(getResources()
                                .getColor(R.color.white, null)).setBackgroundTint(getResources()
                                .getColor(com.google.android.material.R.color.design_default_color_error, null))
                        .show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        // Android default back button
        if (id == android.R.id.home) {
            onBackPressed();
            return true;


        }
        return super.onOptionsItemSelected(item);
    }
}