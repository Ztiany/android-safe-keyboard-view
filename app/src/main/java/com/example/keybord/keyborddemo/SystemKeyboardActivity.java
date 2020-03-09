package com.example.keybord.keyborddemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import me.ztiany.safekb.KeyboardLayout;

public class SystemKeyboardActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_systemkeyboard);

        KeyboardLayout keyboard = findViewById(R.id.system_keyboard);

        EditText edit1 = findViewById(R.id.edit);
        EditText edit2 = findViewById(R.id.edit2);
        EditText edit3 = findViewById(R.id.edit3);
        EditText edit4 = findViewById(R.id.edit4);
        EditText edit5 = findViewById(R.id.edit5);

        keyboard.bindEditTextArray(edit1, edit2, edit3);
    }

}