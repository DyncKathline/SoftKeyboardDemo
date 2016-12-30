package org.dync.softkeyboarddemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DefaultActivity extends AppCompatActivity {
    private EditText edt;
    private EditText editText;
    private TextView text;

    private Activity activity;
    private int mSoftKeybardHeight;
    private boolean isOpen;
    private boolean isTouch = true;
    private int duration = 100;
    private SoftKeyboardUtil softKeyboardUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        activity = this;

        edt = (EditText) findViewById(R.id.edt);
        editText = (EditText) findViewById(R.id.editText);
        text = (TextView) findViewById(R.id.text);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        softKeyboardUtil = new SoftKeyboardUtil();
        softKeyboardUtil.observeSoftKeyboard(activity, new SoftKeyboardUtil.OnSoftKeyboardChangeListener() {
            @Override
            public void onSoftKeyBoardChange(int softKeybardHeight, boolean isShow) {
                mSoftKeybardHeight = softKeybardHeight;
                isOpen = isShow;
                if (isShow) {
                    onShowKeyboard(softKeybardHeight);
                    if (isTouch) {//点击输入框则不移动控件
                        editText.animate().translationYBy(-softKeybardHeight).setDuration(duration).start();
                    }
                    Log.e("TAG", "isShow--平移高度：" + -mSoftKeybardHeight);
                } else {
                    onHideKeyboard(softKeybardHeight);
                    editText.animate().translationYBy(softKeybardHeight).setDuration(duration).start();
                    Log.e("TAG", "isHide--平移高度：" + mSoftKeybardHeight);
                    isTouch = true;//这里一定要设置，不然点击输入框，控件只会在第一次能移动，之后不会移动了
                }
            }
        });

        editText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("TAG", "--onTouch--");
                if (!isOpen) {//键盘没有打开
                    if (isTouch) {//这里是因为onTouch()方法会不止一次调用，所以用boolean值来使得控件只移动一次
                        //这里设为false目的是防止这里延时弹出键盘会触发onSoftKeyBoardChange()会再一次调用移动控件的方法
                        isTouch = false;
                        //先移动到键盘弹出的高度再手动弹出键盘，这样就不会出现挤压布局的效果
                        editText.animate().translationYBy(-mSoftKeybardHeight).setDuration(duration).start();
                        Log.e("TAG", "平移高度：" + -mSoftKeybardHeight);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                softKeyboardUtil.showKeyboard(activity, editText);
                            }
                        }, duration);
                    }
                }
                return false;//这里不能返回true，不然焦点不会聚焦到该控件
            }
        });
//        setHideVirtualKey();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
    }

    public void setHideVirtualKey() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        softKeyboardUtil.removeGlobalOnLayoutListener(this);
    }

    private void onShowKeyboard(int softKeybardHeight) {
        // 在这里处理软键盘弹出的回调
        text.setText("onShowKeyboard : keyboardHeight = " + softKeybardHeight);
    }

    private void onHideKeyboard(int softKeybardHeight) {
        // 在这里处理软键盘收回的回调
        text.setText("onHideKeyboard : keyboardHeight = " + softKeybardHeight);
    }
}
