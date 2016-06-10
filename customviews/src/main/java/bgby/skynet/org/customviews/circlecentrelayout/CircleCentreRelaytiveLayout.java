package bgby.skynet.org.customviews.circlecentrelayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Clariones on 6/6/2016.
 */
public class CircleCentreRelaytiveLayout extends ViewGroup {
    private Logger logger = Logger.getLogger(CircleCentreRelaytiveLayout.class.getName());
    private static final String TAG = "CircleCentreLayout";
    private HashMap<Integer, MeasureData> childrenData;
    private int mTop;
    private int mLeft;
    private int mRight;
    private int mBottom;

    public CircleCentreRelaytiveLayout(Context context) {
        this(context, null);
    }

    public CircleCentreRelaytiveLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleCentreRelaytiveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cnt = getChildCount();
        for(int i = 0; i< cnt; i++){
            View childView = getChildAt(i);
            int id = childView.getId();
            MeasureData  data = childrenData.get(id);
            Log.i(TAG, String.format("Layout %s (%d,%d)->(%d,%d) to (%d,%d)->(%d,%d)",
                    childView.getClass().getSimpleName(),
                    data.getTop(), data.getLeft(), data.getRight(), data.getBottom(),
                    data.getLeft()-mLeft, data.getTop()-mTop,data.getRight()-mLeft, data.getBottom()-mTop));
            childView.layout(data.getLeft()-mLeft, data.getTop()-mTop,data.getRight()-mLeft, data.getBottom()-mTop);
            logger.log(Level.INFO, "Layout {0} ({1},{2})->({3},{4}) to ({5},{6})->({7},{8})",
                    new Object[]{data.getTop(), data.getLeft(), data.getRight(), data.getBottom(),
                            data.getLeft()-mLeft, data.getTop()-mTop,data.getRight()-mLeft, data.getBottom()-mTop});
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new bgby.skynet.org.customviews.circlecentrelayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return super.generateLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new bgby.skynet.org.customviews.circlecentrelayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        List<MeasureData> children = sortChildren();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        int top = 0, left = 0, right = 0, bottom = 0;
        Iterator<MeasureData> it = children.iterator();
        while (it.hasNext()) {
            MeasureData data = it.next();
            View childView = data.getView();
            setMaxDiameter(data, childView);
            this.measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            int childCentreX = childWidth / 2;
            int childCentreY = childHeight / 2;
            if (childView instanceof ICentredView) {
                ICentredView icView = (ICentredView) childView;
                childCentreX = icView.getCircleCentrePoint().x;
                childCentreY = icView.getCircleCentrePoint().y;
            }
            Log.i(TAG, String.format("Child %s size is (%d,%d) @ (%d,%d)", childView.getClass().getSimpleName(), childWidth, childHeight, childCentreX, childCentreY));
            data.setLeft(0 - childCentreX);
            data.setTop(0 - childCentreY);
            data.setRight(childWidth - childCentreX);
            data.setBottom(childHeight - childCentreY);
            Log.i(TAG,String.format("Child area is %d,%d->%d,%d", data.getTop(), data.getLeft(), data.getRight(), data.getBottom()));
            top = Math.min(top, data.getTop());
            left = Math.min(left, data.getLeft());
            right = Math.max(right, data.getRight());
            bottom = Math.max(bottom, data.getBottom());
        }
        this.mTop = top;
        this.mLeft = left;
        this.mRight = right;
        this.mBottom = bottom;

        Log.i(TAG, String.format("My area is %d,%d->%d,%d", mTop, mLeft, mRight, mBottom));
        this.setMeasuredDimension(right-left, bottom-top);
    }

    private void setMaxDiameter(MeasureData data, View childView) {
        if (data.getDependentData() == null) {
            return;
        }
        View dView = data.getDependentData().getView();
        if (!(dView instanceof ICentredView)) {
            return;
        }
        ICentredView cView = (ICentredView) dView;
        int maxDiameter = cView.getCavityDiameter();

        if (maxDiameter <= 0 || !(childView instanceof ICentredView)) {
            return;
        }
        ((ICentredView) childView).setMaxDiameter(maxDiameter);
    }

    private List<MeasureData> sortChildren() {
        this.childrenData = new HashMap<>();
        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = this.getChildAt(i);
            MeasureData data = new MeasureData();
            data.setView(child);
            int childId = child.getId();
            childrenData.put(childId, data);
        }

        Iterator<Map.Entry<Integer, MeasureData>> it = childrenData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, MeasureData> ent = it.next();
            int id = ent.getKey();
            MeasureData data = ent.getValue();
            handleOneData(data, childrenData);
        }

        List<MeasureData> sortedChildren = new ArrayList<>(childrenData.values());
        Collections.sort(sortedChildren, new Comparator<MeasureData>() {
            @Override
            public int compare(MeasureData lhs, MeasureData rhs) {
                return lhs.getPriority() - rhs.getPriority();
            }
        });
        return sortedChildren;
    }

    private void handleOneData(MeasureData data, Map<Integer, MeasureData> children) {
        if (data.isHandled()) {
            return;
        }

        View view = data.getView();
        bgby.skynet.org.customviews.circlecentrelayout.LayoutParams lp = (bgby.skynet.org.customviews.circlecentrelayout.LayoutParams) view.getLayoutParams();
        int insideId = lp.getInsideId();
        if (insideId <= 0) {
            data.setHandled(true);
            return;
        }

        MeasureData dependedData = children.get(insideId);
        handleOneData(dependedData, children);
        data.setDependentData(dependedData);
        data.setPriority(dependedData.getPriority() + 1);
        data.setHandled(true);
    }
}
