package com.example.faizahmadfaiz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.faizahmadfaiz.activities.AddQuoteActivity;
import com.example.faizahmadfaiz.adapters.QuoteAdapter;
import com.example.faizahmadfaiz.database.DatabaseHelper;
import com.example.faizahmadfaiz.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    public static final int STORAGE_PERMISSION_CODE = 1;
    boolean doubleBackPressed = false;
    private RecyclerView rvQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvQuote = findViewById(R.id.recyclerviewQuotes);
        rvQuote.setHasFixedSize(true);
        rvQuote.setLayoutManager(new LinearLayoutManager(this));
        rvQuote.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        initData();

        FloatingActionButton fab = findViewById(R.id.fab_add_Quote);
        fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AddQuoteActivity.class)));

    }

    @SuppressLint("SetTextI18n")
    private void initData() {

        QuoteAdapter quoteAdapter = new QuoteAdapter(this, new DatabaseHelper(this).getQuotes());
        rvQuote.setAdapter(quoteAdapter);

        TextView quoteSize = findViewById(R.id.quoteSize);
        quoteSize.setText(getString(R.string.total_quotes) + quoteAdapter.getItemCount());

    }

    @Override
    public void onBackPressed() {

        if (doubleBackPressed) {
            super.onBackPressed();
            return;
        }
        this.doubleBackPressed = true;
        Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackPressed = false, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuIid = item.getItemId();

        if (menuIid == R.id.action_backup) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Backup Database");


            if (!getDbName().endsWith(".db")) {
                // Set up the input
                EditText input = new EditText(this);
                // Specify the type of input expected; this, for example,
                // sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(input.getText().toString());
                input.setHint("DB Name");
                builder.setView(input);

                builder.setNeutralButton("Save DB Name", (dialog, which) -> saveDbName(input.getText().toString() + ".db"));
            }
            builder.setPositiveButton("Yes", (dialog, id2) -> {

                if (checkStoragePermission()) {
                    backupDB();
                } else {
                    requestStoragePermission();
                }
            });
            builder.setNegativeButton("No", (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void backupDB() {
        try {
            ContentResolver contentResolver = getContentResolver();

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, getDbName());
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.sqlite3");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS
                    + "/Databases");

            Uri contentUri = MediaStore.Files.getContentUri("external");
            Uri itemUri = contentResolver.insert(contentUri, contentValues);

            if (itemUri != null) {
                OutputStream outputStream = contentResolver.openOutputStream(itemUri);
                if (outputStream != null) {
                    File currentDb = getDatabasePath(Constants.DB_NAME);

                    FileInputStream fileInputStream = new FileInputStream(currentDb);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fileInputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }

                    fileInputStream.close();
                    outputStream.close();

                    Toast.makeText(this, "Backup Successful  " , Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Backup Failed: Unable to open output stream",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Backup Failed: Unable to create item in MediaStore",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Backup Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                backupDB();
            } else {
                Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void saveDbName(String dbName) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("db name", dbName);
        editor.apply();
    }

    private String getDbName() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString("db name", "");
    }
}