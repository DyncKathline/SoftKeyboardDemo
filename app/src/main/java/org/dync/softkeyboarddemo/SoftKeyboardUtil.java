package org.dync.softkeyboarddemo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by xiongxuesong-pc on 2016/6/14.
 */
public class SoftKeyboardUtil {


    private static ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private static InputMethodManager imm;
    private static int keyboardHeight;// 软键盘的高度
    private static int navigationBarHeight;// 虚拟按键的高度

    /**
     * 监听键盘高度和键盘时候处于打开状态，在调用的Activity中的onDestroy()方法中调用
     * 该类中的removeGlobalOnLayoutListener()方法来移除监听
     *
     * @param activity
     * @param listener
     */
    public static void observeSoftKeyboard(final Activity activity, final OnSoftKeyboardChangeListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        final int statusBarHeight = getStatusBarHeight(activity);// 状态栏的高度
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean isShowKeyboard;// 软键盘的显示状态

            @Override
            public void onGlobalLayout() {
                onGlobalLayoutListener = this;

                // 应用可以显示的区域。此处包括应用占用的区域，
                // 以及ActionBar和状态栏，但不含设备底部的虚拟按键。
                Rect r = new Rect();
                decorView.getWindowVisibleDisplayFrame(r);
                // 屏幕高度。这个高度不含虚拟按键的高度
                int screenHeight = decorView.getRootView().getHeight();
                NavigationBarInfo navigationBarInfo = getNavigationBarInfo(activity);
                if (navigationBarInfo.isHasNavigationBar()) {
                    navigationBarHeight = navigationBarInfo.getmPoint().y;
                }else {
                    navigationBarHeight = 0;
                }
                int heightDiff = screenHeight - (r.bottom - r.top);
                // 在不显示软键盘时，heightDiff等于状态栏的高度
                // 在显示软键盘时，heightDiff会变大，等于软键盘加状态栏的高度。
                // 所以heightDiff大于状态栏高度时表示软键盘出现了，
                // 这时可算出软键盘的高度，即heightDiff减去状态栏的高度
                if (keyboardHeight == 0 && heightDiff > statusBarHeight + navigationBarHeight) {
                    keyboardHeight = heightDiff - statusBarHeight - navigationBarHeight;
                }
                if (isShowKeyboard) {
                    // 如果软键盘是弹出的状态，并且heightDiff小于等于状态栏高度，
                    // 说明这时软键盘已经收起
                    if (heightDiff <= statusBarHeight + navigationBarHeight) {
                        isShowKeyboard = false;
                        listener.onSoftKeyBoardChange(keyboardHeight, isShowKeyboard);
                    }
                } else {
                    // 如果软键盘是收起的状态，并且heightDiff大于状态栏高度，
                    // 说明这时软键盘已经弹出
                    if (heightDiff > statusBarHeight + navigationBarHeight) {
                        isShowKeyboard = true;
                        listener.onSoftKeyBoardChange(keyboardHeight, isShowKeyboard);
                    }
                }
            }
        });
    }

    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int softKeybardHeight, boolean isShow);
    }

    public static void removeGlobalOnLayoutListener(Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        if (SoftKeyboardUtil.onGlobalLayoutListener != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                decorView.getViewTreeObserver().removeGlobalOnLayoutListener(SoftKeyboardUtil.onGlobalLayoutListener);
            } else {
                decorView.getViewTreeObserver().removeOnGlobalLayoutListener(SoftKeyboardUtil.onGlobalLayoutListener);
            }
        }
    }

    /**
     * 获得状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 获取 NavigationBar 的高度，虚拟按键的高度
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static void hideKeyboard(Context context, View view) {
        view.requestFocus();
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void hideKeyboard(Activity activity) {
        imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showKeyboard(Context context, View view) {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    public static class NavigationBarInfo {
        private Point mPoint;
        private int orientation;//0:horizontal 1:vertical
        private boolean isHasNavigationBar;

        public NavigationBarInfo() {
        }

        public Point getmPoint() {
            return mPoint;
        }

        public void setmPoint(Point mPoint) {
            this.mPoint = mPoint;
        }

        public int getOrientation() {
            return orientation;
        }

        public void setOrientation(int orientation) {
            this.orientation = orientation;
        }

        public boolean isHasNavigationBar() {
            return isHasNavigationBar;
        }

        public void setHasNavigationBar(boolean hasNavigationBar) {
            isHasNavigationBar = hasNavigationBar;
        }
    }

    public static NavigationBarInfo getNavigationBarInfo(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);
        NavigationBarInfo navigationBarInfo = new NavigationBarInfo();
        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            navigationBarInfo.setmPoint(new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y));
            navigationBarInfo.setOrientation(1);
            navigationBarInfo.setHasNavigationBar(true);
            return navigationBarInfo;
        }
        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            navigationBarInfo.setmPoint(new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y));
            navigationBarInfo.setOrientation(0);
            navigationBarInfo.setHasNavigationBar(true);
            return navigationBarInfo;
        }
        // navigation bar is not present
        navigationBarInfo.setmPoint(new Point());
        navigationBarInfo.setOrientation(0);
        navigationBarInfo.setHasNavigationBar(false);
        return navigationBarInfo;

    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;

    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {

            } catch (InvocationTargetException e) {

            } catch (NoSuchMethodException e) {

            }
        }
        return size;
    }
}