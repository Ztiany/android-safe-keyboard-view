package me.ztiany.safekb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * 说明：附带键盘弹出的EditText
 */
public class SystemKeyboardEditText extends KeyboardEditText {

    /**
     * 否启用自定义键盘
     */
    private boolean enable = true;

    /**
     * 默认获取焦点
     */
    private boolean focusEnable = true;

    private KeyboardLayout mKeyboardLayout;
    private CoreOnKeyboardActionListener mCoreOnKeyboardActionListener;
    private OnFocusChangeListener mSpareFocusChangeListener;
    private OnFocusChangeListener mInternalFocusChangeListener;

    public SystemKeyboardEditText(Context context) {
        super(context);
        initInternal(context, null);
    }

    public SystemKeyboardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInternal(context, attrs);
    }

    public SystemKeyboardEditText(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInternal(context, attrs);
    }

    private void initInternal(Context context, AttributeSet attributeSet) {
        initKeyboardView(context);
        parseAttributes(context, attributeSet);
        initListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        setOnTouchListener((v, event) -> {
            if (!isShowing()) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (focusEnable) {
                        requestFocus();
                        requestFocusFromTouch();
                        if (enable) {
                            Util.hideKeyboard(getContext());
                            Util.disableShowSoftInput(SystemKeyboardEditText.this);
                            showKeyboardWindow();
                        }
                    } else {
                        dismissKeyboardWindow();
                        Util.disableShowSoftInput(SystemKeyboardEditText.this);
                    }
                }
            } else {
                requestFocus();
                requestFocusFromTouch();
            }
            return false;
        });

        mInternalFocusChangeListener = (v, hasFocus) -> {
            //根据焦点变化判断外部点击区域
            if (!hasFocus) {
                dismissKeyboardWindow();
            }

            if (mSpareFocusChangeListener != null) {
                mSpareFocusChangeListener.onFocusChange(v, hasFocus);
            }
        };
        setOnFocusChangeListener(mInternalFocusChangeListener);
    }

    private void initKeyboardView(Context context) {
        initPopWindow(mKeyboardLayout = new KeyboardLayout(context));
        mCoreOnKeyboardActionListener = new CoreOnKeyboardActionListener(mKeyboardLayout);
        mCoreOnKeyboardActionListener.setEditText(this);
        mCoreOnKeyboardActionListener.setKeyboardPopupWindow(getKeyboardWindow());
        mKeyboardLayout.getKeyboardView().setOnKeyboardActionListener(mCoreOnKeyboardActionListener);
    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SystemKeyboardEditText, 0, R.style.DefaultKeyboardStyle);

        boolean randomKeys = typedArray.getBoolean(R.styleable.SystemKeyboardEditText_isRandom, false);
        int keyboardLayoutResId = typedArray.getResourceId(R.styleable.SystemKeyboardEditText_xmlLayoutResId, 0);

        Drawable keyDrawable = typedArray.getDrawable(R.styleable.SystemKeyboardEditText_keyDrawable);
        int textColor = typedArray.getColor(R.styleable.SystemKeyboardEditText_keyboardTextColor, Color.WHITE);
        float textSize = typedArray.getDimension(R.styleable.SystemKeyboardEditText_keyboardTextSize, Util.spToPx(context, 16));

        CharSequence title = typedArray.getText(R.styleable.SystemKeyboardEditText_keyboardTitle);
        float titleSize = typedArray.getDimension(R.styleable.SystemKeyboardEditText_keyboardTitleSize, Util.spToPx(context, 16));
        int titleColor = typedArray.getColor(R.styleable.SystemKeyboardEditText_keyboardTitleColor, Color.WHITE);
        int titleBgColor = typedArray.getColor(R.styleable.SystemKeyboardEditText_keyboardTitleBgColor, Color.BLACK);
        int keyboardBgColor = typedArray.getColor(R.styleable.SystemKeyboardEditText_keyboardBgColor, Color.BLACK);

        mKeyboardLayout.setRandomKeys(randomKeys);
        mKeyboardLayout.setKeyBoard(keyboardLayoutResId);

        mKeyboardLayout.setKeyboardBgColor(keyboardBgColor);
        mKeyboardLayout.setKeyDrawable(keyDrawable);
        mKeyboardLayout.setKeyTextColor(textColor);
        mKeyboardLayout.setKeyTextSize(textSize);
        mKeyboardLayout.setKeyboardTitle(title);
        mKeyboardLayout.setKeyboardTitleSize(titleSize);
        mKeyboardLayout.setKeyboardTitleColor(titleColor);
        mKeyboardLayout.setKeyboardTitleBgColor(titleBgColor);

        boolean disableCopyAndPaste = typedArray.getBoolean(R.styleable.SystemKeyboardEditText_disableCopyAndPaste, false);
        if (disableCopyAndPaste) {
            removeCopyAndPaste();
        }

        typedArray.recycle();
    }

    public KeyboardLayout getKeyboardLayout() {
        return mKeyboardLayout;
    }

    /**
     * 重写 onKeyDown 当键盘弹出按回退键关闭
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && getKeyboardWindow().isShowing()) {
            dismissKeyboardWindow();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置键盘输入监听
     *
     * @param listener listener
     */
    public void setOnKeyboardActionListener(KeyBoardActionListener listener) {
        this.mCoreOnKeyboardActionListener.setKeyActionListener(listener);
    }

    public void setSpareFocusChangeListener(OnFocusChangeListener focusChangeListener) {
        this.mSpareFocusChangeListener = focusChangeListener;
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        if (l == mInternalFocusChangeListener) {
            super.setOnFocusChangeListener(l);
        }
    }

}