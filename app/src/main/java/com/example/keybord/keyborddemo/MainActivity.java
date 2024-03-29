package com.example.keybord.keyborddemo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.sdk.kb.KeyboardManager;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyboardManager.configKeyboardMapper(new DemoKeyboardMapper());

        String[] type = new String[]{"SystemKeyboard", "SystemKeyBoardEditText(The Library)", "Other Way"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, type);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        switch (position) {
            case 0:
                startActivity(SystemKeyboardActivity.class);
                break;
            case 1:
                startActivity(SystemKeyboardEditTextActivity.class);
                break;
            case 2:
                startActivity(OtherWayActivity.class);
                break;
        }
    }

    private void startActivity(Class c) {
        startActivity(new Intent(this, c));
    }

}