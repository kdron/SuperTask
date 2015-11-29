package com.supertask.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, "supertask", null, 1);
    }

    public final static String table_registration = "table_registration";
    public final static String table_cloth = "table_cloth";


    public final static String email = "email";
    public final static String img_name = "img_name";
    public final static String bookmark = "bookmark";



    private String database_registration = "Create table " + table_registration + " ( "
            + email + " text );";
    private String database_table_cloth = "Create table " + table_cloth + " ( "
            + bookmark + " text ,"
            + img_name + " text );";




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(database_registration);
        db.execSQL(database_table_cloth);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + database_registration);
        db.execSQL("DROP TABLE IF EXISTS " + database_table_cloth);
        onCreate(db);
    }

}
