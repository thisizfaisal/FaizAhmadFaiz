package com.example.faizahmadfaiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.faizahmadfaiz.MainActivity;
import com.example.faizahmadfaiz.R;
import com.example.faizahmadfaiz.database.DatabaseHelper;
import com.example.faizahmadfaiz.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
public class DetailActivity extends AppCompatActivity {
    TextView tvQuote;
    DatabaseHelper mDBHelper;
    String id, quote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Detail");
        }

        mDBHelper = new DatabaseHelper(this);
        // Get Data
        id = getIntent().getStringExtra(Constants.STRING_EXTRA_ID);
        quote = getIntent().getStringExtra(Constants.STRING_EXTRA_QUOTE);

        tvQuote = findViewById(R.id.tvquote);
        tvQuote.setText(quote);

        FloatingActionButton fab = findViewById(R.id.fab_edit_quote);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(DetailActivity.this, EditActivity.class);
            intent.putExtra(Constants.STRING_EXTRA_ID, id);
            intent.putExtra(Constants.STRING_EXTRA_QUOTE, quote);
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuIid = item.getItemId();
        // Android default back button
        if (menuIid == android.R.id.home) {
            onBackPressed();
            return true;


        }
        if (menuIid == R.id.action_delete) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are You sure To Delete");
            builder.setIcon(R.drawable.ic_delete);

            builder.setPositiveButton("Yes", (dialog, id2) -> {

                mDBHelper.deleteQuote(id);
                startActivity(new Intent(DetailActivity.this, MainActivity.class));
                finishAffinity();
            });
            builder.setNegativeButton("No", (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}