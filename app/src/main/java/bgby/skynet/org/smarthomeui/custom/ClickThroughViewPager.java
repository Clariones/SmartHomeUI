package bgby.skynet.org.smarthomeui.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Clariones on 6/3/2016.
 */
public class ClickThroughViewPager extends ViewPager {
    public ClickThroughViewPager(Context context) {
        super(context);
    }

    public ClickThroughViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
