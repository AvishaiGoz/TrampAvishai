package com.example.mathprojectavishaigozland.mathproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    // הגדרת שמות קבועים למסד הנתונים ולטבלה (כדי למנוע טעויות כתיב)
    private static final String DATABASENAME = "user.db";
    private static final String TABLE_RECORD = "tblusers";
    private static final int DATABASEVERSION = 1;
    // ?

    // הגדרת שמות העמודות בטבלה
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_NAME = "name";
    //private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_RATE = "rate";
    private static final String COLUMN_PICTURE = "image";

    // מערך שמכיל את כל העמודות (לצורך שליפת נתונים נוחה)
    private static final String[] allColumns = {COLUMN_ID, COLUMN_NAME, COLUMN_RATE, COLUMN_PICTURE, COLUMN_SCORE};

    //פקודת SQL ליצירת הטבלה
    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RECORD + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +  // מפתח ראשי
            COLUMN_NAME + " TEXT UNIQUE," +                      // שם ייחודי (לא יכולים להיות שניים זהים)
            COLUMN_SCORE + " INT," +
            COLUMN_RATE + " INT," +
            COLUMN_PICTURE + " BLOB );";                         // סוג נתונים לתמונה

    private SQLiteDatabase database; // משתנה לגישה למסד הנתונים

    public DBHelper(@Nullable Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    //פונקציית עזר להמרת תמונה מכתובת - URI למערך בייטים
    public static byte[] getBytes(Context context, Uri uri) throws IOException {
        //הפיכת ה-URI ל-Bitmap (אובייקט תמונה)
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //דחיסת התמונה לפורמט JPEG כדי שתתפוס פחות מקום במסד הנתונים
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    // פעולה שרצה פעם אחת בלבד כשהאפליקציה מותקנת ויוצרת את המסד
    // creating the database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_USER);
    }

    // פעולה שרצה אם גרסת המסד השתנתה (מוחקת את הטבלה הישנה ויוצרת חדשה)
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD);
        onCreate(sqLiteDatabase);
    }

    // get the user back with the id
    // also possible to return only the id
    public long insert(User user, Context context) {
        database = getWritableDatabase(); // פתיחת המסד לכתיבה

        // הכנסת הערכים מהאובייקט למכולת הנתונים (ContentValues)
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getUserName());
        values.put(COLUMN_RATE, user.getRating());
        values.put(COLUMN_SCORE, user.getScore());

        // המרת התמונה מ - URI למערך של בייטים (Byte) כדי שתוכל להישמר ב - SQLite
        try {
            values.put(COLUMN_PICTURE, getBytes(context, user.getUri()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ביצוע ההכנסה בפועל לטבלה
        long id = database.insert(TABLE_RECORD, null, values);
        user.setId(id);    //עדכון האובייקט ב-ID שנוצר לו במסד הנתונים
        database.close();  // סגירת המסד לחיסכון במשאבים
        return id;
    }

    // remove a specific user from the table
    public void deleteUser(User user) {

    }
//
//    //
//    // I prefer using this one...
//    //
//    public ArrayList<User> genericSelectByUserName(String userName)
//    {
//        String[] vals = { userName };
//        // if using the rawQuery
//        // String query = "SELECT * FROM " + TABLE_RECORD + " WHERE " + COLUMN_NAME + " = ?";
//        String column = COLUMN_NAME;
//        return select(column,vals);
//    }
//
//
//    // INPUT: notice two options rawQuery should look like
//    // rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
//    // OUTPUT: arraylist - number of elements accordingly
//    public ArrayList<User> select(String column,String[] values)
//    {
//        database = getReadableDatabase(); // get access to read the database
//        ArrayList<User> users = new ArrayList<>();
//        // Two options,
//        // since query cannot be created in compile time there is no difference
//        //Cursor cursor = database.rawQuery(query, values);
//        Cursor cursor= database.query(TABLE_RECORD, allColumns, COLUMN_NAME +" = ? ", values, null, null, null); // cursor points at a certain row
//        if (cursor.getCount() > 0) {
//            while (cursor.moveToNext()) {
//                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
//                int rating = cursor.getInt(cursor.getColumnIndex(COLUMN_RATE));
//                byte[] bytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_PICTURE));
//                int score = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE));
//                Bitmap bitmap = getImage(bytes);
//                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
//                User user= new User(id,name,rating,bitmap,score);
//                users.add(user);
//            }// end while
//        } // end if
//        database.close();
//        return users;
//    }

    //
//    public void deleteById(long id )
//    {
//        database = getWritableDatabase(); // get access to write e data
//        database.delete(TABLE_RECORD, COLUMN_ID + " = " + id, null);
//        database.close(); // close the database
//    }
//
//    // update a specific user
//    public void update(User user)
//    {
//        database = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_ID, user.getId());
//        values.put(COLUMN_NAME, user.getUserName());
//        values.put(COLUMN_RATE, user.getRating());
//        // stored as Binary Large OBject ->  BLOB
//        values.put(COLUMN_PICTURE, getBytes(user.getBitmap()));
//        database.update(TABLE_RECORD, values, COLUMN_ID + "=" + user.getId(), null);
//        database.close();
//
//    }
//
// return all rows in table
    public ArrayList<User> selectAll() {
        database = getReadableDatabase(); // get access to read the database
        ArrayList<User> users = new ArrayList<>();
        Cursor cursor = database.query(TABLE_RECORD, allColumns, null, null, null, null, null); // cursor points at a certain row
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                int rating = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RATE));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
                byte[] bytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PICTURE));

                Bitmap bitmap = getImage(bytes);
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                User user = new User(id, name, rating, bitmap, score);
                users.add(user);
            }
        }
        cursor.close();
        database.close();
        return users;
    }

    // פונקציה שהופכת אובייקט תמונה למערך של בייטים
    private byte[] getBytes(Bitmap bitmap) {
        //יצירת צינור שאליו נשפוך את נתוני התמונה
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //ביצוע הדחיסה עם 100 אחוז איכות
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        //הפיכת הצינור למערך בייטים והחזרתו
        return stream.toByteArray();
    }

    // פונקציית עזר להמרת מערך בייטים בחזרה לתמונה לצורך הצגה ב - UI
    private Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}

