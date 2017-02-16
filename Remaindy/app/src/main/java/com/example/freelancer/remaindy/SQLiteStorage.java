package com.example.freelancer.remaindy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SQLiteStorage {

    private static final String TAG = SQLiteStorage.class.getSimpleName();
    private static final String TABLE_TODO="todolist";
    private static final String COL_TODO_ID="id";
    private static final String COL_TODO_TEXT = "title";
    private DatabaseOpenHelper mHelper;
    private static SQLiteStorage sInstance = null;
    private static Context con = null;
    private SQLiteStorage(Context context) {
        mHelper = new DatabaseOpenHelper(context);
        con=context;
    }

    public static synchronized SQLiteStorage getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQLiteStorage(context);
        }
        con = context;
        return sInstance;
    }

    /*
       get all todolist objects from database
    */
    public List<Todoobject>  getalltodoitems(){
        List<Todoobject> todoobjects=new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from  "+TABLE_TODO,null);
        for (int i=0;i<cursor.getCount();i++){
            cursor.moveToPosition(i);
            String title =cursor.getString(cursor.getColumnIndex(COL_TODO_TEXT));
            Todoobject todoobject= new Todoobject();
            todoobject.setTitle(title);
            todoobjects.add(todoobject);
        }
        cursor.close();
        return todoobjects;
    }

    /*
        insert a newly created todolist item to database
     */
    public boolean InsertTodoItem(Todoobject todoobject){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TODO_TEXT,todoobject.getTitle());
        db.insertWithOnConflict(TABLE_TODO, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }
    /*
        delete  todolist item from  database
     */

    public boolean DeleteTodoItem(Todoobject todoobject){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String where = COL_TODO_TEXT + "=?";
        return db.delete(TABLE_TODO, where ,new String[] { todoobject.getTitle() })>0;

    }
    /*
       update already  created todolist item to database
    */
    public boolean Updatetitle(Todoobject old_todoobject,Todoobject new_todoobject){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TODO_TEXT,new_todoobject.getTitle());
        return db.updateWithOnConflict(TABLE_TODO, values, COL_TODO_TEXT + " = '"
                + old_todoobject.getTitle() + "'", null, SQLiteDatabase.CONFLICT_REPLACE)>0;
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        private static final String DB_NAME = "Remaindy.db";
        private static final int DB_VERSION = 1; //added todolist table with title column


        public DatabaseOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTodoTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String DROP_TABLE_TODO = "drop table IF EXISTS "
                    + TABLE_TODO + ";";
            db.execSQL(DROP_TABLE_TODO);
            Log.v(TAG, "onCreate");
            createTodoTable(db);
        }

        /*
            simple table contains only one text field ie title of todoitem
         */
        private void createTodoTable(SQLiteDatabase db) {
            Log.v(TAG, "createTodoTable");
            String CREATE_PF_TABLE = "create table " + TABLE_TODO + " ("
                    + COL_TODO_ID + " integer primary key autoincrement,"
                    + COL_TODO_TEXT + " text not null" + ");";
            db.execSQL(CREATE_PF_TABLE);
        }
    }
}
