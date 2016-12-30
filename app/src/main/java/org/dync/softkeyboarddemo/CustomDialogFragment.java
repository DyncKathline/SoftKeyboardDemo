package org.dync.softkeyboarddemo;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.io.Serializable;

/**
 * Created by KathLine on 2016/11/17.</br>
 * <p>使用说明</p>
 * <pre>
 * CustomDialogFragment.Builder builder = new CustomDialogFragment.Builder(mContext);
 * mGratuityDialogFragment = builder.setFullScreen(true)
 * .setContentView(R.layout.dialogfragment_gratuity)
 * .setExistDialogLined(true)
 * .setBackgroundDrawable(true)
 * .setLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
 * .build(fragmentManager, "GratuityDialogFragment");
 * mGratuityDialogFragment.setOnInitListener(new CustomDialogFragment.OnInitListener() {
 * @Override
 * public void init(View view) {
 *
 * }
 * });
 * </pre>
 */

public class CustomDialogFragment extends DialogFragment {

    protected Builder builder;
    protected int layoutId;
    protected int gravity;
    protected int animId;
    protected boolean backgroundDrawableable;
    protected float dimAmount;
    protected boolean cancelable;
    protected boolean existDialogLined;
    protected boolean isFullScreen;
    protected int width;
    protected int height;

    public CustomDialogFragment() {

    }

    public interface OnInitListener {
        void init(View view);
    }

    public OnInitListener mOnInitListener;

    public void setOnInitListener(OnInitListener listener) {
        mOnInitListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Builder builder = (Builder) getArguments().getSerializable("builder");
        if (builder != null) {
            this.builder = builder;
            layoutId = builder.layoutId;
            gravity = builder.gravity;
            animId = builder.animId;
            backgroundDrawableable = builder.backgroundDrawableable;
            dimAmount = builder.dimAmount;
            cancelable = builder.cancelable;
            existDialogLined = builder.existDialogLined;
            isFullScreen = builder.isFullScreen;
            width = builder.width;
            height = builder.height;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setLayout();
        View view = inflater.inflate(layoutId, container);
        if (mOnInitListener != null) {
            mOnInitListener.init(view);
        }
        return view;
    }

    private void setLayout() {
        if (existDialogLined) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        Window window = getDialog().getWindow();
        window.setGravity(gravity);
        if (animId != 0) {
            window.setWindowAnimations(animId);
        }
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        if (width != 0 && height != 0) {
            lp.width = width;
            lp.height = height;
        }
        if (isFullScreen) {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏，即没有系统状态栏
        }
        if (backgroundDrawableable) {
            window.setBackgroundDrawable(new ColorDrawable(0));
        }
        if (dimAmount < 0f || dimAmount > 1f) {
            throw new RuntimeException("透明度必须在0~1之间");
        } else {
            lp.dimAmount = dimAmount;
        }
        window.setAttributes(lp);
        setCancelable(cancelable);
        if (cancelable) {
            getDialog().setCanceledOnTouchOutside(true);
        }
    }

    public static class Builder implements Serializable {
        public Context context;
        public int layoutId;
        public int gravity;
        public int animId;
        public boolean backgroundDrawableable;
        public float dimAmount;
        public boolean cancelable;
        public boolean existDialogLined;
        public boolean isFullScreen;
        public int width;
        public int height;

        public Builder(Context context) {
            this.context = context;
            layoutId = R.layout.activity_main;
            gravity = Gravity.CENTER;
            animId = 0;
            backgroundDrawableable = false;
            dimAmount = 0.5f;
            cancelable = true;
            existDialogLined = false;
            isFullScreen = false;
            width = WindowManager.LayoutParams.WRAP_CONTENT;
            height = WindowManager.LayoutParams.WRAP_CONTENT;
        }

        public Builder setContentView(@LayoutRes int layoutId) {
            this.layoutId = layoutId;
            return this;
        }

        /**
         * 必须使用Gravity的静态常量，默认在中间弹出
         *
         * @param gravity 详见{@link Gravity}
         * @return
         * @see Gravity
         */
        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * 设置Dialog弹出和Dialog退出的动画
         *
         * @param animId
         * @return
         */
        public Builder setAnimId(int animId) {
            this.animId = animId;
            return this;
        }

        /**
         * Creates a new set of layout parameters with the specified width
         * and height.
         *
         * @param width  the width, either set WindowManager.LayoutParams.WRAP_CONTENT or
         *               WindowManager.LayoutParams.FILL_PARENT (replaced by WindowManager.LayoutParams.MATCH_PARENT in
         *               API Level 8), or a fixed size in pixels
         * @param height the height, either set WindowManager.LayoutParams.WRAP_CONTENT or
         *               WindowManager.LayoutParams.FILL_PARENT (replaced by WindowManager.LayoutParams.MATCH_PARENT in
         *               API Level 8), or a fixed size in pixels
         * @return
         */
        public Builder setLayoutParams(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * 是否给Dialog的背景设置透明，默认false
         *
         * @param backgroundDrawableable
         * @return
         */
        public Builder setBackgroundDrawable(boolean backgroundDrawableable) {
            this.backgroundDrawableable = backgroundDrawableable;
            return this;
        }

        /**
         * 设置Dialog之外的背景透明度，0~1之间，默认值 0.5f，半透明
         *
         * @param dimAmount
         * @return
         */
        public Builder setDimAmount(float dimAmount) {
            this.dimAmount = dimAmount;
            return this;
        }

        /**
         * 设置Dialog是否可以关闭在Dialog之外的区域，默认true
         *
         * @param cancelable
         * @return
         */
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        /**
         * 如果存在Holo主题下Dialog有蓝色线(含有标题栏)可以尝试调用该方法，默认不存在
         *
         * @param existDialogLined
         * @return
         */
        public Builder setExistDialogLined(boolean existDialogLined) {
            this.existDialogLined = existDialogLined;
            return this;
        }

        /**
         * 是否设置全屏模式，指的是去除系统状态栏，默认不去除
         *
         * @param isFullScreen
         * @return
         */
        public Builder setFullScreen(boolean isFullScreen) {
            this.isFullScreen = isFullScreen;
            return this;
        }

        public CustomDialogFragment build(FragmentManager manager, String tag) {
            //谷歌推荐使用这种方式保存传进来的数据
            Bundle bundle = new Bundle();
            bundle.putSerializable("builder", this);

            CustomDialogFragment dialogFragment = new CustomDialogFragment();
            dialogFragment.setArguments(bundle);

            dialogFragment.show(manager, tag);
            return dialogFragment;
        }
    }
}
