package com.cnidaria.ovbhafinal;

public class ContactContract {
    private ContactContract() {}

    public static class ContactEntry {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NUMBER = "number";
        public static final String _ID = "_id";
        public static final String COLUMN_STATUS = "status";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_NAME + " TEXT," +
                        COLUMN_NUMBER + " TEXT," +
                        COLUMN_STATUS + " BOOLEAN" +
                        ")";
        public static final String TABLE_NEARBY_SERVICES = "nearby_services";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_LATITUDE = "lat";
        public static final String COLUMN_LONGITUDE = "long";
        public static final String COLUMN_NAME_SERVICE = "name";
        public static final String COLUMN_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_OPEN_HOURS = "open_hours";

        public static final String CREATE_NEARBY_SERVICES_TABLE =
                "CREATE TABLE " + TABLE_NEARBY_SERVICES + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_IMAGE + " TEXT," +
                        COLUMN_LATITUDE + " REAL," +
                        COLUMN_LONGITUDE + " REAL," +
                        COLUMN_NAME_SERVICE + " TEXT," +
                        COLUMN_PHONE_NUMBER + " TEXT," +
                        COLUMN_OPEN_HOURS + " TEXT" +
                        ")";
        public static final String DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String DELETE_TABLE_SERVICES =
                "DROP TABLE IF EXISTS " + TABLE_NEARBY_SERVICES;
    }
}
