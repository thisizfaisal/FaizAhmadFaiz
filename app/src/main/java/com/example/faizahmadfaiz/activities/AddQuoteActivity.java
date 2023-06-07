package com.example.faizahmadfaiz.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.faizahmadfaiz.R;
import com.example.faizahmadfaiz.database.DatabaseHelper;
import com.example.faizahmadfaiz.utils.Helper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class AddQuoteActivity extends AppCompatActivity {

    EditText editTextQuote;
    FloatingActionButton buttonSaveQuote, buttonPasteQuote;
    DatabaseHelper mDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add Quote");
        }

        mDBHelper = new DatabaseHelper(this);

        editTextQuote = findViewById(R.id.editTextAddQuote);
        buttonSaveQuote = findViewById(R.id.buttonSaveQuote);
        buttonPasteQuote = findViewById(R.id.buttonPasteQuote);


        buttonPasteQuote.setOnClickListener(view -> {
            String pasteData = Helper.getClipboardData(this);
                if(!TextUtils.isEmpty(pasteData)){
                    editTextQuote.setText(pasteData);
                }
        });

        buttonSaveQuote.setOnClickListener(v -> {

            String quote = editTextQuote.getText().toString().trim();
            if (!TextUtils.isEmpty(quote)) {

                boolean isAdded = mDBHelper.addQuote(quote);
                if (isAdded) {
                    Snackbar.make(v, "Quote Added", Snackbar.LENGTH_LONG).setTextColor(getResources()
                                    .getColor(R.color.white, null))
                            .setBackgroundTint(Color.parseColor("#00C853")).show();
                    editTextQuote.getText().clear();
                } else

                    Snackbar.make(v, "Quote Not Added", Snackbar.LENGTH_LONG).setTextColor(getResources()
                                    .getColor(R.color.white, null)).setBackgroundTint(getResources()
                                    .getColor(com.google.android.material.R.color.design_default_color_error, null))
                            .show();
            } else {
                editTextQuote.setError("Required");
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