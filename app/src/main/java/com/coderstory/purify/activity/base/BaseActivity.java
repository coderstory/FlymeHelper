package com.coderstory.purify.activity.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.coderstory.purify.utils.ConfigPreferences;

import androidx.appcompat.app.AppCompatActivity;

import static com.coderstory.purify.utils.ConfigPreferences.getInstance;

public abstract class BaseActivity extends AppCompatActivity {
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(setLayoutResourceID());
        setUpView();
        setUpData();
    }

    protected void setUpData() {
    }

    /***
     * 用于在初始化View之前做一些事
     */
    protected void init() {

    }

    protected abstract void setUpView();

    protected abstract int setLayoutResourceID();

    protected <T extends View> T $(int id) {
        return (T) super.findViewById(id);
    }

    protected void startActivityWithoutExtras(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    protected SharedPreferences.Editor getEditor() {
        if (editor == null) {
            editor = prefs.edit();
        }
        return editor;

    }
}
