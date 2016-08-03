package com.inverce.utils.tools;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.inverce.utils.IM;
import com.inverce.utils.logging.Log;

import static com.inverce.utils.IM.context;
import static com.inverce.utils.IM.resources;

@SuppressWarnings("unused")
public class Screen {
    public static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

    public static boolean isXLargeTablet() {
        return (resources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static Point getScreenSize() {
        Point size = new Point();
        Display display;
        if (context() instanceof Activity) {
            display = ((Activity) context()).getWindowManager().getDefaultDisplay();
        } else {
            WindowManager wm = (WindowManager) context().getSystemService(Context.WINDOW_SERVICE);
            display = wm.getDefaultDisplay();
        }
        display.getSize(size);
        return size;
    }

    public static Point getActivitySize() {
        Point size = getScreenSize();
        size.y -= getStatusBarHeight();
        Activity activity = Ui.getCurrentActivity();

        if (activity != null) {
            try {
                int measuredHeight = activity.findViewById(android.R.id.content).getMeasuredHeight();
                if (measuredHeight > 0 && size.y != measuredHeight) {
                    size.y = measuredHeight;
                }
            } catch (Exception ex) {
                Log.exs("Tools", "getActivitySize", ex);
            }
        }
        return size;
    }

    public static int pxToDp(int px) {
        return (int) (px / resources().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * resources().getDisplayMetrics().density);
    }

    public static Point getLocationOnScreen(View measurementView) {
        int[] areaBegin = new int[2];
        if (measurementView == null)
            return new Point(-1, -1);
        measurementView.getLocationOnScreen(areaBegin);
        return new Point(areaBegin[0], areaBegin[1]);
    }

    public static Point getViewSize(View measurementView) {
        if (measurementView == null)
            return new Point(-1, -1);
        return new Point(measurementView.getMeasuredWidth(), measurementView.getMeasuredHeight());
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = resources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
