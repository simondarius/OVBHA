package com.cnidaria.ovbhafinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "my_database.db";
    private static final int DATABASE_VERSION = 3;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ContactContract.ContactEntry.CREATE_TABLE);
        db.execSQL(ContactContract.ContactEntry.CREATE_NEARBY_SERVICES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ContactContract.ContactEntry.DELETE_TABLE);
        db.execSQL(ContactContract.ContactEntry.DELETE_TABLE_SERVICES);
        onCreate(db);
    }
}

