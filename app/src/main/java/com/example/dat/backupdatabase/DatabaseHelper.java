package com.example.dat.backupdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "note.db";
    public static final String TABLE_NOTE = "tb_note";
    public static final String KEY_ID_NOTE = "id";
    public static final String KEY_TITLE_NOTE = "title";
    public static final String KEY_CONTENT_NOTE = "content";
    public static final String KEY_LAST_MODIFIED_NOTE = "last_modified";

    /**
     * string for create table note
     */
    public static final String CREATE_TABLE_NOTE =
            "CREATE TABLE " + TABLE_NOTE + "(" +
                    KEY_ID_NOTE + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                    ", " + KEY_TITLE_NOTE + " TEXT NOT NULL" +
                    "," + KEY_CONTENT_NOTE + " TEXT NOT NULL" +
                    "," + KEY_LAST_MODIFIED_NOTE + " TEXT DEFAULT \'\'" +
                    ")";

    /**
     * value for update database
     */
    public static final int DATA_VERSION = 2;

    /**
     * Sqlite database
     */
    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATA_VERSION);
    }

    /**
     * create db when app start, and only call when database don't create
     * When database created, it will not call
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_NOTE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * call when change DATA_VERSION
     * help we update database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // update database for database version < 2
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NOTE + " ADD COLUMN " + KEY_LAST_MODIFIED_NOTE + " TEXT DEFAULT \'\'");
        }
    }

    /**
     * open database
     */
    public void open() {
        try {
            db = getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * close database
     */
    public void close() {
        if (db != null && db.isOpen()) {
            try {
                db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /************************* method work with database *******************/

    /**
     * get all row of table with sql command then return cursor
     * cursor move to frist to redy for get data
     */
    public Cursor getAll(String sql) {
        open();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        close();
        return cursor;
    }

    /**
     * insert contentvaluse to table
     *
     * @param values value of data want insert
     * @return index row insert
     */
    public long insert(String table, ContentValues values) {
        open();
        long index = db.insert(table, null, values);
        close();
        return index;
    }

    /**
     * update values to table
     *
     * @return index row update
     */
    public boolean update(String table, ContentValues values, String where) {
        open();
        long index = db.update(table, values, where, null);
        close();
        return index > 0;
    }

    /**
     * delete id row of table
     */
    public boolean delete(String table, String where) {
        open();
        long index = db.delete(table, where, null);
        close();
        return index > 0;
    }


    /************************* end of method work with database *******************/

    /************************* method work with note table *******************/
    /**
     * get Note by sql command
     *
     * @param sql sql to get note
     */
    public Note getNote(String sql) {
        Note note = null;
        Cursor cursor = getAll(sql);
        if (cursor != null) {
            note = cursorToNote(cursor);
            cursor.close();
        }
        return note;
    }

    /**
     * @param sql get all notes with sql command
     * @return arraylist of note
     */
    public ArrayList<Note> getListNote(String sql) {
        ArrayList<Note> list = new ArrayList<>();
        Cursor cursor = getAll(sql);

        while (!cursor.isAfterLast()) {
            list.add(cursorToNote(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }

    /**
     * insert note to table
     *
     * @param note note to insert
     * @return id of note
     */
    public long insertNote(Note note) {
        return insert(TABLE_NOTE, noteToValues(note));
    }

    /**
     * @param note note to update
     * @return id of note update
     */
    public boolean updateNote(Note note) {
        return update(TABLE_NOTE, noteToValues(note), KEY_ID_NOTE + " = " + note.getId());
    }

    /**
     * delete id row of table
     */
    public boolean deleteNote(String where) {
        return delete(TABLE_NOTE, where);
    }

    /**
     * convert note to contentvalues
     * don't put id of note because
     * when insert id will auto create
     * when update we don't update id
     */
    private ContentValues noteToValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE_NOTE, note.getTitle());
        values.put(KEY_CONTENT_NOTE, note.getContent());
        values.put(KEY_LAST_MODIFIED_NOTE, note.getLastModified());
        return values;
    }

    /**
     * convert cursor to note
     */
    public Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_NOTE)))
                .setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE_NOTE)))
                .setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT_NOTE)))
                .setLastModified(cursor.getString(cursor.getColumnIndex(KEY_LAST_MODIFIED_NOTE)));
        return note;
    }
}
