package com.example.chatapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserInformation.db";
    private static final String User_TABLE = "Users";
    private static final String Post_table = "Posts";
    //private static final String KEY_ID = "id";
    private static final String Email = "Email";
    private static final String User_name = "UserName";
    private static final String Password = "password";
    private static final String Message = "message";
    public static String logged_username = "";
//    String CREATE_User_TABLE = "CREATE TABLE " + User_TABLE + "("
//            + KEY_ID + " INTEGER PRIMARY KEY," + Email + " TEXT,"



    String q_1= "Create table Users (Email Text,UserName Text  Primary key , password TEXT )";
    //String query = "Create table Users (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,Email TEXT, Username TEXT, password TEXT, UNIQUE (Username))";
    String q_2= "Create table Posts (message Text, user_name Text,image BLOB, ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)";

    public DataBaseHandler(@Nullable Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(q_1);
        db.execSQL(q_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + User_TABLE);

        //Create tables again
        onCreate(db);

    }
    //Adding information to the database
    public Boolean addInformation(com.example.chatapplication.Users users)
    {
        Boolean result;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(User_name,users.getUsername());
        values.put(Email,users.getEmail());
        values.put(Password,users.getPassword());
        long id = db.insert(User_TABLE,null,values);
        db.close();
        if (id== -1)
        {
            result = false;
        }
        else
        {
            result = true;
            addPost();

        }
        return  result;
    }
    //Getting all the users data that is stored in the databse
    public List<Users> getContacts()
    {
        List<Users> userlist = new ArrayList<Users>();
        String selectQuery = "SELECT  * FROM " + User_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        //Looping all the rows
        if (cursor.moveToNext())
        {
            do
            {
                Users users = new Users();
                users.setEmail(cursor.getString(0));
                users.setUsername(cursor.getString(1));
                users.setPassword(cursor.getString(2));
                // Adding contact to list
                userlist.add(users);
            } while (cursor.moveToNext());
        }
        // return contact list
        return userlist;


    }
    public Boolean isLogged(String Username1, String passwords)
    {
        boolean choice = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] col = {User_name};
        String selection = User_name + "=?" + " and " + Password + " =?";
        String [] selectionArguments = {Username1, passwords};
        Cursor cursor = db.query(User_TABLE,col,selection,selectionArguments,null,null,null);
        choice = cursor.getCount() > 0;//True
        cursor.close();
        if (choice) {
            logged_username = Username1;
        }
        return choice;
    }
    public Boolean savePost(Post post, byte[] image) {
        Boolean result;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Message,post.getMessage());
        values.put("user_name",post.getUser_name());
        values.put("image", image);
        long id = db.insert(Post_table,null,values);
        db.close();
        result = id != -1;
        return  result;
    }
    public List<Post> getPosts(String  username)
    {
        List<Post> posts_list = new ArrayList<Post>();
        String selectQuery = "SELECT  * FROM Posts where user_name='" + username+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        //Looping all the rows
        if (cursor.moveToNext())
        {
            do
            {
                Post post = new Post();
                post.setMessage(cursor.getString(0));
                post.setUser_name(cursor.getString(1));
                byte[] byteArray = cursor.getBlob(2);
                post.setID(cursor.getInt(3));
                if (byteArray != null) {
                    Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    post.setImage(image);
                }
                // Adding post to list
                posts_list.add(post);
            } while (cursor.moveToNext());
        }
        // return contact list
        return posts_list;


    }
    private  void addPost() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Message,"This is just demo post");
        values.put("user_name",DataBaseHandler.logged_username);
        long id = db.insert(Post_table,null,values);
        db.close();
    }

}

