package com.abogados.proyectog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class bd {

    private static final String sessionId = "_id";

    private static final String user_id = "user_id";
    private static final String userEmail = "email";
    private static final String token = "token";

    private static final String answerId = "_id";
    private static final String answersJson = "answersJson";

    private static final String status = "status";
    private static final String created_at = "created_at";
    private static final String updated_at = "updated_at";

    private static final String BD = "BD_VOLUNDR";
    private static final String sessions = "sessions";
    private static final String answers = "answers";
    private static final int VERSION_BD = 1;

    private BDHelper bdHelper;
    private final Context context;
    private SQLiteDatabase sqLiteDatabase;

    private static class BDHelper extends SQLiteOpenHelper {

        public BDHelper(Context context) {
            super(context, BD, null, VERSION_BD);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub

            db.execSQL("CREATE TABLE " + sessions + "(" + sessionId + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + user_id + " INTEGER NOT NULL, "
                    + userEmail + " TEXT NOT NULL, "
                    + token + " TEXT NOT NULL, "
                    + status + " TEXT NOT NULL, "
                    + created_at + " TEXT NOT NULL, "
                    + updated_at + " TEXT NOT NULL);");

            db.execSQL("CREATE TABLE " + answers + "(" + answerId + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + answersJson + " TEXT NOT NULL, "
                    + status + " TEXT NOT NULL, "
                    + created_at + " TEXT NOT NULL, "
                    + updated_at + " TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
        }

    }

    public bd(Context context) {
        this.context = context;
    }

    public bd open() throws Exception {
        bdHelper = new BDHelper(context);
        sqLiteDatabase = bdHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        // TODO Auto-generated method stub
        bdHelper.close();
    }

    public long createSession(Integer user_id, String userEmail, String token, String date)
            throws SQLException {
        // TODO Auto-generated method stub
        String status = "Active";

        ContentValues cv = new ContentValues();
        cv.put(this.user_id, user_id);
        cv.put(this.userEmail, userEmail);
        cv.put(this.token, token);
        cv.put(this.status, status);
        cv.put(this.created_at, date);
        cv.put(this.updated_at, date);

        return sqLiteDatabase.insert(sessions, null, cv);
    }

    public Cursor searchSessionActive() throws SQLException {

        String selectQuery = "SELECT * FROM " + sessions + " WHERE " + status
                + " = 'Active'";
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        return cursor;
    }
}
