package me.ztiany.safekb;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class Util {

    static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    static int spToPx(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    private static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 禁止EditText弹出软件盘，光标依然正常显示，并且能正常选取光标。
     */
    static void disableShowSoftInput(EditText editText) {
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(editText, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示系统键盘
     */
    static void showKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                view.requestFocus();
                imm.showSoftInput(view, 0);
            }
        }
    }

    /**
     * 隐藏软键盘
     */
    static void hideKeyboard(Context context) {
        View view = ((Activity) context).getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * 获取实际内容高度
     */
    static int getContentHeight(Context context) {
        int screen_h_navigator_bar = 0;

        DisplayMetrics dMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            display.getMetrics(dMetrics);
            screen_h_navigator_bar = dMetrics.heightPixels;

            int ver = Build.VERSION.SDK_INT;

            // 新版本的android 系统有导航栏，造成无法正确获取高度
            if (ver == 13) {
                try {
                    Method mt = display.getClass().getMethod("getRealHeight");
                    screen_h_navigator_bar = (Integer) mt.invoke(display);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ver > 13) {
                try {
                    Method mt = display.getClass().getMethod("getRawHeight");
                    screen_h_navigator_bar = (Integer) mt.invoke(display);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return screen_h_navigator_bar - getStatusBarHeight(context);
    }

    static int getStatusBarHeight(Context context) {
        Class<?> c;
        Object obj;
        Field field;
        int x;
        int bar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            bar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return bar;
    }

    static void checkNull(Object object, String info) {
        if (object == null) {
            throw new NullPointerException(info);
        }
    }

    static void randomKey(Keyboard keyboard) {
        List<Keyboard.Key> keyList = keyboard.getKeys();
        List<Keyboard.Key> newKeyList = new ArrayList<>();
        for (int i = 0, size = keyList.size(); i < size; i++) {
            Keyboard.Key key = keyList.get(i);
            CharSequence label = key.label;
            if (label != null && Util.isNumeric(label.toString())) {
                newKeyList.add(key);
            }
        }
        int count = newKeyList.size();
        List<KeyModel> resultList = new ArrayList<>();
        LinkedList<KeyModel> temp = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            temp.add(new KeyModel(48 + i, i + ""));
        }
        Random rand = new SecureRandom();
        rand.setSeed(SystemClock.currentThreadTimeMillis());
        for (int i = 0; i < count; i++) {
            int num = rand.nextInt(count - i);
            KeyModel model = temp.get(num);
            resultList.add(new KeyModel(model.getCode(), model.getLabel()));
            temp.remove(num);
        }
        for (int i = 0; i < count; i++) {
            Keyboard.Key newKey = newKeyList.get(i);
            KeyModel resultModel = resultList.get(i);
            newKey.label = resultModel.getLabel();
            newKey.codes[0] = resultModel.getCode();
        }
    }

}