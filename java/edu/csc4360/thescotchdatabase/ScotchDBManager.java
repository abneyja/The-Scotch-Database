package edu.csc4360.thescotchdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ScotchDBManager {
    private Context context;
    private SQLiteDatabase database;
    private ScotchDbHelper dbHelper;

    public ScotchDBManager(Context c){
        this.context = c;
    }

    public ScotchDBManager open() throws SQLException {
        this.dbHelper = ScotchDbHelper.getInstance(this.context);
        this.database = this.dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.dbHelper.close();
    }

    public void insertScotch(String name, float rating, String notes, boolean favorite, String image){
        ContentValues values = new ContentValues();

        values.put(ScotchDbHelper.KEY_NAME, name);
        values.put(ScotchDbHelper.KEY_RATING, rating);
        values.put(ScotchDbHelper.KEY_NOTES, notes);
        values.put(ScotchDbHelper.KEY_FAVORITE, favorite);
        values.put(ScotchDbHelper.KEY_IMAGE, image);
        this.database.insert(ScotchDbHelper.TABLE_NAME, null, values);
    }

    public Cursor getAllScotch() {
        Cursor cursor = this.database.query(ScotchDbHelper.TABLE_NAME, new String[]{ScotchDbHelper.KEY_ID, ScotchDbHelper.KEY_NAME,
                ScotchDbHelper.KEY_RATING, ScotchDbHelper.KEY_NOTES, ScotchDbHelper.KEY_FAVORITE, ScotchDbHelper.KEY_IMAGE},
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getScotch(long uid) {
        Cursor cursor = this.database.rawQuery("select * from "+ ScotchDbHelper.TABLE_NAME +" where _scotch = "+uid,null);
        return cursor;
    }

    public Cursor getData(String sql){
        return this.database.rawQuery(sql, null);
    }

    public boolean deleteScotch(long id) {
        int rowsDeleted = database.delete(ScotchDbHelper.TABLE_NAME, ScotchDbHelper.KEY_ID + " = ?",
                new String[] { Long.toString(id) });
        return rowsDeleted > 0;
    }

    public boolean updateScotch(long id, String name, float rating, String notes, boolean favorite, String image) {
        ContentValues values = new ContentValues();
        values.put(ScotchDbHelper.KEY_NAME, name);
        values.put(ScotchDbHelper.KEY_RATING, rating);
        values.put(ScotchDbHelper.KEY_NOTES, notes);
        values.put(ScotchDbHelper.KEY_FAVORITE, favorite);
        values.put(ScotchDbHelper.KEY_IMAGE, image);

        int rowsUpdated = database.update(ScotchDbHelper.TABLE_NAME, values, "_scotch = ?",
                new String[] { Long.toString(id) });
        return rowsUpdated > 0;
    }
}
