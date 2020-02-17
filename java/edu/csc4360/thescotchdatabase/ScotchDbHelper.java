package edu.csc4360.thescotchdatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScotchDbHelper extends SQLiteOpenHelper{
    private static ScotchDbHelper mInstance = null;

    private static final String DATABASE_NAME = "scotch.db";
    public static final String TABLE_NAME = "scotch_db";
    // when the structure of the database is changed, change the version number from 1 to 2
    private static final int DATABASE_VERSION = 1;
    public static final String KEY_ID = "_scotch";
    public static final String KEY_NAME = "name";
    public static final String KEY_RATING = "rating";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FAVORITE = "fav_flag";

    public static ScotchDbHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new ScotchDbHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private ScotchDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" +
                KEY_ID + " integer primary key autoincrement, " +
                KEY_NAME + " text, " +
                KEY_RATING + " float, " +
                KEY_NOTES + " text, " +
                KEY_FAVORITE + " boolean, " +
                KEY_IMAGE + " text) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
}
