package bgby.skynet.org.customviews.circlecentrelayout;

import android.view.View;

/**
 * Created by Clariones on 6/9/2016.
 */
public class MeasureData {
    private View view;
    protected boolean handled = false;
    protected int priority = 0;
    private MeasureData dependentData;
    private int top;
    private int left;
    private int right;

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    private int bottom;
    public MeasureData getDependentData() {
        return dependentData;
    }

    public void setDependentData(MeasureData dependentData) {
        this.dependentData = dependentData;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}

