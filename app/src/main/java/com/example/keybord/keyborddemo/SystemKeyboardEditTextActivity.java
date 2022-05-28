package com.example.keybord.keyborddemo;

import android.os.Bundle;
import android.text.Editable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.sdk.kb.KeyBoardActionListener;


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
