package bgby.skynet.org.customviews.circlecentrelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;

import bgby.skynet.org.customviews.R;

/**
 * Created by Clariones on 6/9/2016.
 */
public class LayoutParams extends ViewGroup.LayoutParams {
    private int insideId = -1;

    public int getInsideId() {
        return insideId;
    }

    public void setInsideId(int insideId) {
        this.insideId = insideId;
    }

    public LayoutParams(Context c, AttributeSet attrs) {
        super(c, attrs);
        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.CircleCentreRelaytiveLayout);
        try{
            insideId = a.getResourceId(R.styleable.CircleCentreRelaytiveLayout_layout_inside, -1);
        }finally {
            a.recycle();
        }

    }

    public LayoutParams(int width, int height) {
        super(width, height);
    }

    public LayoutParams(ViewGroup.LayoutParams source) {
        super(source);
    }
}
