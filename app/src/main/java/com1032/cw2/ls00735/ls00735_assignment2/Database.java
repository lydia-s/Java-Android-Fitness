package com1032.cw2.ls00735.ls00735_assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by savou on 16/05/2017.
 * The database class stores the distance a runner has travelled
 */
public class Database extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private final static String TABLE_DISTANCE = "distance";

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d( "[Database::onCreate]", "Creating distance table" );
        createTable(db);
        addDistanceRow(db);

    }
    /*
    createTable creates a table to store distance
     */
    private void createTable(SQLiteDatabase db) {
        String createSQL = "CREATE TABLE " + TABLE_DISTANCE +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "distance REAL);";
        db.execSQL(createSQL);

    }
    /*
    onUpgrade drops existing table and calls methods createTable and addDistanceRow
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String dropSQL = "DROP TABLE IF EXISTS" + TABLE_DISTANCE + ";";
        db.execSQL(dropSQL);

        createTable(db);
        addDistanceRow(db);
        Log.d("[Database::onUpgrade]", "Upgrading DB");
    }

    /*
    addDistanceRow adds a value for distance to the database
     */

    private void addDistanceRow(SQLiteDatabase sqLiteDatabase)
    {
        ContentValues values = new ContentValues();
        values.put("distance", 0.0);

        sqLiteDatabase.insert(TABLE_DISTANCE, null, values);
    }
    /*
    clearDistance sets the value of distance to 0.0 resetting the distance value
     */
    public void clearDistance(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("distance", 0.0);

        sqLiteDatabase.update(TABLE_DISTANCE, values, "id=1", null);
    }

    /*
    updateDistance sets the distance to the value of the input distance
     */
    public void updateDistance(double distance)
    {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("distance", distance);

        sqLiteDatabase.update(TABLE_DISTANCE, values, "id=1", null);
    }

    /*
    getDistance method allows you to read the distance from the database
     */
    public double getDistance()
    {
        double distance = 0.0;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT distance FROM " +
                TABLE_DISTANCE +
                " WHERE id = '1'", null);

        if(cursor.moveToFirst())
        {
            distance = cursor.getDouble(cursor.getColumnIndex("distance"));
        }
        return distance;
    }
}
