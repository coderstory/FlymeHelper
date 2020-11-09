package com.coderstory.flyme.utils;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.nio.charset.StandardCharsets;

public class SqlLiteHelper extends SQLiteOpenHelper {
    // create table user(name text,qq text,sn text)
    public static final String CREATE_TABLE = hexStringToString("637265617465207461626c652075736572286e616d6520746578742c717120746578742c736e207465787429");

    public SqlLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase get(Context mc) {
        SQLiteDatabase.loadLibs(mc);
        SqlLiteHelper dbHelper = new SqlLiteHelper(mc, "test.log", null, 1);
        return dbHelper.getWritableDatabase("pass_word_1");
    }

    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, StandardCharsets.UTF_8);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}