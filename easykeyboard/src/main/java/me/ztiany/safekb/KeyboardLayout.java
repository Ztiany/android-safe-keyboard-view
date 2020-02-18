package me.ztiany.safekb;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 说明：使用系统API实现的键盘
 */
public class KeyboardLayout extends FrameLayout {

    private CustomKeyboardView mKeyboardView;
    private boolean mIsRandom;

    private CoreOnKeyboardActionListener mCoreOnKeyboardActionListener;

    private Map<Integer, Keyboard> mKeyboards = new HashMap<>();
    private int mCurrentKeyboardRes;
    private FrameLayout mKeyboardTitleContainer;
    private TextView mKeyboardTitle;

    public KeyboardLayout(Context context) {
        this(context, null);
    }

    public KeyboardLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initKeyBoardView(context);
        parseAttributes(context, attrs);
        setupKeyboard(getCurrentKeyboard());
    }

    private void parseAttributes(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyboardLayout, 0, R.style.DefaultKeyboardStyle);

        mIsRandom = typedArray.getBoolean(R.styleable.KeyboardLayout_isRandom, false);
        mCurrentKeyboardRes = typedArray.getResourceId(R.styleable.KeyboardLayout_xmlLayoutResId, 0);

        int keyboardBgColor = typedArray.getColor(R.styleable.KeyboardLayout_keyboardBgColor, Color.BLACK);
        Drawable keyDrawable = typedArray.getDrawable(R.styleable.KeyboardLayout_keyDrawable);
        CharSequence title = typedArray.getText(R.styleable.KeyboardLayout_keyboardTitle);
        float titleSize = typedArray.getDimension(R.styleable.KeyboardLayout_keyboardTitleSize, Util.spToPx(context, 16));
        int titleColor = typedArray.getColor(R.styleable.KeyboardLayout_keyboardTitleColor, Color.BLACK);
        int titleBgColor = typedArray.getColor(R.styleable.KeyboardLayout_keyboardTitleBgColor, Color.BLACK);
        int textColor = typedArray.getColor(R.styleable.KeyboardLayout_keyboardTextColor, Color.BLACK);
        float textSize = typedArray.getDimension(R.styleable.KeyboardLayout_keyboardTextSize, Util.spToPx(context, 16));

        setKeyboardBgColor(keyboardBgColor);
        setKeyDrawable(keyDrawable);
        setKeyTextColor(textColor);
        setKeyTextSize(textSize);
        setKeyboardTitle(title);
        setKeyboardTitleSize(titleSize);
        setKeyboardTitleColor(titleColor);
        setKeyboardTitleBgColor(titleBgColor);

        typedArray.recycle();
    }

    private void initKeyBoardView(Context context) {
        View keyboardContainer = LayoutInflater.from(context).inflate(R.layout.custom_keyboardview, this, false);
        //title
        mKeyboardTitleContainer = keyboardContainer.findViewById(R.id.ekbFlKeyboardTitle);
        mKeyboardTitle = keyboardContainer.findViewById(R.id.ekbTvKeyboardTitle);
        //keyboard view
        mKeyboardView = keyboardContainer.findViewById(R.id.ekbKvKeyboardTitle);
        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(mCoreOnKeyboardActionListener = new CoreOnKeyboardActionListener(this));
        this.addView(keyboardContainer);
    }

    private void setupKeyboard(Keyboard currentKeyboard) {
        if (currentKeyboard != null) {
            mKeyboardView.setKeyboard(currentKeyboard);
        }
    }

    public CustomKeyboardView getKeyboardView() {
        return mKeyboardView;
    }

    /**
     * 设置键盘布局
     *
     * @param keyBoardResId xml
     */
    public void setKeyBoard(int keyBoardResId) {
        if (keyBoardResId > 0) {
            mCurrentKeyboardRes = keyBoardResId;
            setupKeyboard(getCurrentKeyboard());
        }
    }

    public void setKeyboardBgColor(int keyboardBgColor) {
        mKeyboardView.setBackgroundColor(keyboardBgColor);
    }

    public void setKeyboardTitleColor(int titleColor) {
        mKeyboardTitle.setTextColor(titleColor);
    }

    public void setKeyboardTitleBgColor(int titleBgColor) {
        mKeyboardTitleContainer.setBackgroundColor(titleBgColor);
    }

    public void setKeyboardTitleSize(float titleSize) {
        mKeyboardTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
    }

    /**
     * 设置键盘键上文字的颜色。
     */
    public void setKeyTextColor(@ColorInt int color) {
        mKeyboardView.setKeyTextColor(color);
    }

    /**
     * 设置键盘键上文字的大小。
     */
    public void setKeyTextSize(float textSizePx) {
        mKeyboardView.setKeyTextSize(textSizePx);
    }

    /**
     * 设置键盘标题，如果传空标题，则隐藏标题。
     *
     * @param title 键盘标题
     */
    public void setKeyboardTitle(CharSequence title) {
        if (TextUtils.isEmpty(title)) {
            mKeyboardTitleContainer.setVisibility(View.GONE);
        } else {
            mKeyboardTitleContainer.setVisibility(View.VISIBLE);
            mKeyboardTitle.setText(title);
        }
    }

    /**
     * 设置随机数字键盘
     *
     * @param isRandomKeys 是否随机,再次设置为false则恢复正常
     */
    public void setRandomKeys(boolean isRandomKeys) {
        if (mIsRandom == isRandomKeys) {
            return;
        }
        if (isRandomKeys) {
            randomAllKeyboard();
        } else {
            mKeyboards.clear();
        }
        setupKeyboard(getCurrentKeyboard());
        mIsRandom = isRandomKeys;
    }

    private void randomAllKeyboard() {
        Set<Map.Entry<Integer, Keyboard>> entries = mKeyboards.entrySet();
        for (Map.Entry<Integer, Keyboard> entry : entries) {
            Util.randomKey(entry.getValue());
        }
    }

    private Keyboard getCurrentKeyboard() {
        if (mCurrentKeyboardRes <= 0) {
            return null;
        }
        Keyboard keyboard = mKeyboards.get(mCurrentKeyboardRes);
        if (keyboard == null) {
            keyboard = new Keyboard(getContext(), mCurrentKeyboardRes);
            mKeyboards.put(mCurrentKeyboardRes, keyboard);
            if (mIsRandom) {
                Util.randomKey(keyboard);
            }
        }
        return keyboard;
    }

    /**
     * 设置按压背景，线条粗细等。
     *
     * @param keyDrawable mKeyDrawable
     */
    public void setKeyDrawable(Drawable keyDrawable) {
        mKeyboardView.setKeyDrawable(keyDrawable);
    }

    /**
     * 建立与EditText的绑定关系，用于控制输入值
     *
     * @param editText 绑定EditText 默认显示自定义键盘
     */
    public void setEditText(@NonNull EditText editText) {
        setEditText(editText, false);
    }

    /**
     * 建立与EditText的绑定关系，用于控制输入值
     *
     * @param editText             需要绑定的EditText
     * @param isOpenNativeKeyBoard 是否打开原生键盘
     */
    public void setEditText(@NonNull EditText editText, boolean isOpenNativeKeyBoard) {
        Util.checkNull(mCoreOnKeyboardActionListener, "Please check if xmlLayoutResId is set");
        mCoreOnKeyboardActionListener.setEditText(editText);
        if (isOpenNativeKeyBoard) {
            Util.showKeyboard(editText);
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            Util.disableShowSoftInput(editText);
            Util.hideKeyboard(editText.getContext());
        }
    }

    /**
     * 设置键盘输入监听
     *
     * @param listener l
     */
    public void setOnKeyboardActionListener(KeyBoardActionListener listener) {
        mCoreOnKeyboardActionListener.setKeyActionListener(listener);
    }

}