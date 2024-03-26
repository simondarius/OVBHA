package com.cnidaria.ovbhafinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DBHandler {
    private SQLiteDatabase database;
    private DbHelper dbHelper;

    public DBHandler(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertContact(String name, String number) {
        ContentValues values = new ContentValues();
        values.put(ContactContract.ContactEntry.COLUMN_NAME, name);
        values.put(ContactContract.ContactEntry.COLUMN_NUMBER, number);
        values.put(ContactContract.ContactEntry.COLUMN_STATUS, "0");
        return database.insert(ContactContract.ContactEntry.TABLE_NAME, null, values);
    }


    public Cursor getAllContacts() {
        return database.query(
                ContactContract.ContactEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
    public void updateContactStatus(long contactId, boolean isFavourite) {
        ContentValues values = new ContentValues();
        // Convert boolean value to integer (0 or 1)
        int status = isFavourite ? 1 : 0;
        values.put(ContactContract.ContactEntry.COLUMN_STATUS, status);

        // Update the status for the specific contact
        database.update(ContactContract.ContactEntry.TABLE_NAME, values,
                ContactContract.ContactEntry._ID + " = ?", new String[]{String.valueOf(contactId)});
    }
    public List<String> getNumbersWithTrueStatus() {
        List<String> numbers = new ArrayList<>();

        String query = "SELECT " + ContactContract.ContactEntry.COLUMN_NUMBER +
                " FROM " + ContactContract.ContactEntry.TABLE_NAME +
                " WHERE " + ContactContract.ContactEntry.COLUMN_STATUS + " = 1";


        Cursor cursor = database.rawQuery(query, null);


        if (cursor != null && cursor.moveToFirst()) {
            do {

                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NUMBER));

                numbers.add(number);
            } while (cursor.moveToNext());


            cursor.close();
        }


        return numbers;
    }
    public void insertNearbyService(String image, double latitude, double longitude, String name,
                                    String phoneNumber, String openHours) {
        ContentValues values = new ContentValues();
        values.put(ContactContract.ContactEntry.COLUMN_IMAGE, image);
        values.put(ContactContract.ContactEntry.COLUMN_LATITUDE, latitude);
        values.put(ContactContract.ContactEntry.COLUMN_LONGITUDE, longitude);
        values.put(ContactContract.ContactEntry.COLUMN_NAME_SERVICE, name);
        values.put(ContactContract.ContactEntry.COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(ContactContract.ContactEntry.COLUMN_OPEN_HOURS, openHours);

        database.insert(ContactContract.ContactEntry.TABLE_NEARBY_SERVICES, null, values);
    }

    public List<ContentValues> getNearbyServices() {
        List<ContentValues> servicesList = new ArrayList<>();

        Cursor cursor = database.query(
                ContactContract.ContactEntry.TABLE_NEARBY_SERVICES,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ContentValues values = new ContentValues();
                values.put(ContactContract.ContactEntry._ID, cursor.getLong(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry._ID)));
                values.put(ContactContract.ContactEntry.COLUMN_IMAGE, cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_IMAGE)));
                values.put(ContactContract.ContactEntry.COLUMN_LATITUDE, cursor.getDouble(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_LATITUDE)));
                values.put(ContactContract.ContactEntry.COLUMN_LONGITUDE, cursor.getDouble(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_LONGITUDE)));
                values.put(ContactContract.ContactEntry.COLUMN_NAME_SERVICE, cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME)));
                values.put(ContactContract.ContactEntry.COLUMN_PHONE_NUMBER, cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_PHONE_NUMBER)));
                values.put(ContactContract.ContactEntry.COLUMN_OPEN_HOURS, cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_OPEN_HOURS)));
                servicesList.add(values);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return servicesList;
    }

    public void addNearbyServicesFromJSON(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String image = jsonObject.getString("image");
                double latitude = jsonObject.getDouble("lat");
                double longitude = jsonObject.getDouble("long");
                String name = jsonObject.getString("name");
                String phoneNumber = jsonObject.getString("phone_number");
                String openHours = jsonObject.getString("open_hours");

                insertNearbyService(image, latitude, longitude, name, phoneNumber, openHours);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteContact(long contactId) {
        // Delete the contact from the database
        database.delete(ContactContract.ContactEntry.TABLE_NAME,
                ContactContract.ContactEntry._ID + " = ?", new String[]{String.valueOf(contactId)});
    }
}
