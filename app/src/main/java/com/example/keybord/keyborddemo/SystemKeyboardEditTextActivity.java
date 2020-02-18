package com.example.keybord.keyborddemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;

import me.ztiany.safekb.KeyBoardActionListener;

/**
 * 附带EditText的 popwindow 形式弹出的键盘
 */
public class SystemKeyboardEditTextActivity extends AppCompatActivity implements KeyBoardActionListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_systemkeyboardedittext);
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onTextChange(Editable editable) {

    }

    @Override
    public void onClear() {

    }

    @Override
    public void onClearAll() {

    }

    @Override
    public boolean handOnKey(int primaryCode, int[] keyCodes) {
        return false;
    }

}
