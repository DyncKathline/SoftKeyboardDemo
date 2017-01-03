package org.dync.softkeyboarddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

public class DialogFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_fragment);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏，即没有系统状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //保持屏幕常亮
    }

    public void onClick(View view){
        new DefaultDialogFragment().show(getFragmentManager(), "DefaultDialogFragment");
    }
}
