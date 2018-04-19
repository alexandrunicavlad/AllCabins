package com.alexandrunica.allcabins.service.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.profile.model.User;
import com.alexandrunica.allcabins.service.database.model.DatabaseUserModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class DatabaseService extends SQLiteOpenHelper {

    @Inject
    Context context;
    @Inject
    Bus bus;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "allcabins.db";
    private SQLiteDatabase mDb;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseUserModel.UserEntity.TABLE_NAME + " (" +
                    DatabaseUserModel.UserEntity.COLUMN_NAME_ID + " TEXT," +
                    DatabaseUserModel.UserEntity.COLUMN_NAME_NAME + " TEXT," +
                    DatabaseUserModel.UserEntity.COLUMN_NAME_EMAIL + " TEXT," +
                    DatabaseUserModel.UserEntity.COLUMN_NAME_PHOTO + " TEXT," +
                    DatabaseUserModel.UserEntity.COLUMN_NAME_FAVORITES + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseUserModel.UserEntity.TABLE_NAME;

    public DatabaseService(DaggerDbApplication app) {
        super(app, DATABASE_NAME, null, DATABASE_VERSION);
        app.getAppDbComponent().inject(this);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean isTableExists(String tableName, boolean openDb) {
        getDatabase();
        if (openDb) {
            if (mDb == null || !mDb.isOpen()) {
                mDb = getReadableDatabase();
            }

            if (!mDb.isReadOnly()) {
                mDb.close();
                mDb = getReadableDatabase();
            }
        }

        Cursor cursor = mDb.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    private SQLiteDatabase getDatabase() {
        if (mDb != null && mDb.isOpen())
            return mDb;
        mDb = getWritableDatabase();
        return mDb;
    }

    public void createUserTable() {
        getDatabase();
        try {
            mDb.execSQL(SQL_CREATE_ENTRIES);
        } catch (Exception e) {
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void writeUser(User user) {
        getDatabase();

        boolean exists = isTableExists(DatabaseUserModel.UserEntity.TABLE_NAME, false);
        if (!exists) {
            createUserTable();
        }
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseUserModel.UserEntity.COLUMN_NAME_ID, user.getId());
            values.put(DatabaseUserModel.UserEntity.COLUMN_NAME_NAME, user.getUsername());
            values.put(DatabaseUserModel.UserEntity.COLUMN_NAME_EMAIL, user.getEmail());
            values.put(DatabaseUserModel.UserEntity.COLUMN_NAME_PHOTO, user.getProfilePhoto());
            values.put(DatabaseUserModel.UserEntity.COLUMN_NAME_FAVORITES, new Gson().toJson(user.getFavoriteList()));
            long newRowId = mDb.insert(DatabaseUserModel.UserEntity.TABLE_NAME, null, values);
        } catch (Exception e) {
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUser(User user) {
        getDatabase();

        boolean exists = isTableExists(DatabaseUserModel.UserEntity.TABLE_NAME, false);
        if (!exists) {
            createUserTable();
        }
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseUserModel.UserEntity.COLUMN_NAME_ID, user.getId());
            values.put(DatabaseUserModel.UserEntity.COLUMN_NAME_NAME, user.getUsername());
            values.put(DatabaseUserModel.UserEntity.COLUMN_NAME_EMAIL, user.getEmail());
            values.put(DatabaseUserModel.UserEntity.COLUMN_NAME_PHOTO, user.getProfilePhoto());
            values.put(DatabaseUserModel.UserEntity.COLUMN_NAME_FAVORITES, new Gson().toJson(user.getFavoriteList()));
            long newRowId = mDb.update(DatabaseUserModel.UserEntity.TABLE_NAME, values, DatabaseUserModel.UserEntity.COLUMN_NAME_ID + "= ?", new String[]{user.getId()});

        } catch (Exception e) {
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteUser() {
        getDatabase();

        boolean exists = isTableExists(DatabaseUserModel.UserEntity.TABLE_NAME, false);
        if (exists) {
            mDb.execSQL("delete from " + DatabaseUserModel.UserEntity.TABLE_NAME);
        }
    }

    public void updateOrAdd(User user) {
        if (checkUser(user.getId())) {
            updateUser(user);
        }
    }

    public boolean checkUser(String fieldValue) {
        getDatabase();
        String[] projection = {
                DatabaseUserModel.UserEntity.COLUMN_NAME_ID
        };

        String selection = DatabaseUserModel.UserEntity.COLUMN_NAME_ID + " LIKE ? ";

        Cursor c = mDb.query(
                DatabaseUserModel.UserEntity.TABLE_NAME,  // Your Table Name
                projection,
                selection,
                new String[]{fieldValue},
                null,
                null,
                null
        );

        if (c.getCount() <= 0) {
            c.close();
            return false;
        }
        c.close();
        return true;
    }

    public User getUser () {
        String createString = "SELECT * from " + DatabaseUserModel.UserEntity.TABLE_NAME;
        getDatabase();

        try {
            Cursor cursor = mDb.rawQuery(createString, null);
            while (cursor.moveToNext()) {
                try {
                    String idField = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUserModel.UserEntity.COLUMN_NAME_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUserModel.UserEntity.COLUMN_NAME_NAME));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUserModel.UserEntity.COLUMN_NAME_EMAIL));
                    String photo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUserModel.UserEntity.COLUMN_NAME_PHOTO));
                    String favorites = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUserModel.UserEntity.COLUMN_NAME_FAVORITES));
                    HashMap<String, String> fav = new Gson().fromJson(favorites, new TypeToken<HashMap<String, String>>(){}.getType());
                    User user = new User(idField, email,  name, photo, fav);
                    return user;
                } catch (Exception e) {
                    //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            cursor.close();
        } catch (Exception e) {
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}

