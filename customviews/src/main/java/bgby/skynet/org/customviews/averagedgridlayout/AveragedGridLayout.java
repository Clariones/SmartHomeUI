package bgby.skynet.org.customviews.averagedgridlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import bgby.skynet.org.customviews.R;

/**
 * Created by Clariones on 6/3/2016.
 */
public class AveragedGridLayout extends ViewGroup {
    protected int rows= 1;
    protected int cols = 1;
    private double blockWidth;
    private double blockHeight;

    public AveragedGridLayout(Context context) {
        this(context, null);
    }

    public AveragedGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AveragedGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null){
            initWithAttrs(attrs);
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    private void initWithAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AveragedGridLayout);
        try{
            rows = a.getInt(R.styleable.AveragedGridLayout_hasRows, 1);
            cols = a.getInt(R.styleable.AveragedGridLayout_hasCols, 1);
        }finally {
            a.recycle();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount = getChildCount();
        for(int i=0;i<cCount;i++){
            View childView = getChildAt(i);
            int chdWidth = childView.getMeasuredWidth();
            int chdHeight = childView.getMeasuredHeight();

            LayoutParams lp = (LayoutParams) childView.getLayoutParams();

            int chdLeft = (int) ((lp.atCol-1) * blockWidth);
            int chdTop = (int) ((lp.atRow-1) * blockHeight);
            childView.layout(chdLeft, chdTop, chdLeft+chdWidth, chdTop+chdHeight);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
//        return super.checkLayoutParams(p);
        return p instanceof LayoutParams;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        blockWidth = sizeWidth * 1.0/ cols;
        blockHeight = sizeHeight * 1.0/ rows;

        int chdWidthSpec = MeasureSpec.makeMeasureSpec((int)blockWidth, MeasureSpec.EXACTLY);
        int chdHeightSpec = MeasureSpec.makeMeasureSpec((int)blockHeight, MeasureSpec.EXACTLY);
        measureChildren(chdWidthSpec, chdHeightSpec);
    }

    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        final int size = getChildCount();;
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            int vsb = child.getVisibility();
            if (vsb == View.GONE){
                continue;
            }

            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int width = (int) (lp.hasCols * blockWidth);
            int height = (int) (lp.hasRows * blockHeight);
            int chdWidthSpec = MeasureSpec.makeMeasureSpec((int)width, MeasureSpec.EXACTLY);
            int chdHeightSpec = MeasureSpec.makeMeasureSpec((int)height, MeasureSpec.EXACTLY);
            measureChild(child, chdWidthSpec, chdHeightSpec);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }
    
    public static class LayoutParams extends ViewGroup.LayoutParams{
        private int atRow = 1;
        private int atCol = 1;
        private int hasRows = 1;
        private int hasCols = 1;
        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AveragedGridLayout);
            try{
                atRow = a.getInt(R.styleable.AveragedGridLayout_atRow, 1);
                atCol = a.getInt(R.styleable.AveragedGridLayout_atCol, 1);
                hasRows = a.getInt(R.styleable.AveragedGridLayout_hasRows, 1);
                hasCols = a.getInt(R.styleable.AveragedGridLayout_hasCols, 1);
            }finally {
                a.recycle();
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams srcLp) {
            super(srcLp);
        }

        public int getAtRow() {
            return atRow;
        }

        public void setAtRow(int atRow) {
            this.atRow = atRow;
        }

        public int getAtCol() {
            return atCol;
        }

        public void setAtCol(int atCol) {
            this.atCol = atCol;
        }

        public int getHasRows() {
            return hasRows;
        }

        public void setHasRows(int hasRows) {
            this.hasRows = hasRows;
        }

        public int getHasCols() {
            return hasCols;
        }

        public void setHasCols(int hasCols) {
            this.hasCols = hasCols;
        }
    }
}
