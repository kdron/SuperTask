package com.supertask.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ConstantClass {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;


    Context context;


    public ConstantClass(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);

    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    public void deleteUser() {
        open();
        long delete = database.delete(DatabaseHelper.table_registration, null, null);
        close();

    }


    public void insertUser(String email) {
        open();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.email, email);

        long insert = database.insert(DatabaseHelper.table_registration, null, values);
        System.out.print("");
        close();
    }

    public Cursor getUser() {

        open();
        Cursor cursor = database.query(DatabaseHelper.table_registration, null, null, null, null, null, null, null);
        cursor.moveToFirst();


        return cursor;
    }

    public void createFolder() {
        File myDirectory = new File(Environment.getExternalStorageDirectory(), Constant.folder);

        if (!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
    }

    public void insertCloths(String name) {
        open();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.img_name, name);
        values.put(DatabaseHelper.bookmark, Constant.default_bookmark);

        long insert = database.insert(DatabaseHelper.table_cloth, null, values);
        System.out.print("");
        close();
    }

    public Cursor getCloths() {

        open();
        Cursor cursor = database.query(DatabaseHelper.table_cloth, null, null, null, null, null, null, null);
        cursor.moveToFirst();


        return cursor;
    }

    public Cursor getCloths(String name) {

        open();
        Cursor cursor = database.query(DatabaseHelper.table_cloth, null, DatabaseHelper.img_name + " = ? ", new String[]{name}, null, null, null, null);

        cursor.moveToFirst();

        close();
        return cursor;
    }
    public void bookmarkCloth(String img_name, String bookmark) {
        open();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.img_name, img_name);
        values.put(DatabaseHelper.bookmark, bookmark);



        long update = database.update(DatabaseHelper.table_cloth, values,
                DatabaseHelper.img_name + " = ? ", new String[]{img_name});
        System.out.print("");
        close();

    }

}

