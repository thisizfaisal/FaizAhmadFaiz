package com.example.faizahmadfaiz.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class DatabaseHandler {

    public static final String backup = "-backup"; //value to be appended to file name when renaming (pseudo delete)
    static final String[] tempFiles = new String[]{"-journal", "-wal", "-shm"}; // temporary files to rename

    /**
     * Check if the database already exists. NOTE will create the databases folder is it doesn't exist
     *
     * @return true if it exists, false if it doesn't
     */
    public static boolean checkDataBase(Context context, String dbName) {

        File db = new File(context.getDatabasePath(dbName).getPath()); //Get the file name of the database
        if (db.exists()) return true; // If it exists then return doing nothing

        // Get the parent (directory in which the database file would be)
        File dbdir = db.getParentFile();
        // If the directory does not exits then make the directory (and higher level directories)
        if (!dbdir.exists()) {
            db.getParentFile().mkdirs();
            dbdir.mkdirs();
        }
        return false;
    }

    public static void copyDataBase(Context context, String dbName, String assetFileName, boolean deleteExistingDB, int version) {

        checkpointIfWALEnabled(context, dbName);
        int stage = 0, buffer_size = 4096, blocks_copied = 0, bytes_copied = 0;
        File f = new File(context.getDatabasePath(dbName).toString());
        InputStream is;
        OutputStream os;

        /**
         * If forcing then effectively delete (rename) current database files
         */
        if (deleteExistingDB) {
            f.renameTo(context.getDatabasePath(dbName + backup));
            for (String s : tempFiles) {
                File tmpf = new File(context.getDatabasePath(dbName + s).toString());
                if (tmpf.exists()) {
                    tmpf.renameTo(context.getDatabasePath(dbName + s + backup));
                }
            }
        }

        //Open your local db as the input stream
        try {
            is = context.getAssets().open(assetFileName); // Open the Asset file
            stage++;

            os = new FileOutputStream(f);
            stage++;
            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[buffer_size];
            int length;
            while ((length = is.read(buffer)) > 0) {
                blocks_copied++;
                os.write(buffer, 0, length);
                bytes_copied += length;
            }
            stage++;
            //Close the streams
            os.flush();
            stage++;
            os.close();
            stage++;
            is.close();
            if (version > 0) {
                setVersion(context, dbName, version);
            }
        } catch (IOException e) {
            String exception_message = "";
            e.printStackTrace();
            switch (stage) {
                case 0:
                    exception_message = "Error trying to open the asset " + dbName;
                    break;
                case 1:
                    exception_message = "Error opening Database file for output, path is " + f.getPath();
                    break;
                case 2:
                    exception_message = "Error flushing written database file " + f.getPath();
                    break;
                case 3:
                    exception_message = "Error closing written database file " + f.getPath();
                    break;
                case 4:
                    exception_message = "Error closing asset file " + f.getPath();

            }
            throw new RuntimeException("Unable to copy the database from the asset folder." + exception_message + " see starck-trace above.");
        }
    }


    public static void copyDataBase(Context context, String dbName, boolean deleteExistingDB, int version) {
        copyDataBase(context, dbName, dbName, deleteExistingDB, version);
    }


    private static void checkpointIfWALEnabled(Context context, String dbName) {
        Cursor csr;
        int wal_busy = -99, wal_log = -99, wal_checkpointed = -99;
        if (!new File(context.getDatabasePath(dbName).getPath()).exists()) {
            return;
        }
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(dbName).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        csr = db.rawQuery("PRAGMA journal_mode", null);
        if (csr.moveToFirst()) {
            String mode = csr.getString(0);
            if (mode.toLowerCase().equals("wal")) {
                csr = db.rawQuery("PRAGMA wal_checkpoint", null);
                if (csr.moveToFirst()) {
                    wal_busy = csr.getInt(0);
                    wal_log = csr.getInt(1);
                    wal_checkpointed = csr.getInt(2);
                }
                csr = db.rawQuery("PRAGMA wal_checkpoint(TRUNCATE)", null);
                csr.getCount();
                csr = db.rawQuery("PRAGMA wal_checkpoint", null);
                if (csr.moveToFirst()) {
                    wal_busy = csr.getInt(0);
                    wal_log = csr.getInt(1);
                    wal_checkpointed = csr.getInt(2);
                }
            }
        }
        csr.close();
        db.close();
    }

    private static void setVersion(Context context, String dbName, int version) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(dbName).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        db.setVersion(version);
        db.close();

    }
}