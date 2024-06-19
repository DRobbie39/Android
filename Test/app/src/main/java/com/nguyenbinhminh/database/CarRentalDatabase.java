package com.nguyenbinhminh.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CarRentalDatabase extends SQLiteOpenHelper {
    public static final String DB_NAME = "cars.sqlite";
    public static final int DB_VERSION = 1;
    public static final String TBL_NAME = "CarRental";
    public static final String COL_CODE = "CarRentalCode";
    public static final String COL_NAME = "CarName";
    public static final String COL_IMV = "CarImv";
    public static final String COL_RENTAL_LOCATION = "CarRentalLocation";
    public static final String COL_TYPE = "CarType";
    public static final String COL_PRICE = "RentalPrice";

    public CarRentalDatabase(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TBL_NAME + " (" + COL_CODE + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NAME + " VARCHAR(50), "  + COL_PRICE   + " REAL, " +  COL_IMV + " BLOB," + COL_RENTAL_LOCATION  + " VARCHAR(150), "+ COL_TYPE + " VARCHAR(50))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_NAME);
        onCreate(db);
    }

    //SELECT...

    public Cursor getData (String sql) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            return db.rawQuery(sql, null);
        } catch (Exception e) {
            return null;
        }
    }

    //UPDATE, DELETE,...

    public boolean execSql (String sql) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //INSERT

    public boolean insertData(String name, double price, byte[] imv, String loc, String type) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql = "INSERT INTO " + TBL_NAME + " VALUES (null, ?, ?, ?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.clearBindings();
            statement.bindString(1, name);
            statement.bindDouble(2, price);
            statement.bindBlob(3, imv);
            statement.bindString(4, loc);
            statement.bindString(5, type);

            statement.executeInsert();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateData(int id, String name, double price, byte[] imv, String loc, String type) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql = "UPDATE " + TBL_NAME + " SET "
                    + CarRentalDatabase.COL_NAME + " = ?, "
                    + CarRentalDatabase.COL_PRICE + " = ?, "
                    + CarRentalDatabase.COL_IMV + " = ?, "
                    + CarRentalDatabase.COL_RENTAL_LOCATION + " = ?, "
                    + CarRentalDatabase.COL_TYPE + " = ? "
                    + "WHERE " + CarRentalDatabase.COL_CODE + " = ?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.clearBindings();
            statement.bindString(1, name);
            statement.bindDouble(2, price);
            statement.bindBlob(3, imv);
            statement.bindString(4, loc);
            statement.bindString(5, type);
            statement.bindLong(6, id);

            statement.executeUpdateDelete();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Cursor searchData(String name, double fromPrice, double toPrice, String carTypeArray) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TBL_NAME + " WHERE 1=1";

        List<String> args = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            query += " AND " + COL_NAME + " LIKE ?";
            args.add("%" + name + "%");
        }

        if (fromPrice >= 0 && toPrice >= 0 && fromPrice <= toPrice) {
            query += " AND " + COL_PRICE + " BETWEEN ? AND ?";
            args.add(String.valueOf(fromPrice));
            args.add(String.valueOf(toPrice));
        }

        if (!carTypeArray.equals("All") && !carTypeArray.isEmpty()) {
            query += " AND " + COL_TYPE + " = ?";
            args.add(carTypeArray);
        }

        Cursor cursor = db.rawQuery(query, args.toArray(new String[0]));

        return cursor;
    }
}