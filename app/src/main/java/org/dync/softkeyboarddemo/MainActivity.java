package org.dync.softkeyboarddemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText edt;
    private EditText editText;
    private TextView text;

    private Activity activity;
    private int mSoftKeybardHeight;
    private boolean isOpen;
    private boolean isTouch = true;
    private int duration = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        edt = (EditText) findViewById(R.id.edt);
        editText = (EditText) findViewById(R.id.editText);
        text = (TextView) findViewById(R.id.text);

        editText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("TAG", "--onTouch--");
                //这里点击输入框强制不让键盘弹出
                SoftKeyboardUtil.hideKeyboard(activity, editText);
                if (!isOpen) {//键盘没有打开
                    if (isTouch) {//这里是因为onTouch()方法会不止一次调用，所以用boolean值来使得控件只移动一次
                        //先移动到键盘弹出的高度再手动弹出键盘，这样就不会出现挤压布局的效果
                        duration = 90;
                        editText.animate().translationYBy(-mSoftKeybardHeight).setDuration(duration).start();
                        Log.e("TAG", "平移高度：" + -mSoftKeybardHeight);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                SoftKeyboardUtil.showKeyboard(activity, editText);
                            }
                        }, duration);
                        //这里设为false目的是防止这里延时弹出键盘会触发onSoftKeyBoardChange()会再一次调用移动控件的方法
                        isTouch = false;
                    }
                }
                return true;
            }
        });

        SoftKeyboardUtil.observeSoftKeyboard(activity, new SoftKeyboardUtil.OnSoftKeyboardChangeListener() {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoftKeyboardUtil.removeGlobalOnLayoutListener(this);
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
