package com.alexandrunica.allcabins.service.database.model;

import android.provider.BaseColumns;

public class DatabaseUserModel {

    private DatabaseUserModel() {}

    public static class UserEntity implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PHOTO = "profilePhoto";
        public static final String COLUMN_NAME_FAVORITES = "favorites";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_PHONE = "phone";
    }
}
