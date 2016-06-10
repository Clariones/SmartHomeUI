package bgby.skynet.org.customviews.arcprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Iterator;
import java.util.List;

import bgby.skynet.org.customviews.R;
import bgby.skynet.org.customviews.circlecentrelayout.ICentredView;

/**
 * Created by Clariones on 6/6/2016.
 */
public class ArcProgressBar extends View implements ICentredView {
    public static final float TEMPLATE_DIAMTER = 10000.0f;
    public static final int BAR_WIDTH = 10;
    protected static final String TAG = "ArcProgressBar";
    protected static final float FACTOR_TEXT_TO_LENGTH = 2.8f;
    protected double layoutDiameter = 0;
    protected double renderDiameter = 0;
    protected Attributes attributes;
    protected float maxDiameter = -1.0f;
    protected Utils.DrawAreaData renderDrawArea;
    protected Point circleCentrePoint;
    protected float progress = 0.0f;
    private Bitmap backgroundBitmap;

    public ArcProgressBar(Context context) {
        this(context, null);
    }

    public ArcProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attributes = new Attributes();
        if (attrs == null) {
            return;
        }
        TypedArray t = context.obtainStyledAttributes(attrs,
                R.styleable.ArcProgressBar);
        attributes.loadFromTypedArray(t);
        t.recycle();
    }

    protected void clearCachedCalculation() {
        renderDrawArea = null;
        backgroundBitmap = null;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        postInvalidate();
    }

    public float getTickMarkTextSize() {
        return attributes.getTickMarkTextSize();
    }

    public void setTickMarkTextSize(float tickMarkTextSize) {
        clearCachedCalculation();
        attributes.setTickMarkTextSize(tickMarkTextSize);
    }

    public int getProgressBarHighColor() {
        return attributes.getProgressBarHighColor();
    }

    public void setProgressBarHighColor(int progressBarHighColor) {
        clearCachedCalculation();
        attributes.setProgressBarHighColor(progressBarHighColor);
    }

    public float getMaxValue() {
        return attributes.getMaxValue();
    }

    public void setMaxValue(float maxValue) {
        clearCachedCalculation();
        attributes.setMaxValue(maxValue);
    }

    public int getProgressBarBackgroundColor() {
        return attributes.getProgressBarBackgroundColor();
    }

    public void setProgressBarBackgroundColor(int progressBarBackgroundColor) {
        clearCachedCalculation();
        attributes.setProgressBarBackgroundColor(progressBarBackgroundColor);
    }

    public int getDegree() {
        return attributes.getDegree();
    }

    public void setDegree(int degree) {
        clearCachedCalculation();
        attributes.setDegree(degree);
    }

    public int getProgressBarMiddleColor() {
        return attributes.getProgressBarMiddleColor();
    }

    public void setProgressBarMiddleColor(int progressBarMiddleColor) {
        clearCachedCalculation();
        attributes.setProgressBarMiddleColor(progressBarMiddleColor);
    }

    public float getDiameter() {
        return attributes.getDiameter();
    }

    public void setDiameter(float diameter) {
        clearCachedCalculation();
        attributes.setDiameter(diameter);
    }

    public float getTickMarkLineLength() {
        return attributes.getTickMarkLineLength();
    }

    public void setTickMarkLineLength(float tickMarkLineLength) {
        clearCachedCalculation();
        attributes.setTickMarkLineLength(tickMarkLineLength);
    }

    public int getProgressBarLowColor() {
        return attributes.getProgressBarLowColor();
    }

    public void setProgressBarLowColor(int progressBarLowColor) {
        clearCachedCalculation();
        attributes.setProgressBarLowColor(progressBarLowColor);
    }

    public boolean isHasTickMark() {
        return attributes.isHasTickMark();
    }

    public void setHasTickMark(boolean hasTickMark) {
        clearCachedCalculation();
        attributes.setHasTickMark(hasTickMark);
    }

    public float getMinValue() {
        return attributes.getMinValue();
    }

    public void setMinValue(float minValue) {
        clearCachedCalculation();
        attributes.setMinValue(minValue);
    }

    public int getTickMarkColor() {
        return attributes.getTickMarkColor();
    }

    public void setTickMarkColor(int tickMarkColor) {
        attributes.setTickMarkColor(tickMarkColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //int mhoffset = (int) (attributes.getTickMarkTextSize() * FACTOR_TEXT_TO_LENGTH / 2 - attributes.getTickMarkLineLength());
        int mhoffset = 0;
        if (renderDrawArea != null) {
            int measureWidth = (int) (renderDrawArea.x1 - renderDrawArea.x0);
            int measureHeight = (int) (renderDrawArea.y1 - renderDrawArea.y0);
            Log.d(TAG, "Measurement is " + measureWidth + "x" + measureHeight);
            setMeasuredDimension(measureWidth, measureHeight + mhoffset);
            return;
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        Log.i(TAG, String.format("DEBUG: onMeasure (%x)%d,(%x)%d", widthMode, sizeWidth, heightMode, sizeHeight));
        // calculate draw area template
        Utils.DrawAreaData template = Utils.calculateDrawArea(TEMPLATE_DIAMTER, attributes.getDegree());
        Log.d(TAG, "Template " + template);
        // calculate the tickmark delta-area
        Utils.DrawAreaData deltaArea = null;
        if (attributes.isHasTickMark() == true) {
            deltaArea = Utils.calculateDeltaAreaWithTickMark(BAR_WIDTH, template, attributes.getDegree(),
                    attributes.getTickMarkLineLength(), attributes.getTickMarkTextSize());
        } else {
            deltaArea = Utils.calculateDeltaAreaWithoutTickMark(BAR_WIDTH, template, attributes.getDegree());
        }

        // calculate 3 possible draw area
        Utils.DrawAreaData drawAreaByDiameter = calcDrawAreaByDiameter(template, deltaArea);
        Log.d(TAG, "By set diameter, in " + drawAreaByDiameter);
        Utils.DrawAreaData drawAreaByLayoutWidthAndHeight = calcDrawAreaByLayoutWidthAndHeight(template, deltaArea, sizeWidth, sizeHeight);
        Log.d(TAG, "By layout container, in " + drawAreaByLayoutWidthAndHeight);
        Utils.DrawAreaData drawAreaByMaxDiameter = calcDrawAreaByLayoutMaxDiameter(template, deltaArea);
        Log.d(TAG, "By layout max diameter, in " + drawAreaByMaxDiameter);


        Utils.DrawAreaData resultDrawArea = drawAreaByLayoutWidthAndHeight;

        // then get the minimal radius
        double diameter = layoutDiameter;
        if (attributes.getDiameter() > 0.0 && attributes.getDiameter() < diameter) {
            diameter = attributes.getDiameter();
            resultDrawArea = drawAreaByDiameter;
        }
        if (getMaxDiameter() > 0.0 && getMaxDiameter() < diameter) {
            diameter = getMaxDiameter();
            resultDrawArea = drawAreaByMaxDiameter;
        }
        renderDiameter = diameter;
        renderDrawArea = resultDrawArea;
        Log.d(TAG, "Finally, draw in " + renderDrawArea);

        int x0 = (int) (0 - resultDrawArea.x0);
        int y0 = (int) (0 - resultDrawArea.y0);
        circleCentrePoint = new Point(x0, y0);

        int measureWidth = (int) (renderDrawArea.x1 - renderDrawArea.x0);
        int measureHeight = (int) (renderDrawArea.y1 - renderDrawArea.y0);
        Log.d(TAG, "Measurement is " + measureWidth + "x" + measureHeight);
        setMeasuredDimension(measureWidth, measureHeight + mhoffset);
    }

    protected Utils.DrawAreaData calcDrawAreaByLayoutMaxDiameter(Utils.DrawAreaData template, Utils.DrawAreaData deltaArea) {
        if (getMaxDiameter() <= 0.0) {
            return null; // if not set max-radius, then ignore its related limitation
        }
        return calcDrawAreaData(template, deltaArea, getMaxDiameter());
    }

    protected Utils.DrawAreaData calcDrawAreaByLayoutWidthAndHeight(Utils.DrawAreaData template, Utils.DrawAreaData deltaArea, int sizeWidth, int sizeHeight) {
        int drawAreaWidth = sizeWidth;
        int drawAreaHeight = sizeHeight;
        if (deltaArea != null) {
            drawAreaWidth -= (deltaArea.x1 - deltaArea.x0);
            drawAreaHeight -= (deltaArea.y1 - deltaArea.y0);
        }
        if (drawAreaWidth < 0 || drawAreaHeight < 0) {
            return new Utils.DrawAreaData(0, 0, 0, 0); // don't draw anything if too small area
        }
        double ratioWvH = (template.x1 - template.x0) / (template.y1 - template.y0);
        double layoutRatioWvH = 1.0 * drawAreaWidth / drawAreaHeight;
        double factor = drawAreaWidth / (template.x1 - template.x0);
        if (layoutRatioWvH > ratioWvH) {
            factor = drawAreaHeight / (template.y1 - template.y0);
        }
        layoutDiameter = TEMPLATE_DIAMTER * factor;
        Utils.DrawAreaData barArea = new Utils.DrawAreaData(template, factor);
        barArea.addDeltaArea(deltaArea);
        return barArea;
    }

    protected Utils.DrawAreaData calcDrawAreaByDiameter(Utils.DrawAreaData template, Utils.DrawAreaData deltaArea) {
        if (attributes.getDiameter() <= 0.0) {
            return null; // if not set diameter, then ignore its limitation
        }

        double setDiameter = attributes.getDiameter();
        return calcDrawAreaData(template, deltaArea, setDiameter);
    }

    @NonNull
    protected Utils.DrawAreaData calcDrawAreaData(Utils.DrawAreaData template, Utils.DrawAreaData deltaArea, double setDiameter) {
        Utils.DrawAreaData innerArea = new Utils.DrawAreaData(template, setDiameter / this.TEMPLATE_DIAMTER);
        innerArea.addDeltaArea(deltaArea);
        return innerArea;
    }

    @Override
    public Point getCircleCentrePoint() {
        return circleCentrePoint;
    }

    @Override
    public float getMaxDiameter() {
        return maxDiameter;
    }

    @Override
    public void setMaxDiameter(float diameter) {
        clearCachedCalculation();
        maxDiameter = diameter;
    }

    @Override
    public float getCavityRatio() {
        return attributes.getCavityRatio();
    }

    @Override
    public void setCavityRatio(float cavityRatio) {
        attributes.setCavityRatio(cavityRatio);
    }

    @Override
    public int getCavityDiameter() {
        return (int) (renderDiameter * getCavityRatio());
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Paint paint1 = new Paint();
        paint1.setColor(attributes.getProgressBarBackgroundColor()); //设置圆环的颜色
        paint1.setStyle(Paint.Style.STROKE); //设置空心
        paint1.setStrokeWidth(BAR_WIDTH); //设置圆环的宽度
        paint1.setAntiAlias(true);  //消除锯齿
        paint1.setStrokeCap(Paint.Cap.ROUND);

        Bitmap bkgBitmap = getBackgoundBitmap();
        canvas.drawBitmap(bkgBitmap, 0, 0, paint1);

        canvas.save();
        canvas.rotate((270 - attributes.getDegree() / 2), getCircleCentrePoint().x, getCircleCentrePoint().y);
        SweepGradient shader = Utils.getGradient(attributes, getCircleCentrePoint().x, getCircleCentrePoint().y);
        Paint paint2 = new Paint();
        paint2.setStyle(Paint.Style.STROKE); //设置空心
        paint2.setStrokeWidth(BAR_WIDTH); //设置圆环的宽度
        paint2.setAntiAlias(true);  //消除锯齿
        paint2.setStrokeCap(Paint.Cap.ROUND);
        paint2.setShader(shader);
        RectF rectF = new RectF((float) (-renderDiameter / 2 + getCircleCentrePoint().x),
                (float) (-renderDiameter / 2 + getCircleCentrePoint().y),
                (float) (renderDiameter / 2 + getCircleCentrePoint().x),
                (float) (renderDiameter / 2 + getCircleCentrePoint().y));
        float drawProgress = getProgress();
        if (drawProgress > attributes.getMaxValue()) {
            drawProgress = attributes.getMaxValue();
        }
        if (drawProgress < attributes.getMinValue()) {
            drawProgress = attributes.getMinValue();
        }
        Log.i(TAG, "Draw progress " + drawProgress);
        float endAngle = (drawProgress - attributes.getMinValue()) / (attributes.getMaxValue() - attributes.getMinValue()) * attributes.getDegree();
        canvas.drawArc(rectF, 0, endAngle, false, paint2);
        canvas.restore();
    }

    private Bitmap getBackgoundBitmap() {
        if (this.backgroundBitmap != null) {
            return backgroundBitmap;
        }

        backgroundBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(backgroundBitmap);
//        Canvas canvas = new Canvas();
        // draw tick mark first
        if (attributes.isHasTickMark()) {
            List<Utils.TickMarkData> marks = Utils.calcGraduation(attributes.getDegree(),
                    this.renderDiameter, attributes.getMinValue(), attributes.getMaxValue());
            canvas.save();
            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(attributes.getTickMarkColor());
            textPaint.setStrokeWidth(3);
            textPaint.setTextSize(attributes.getTickMarkTextSize());
            textPaint.setTextAlign(Paint.Align.CENTER);
//            textPaint.setShader(Utils.getGradient(attributes, getCircleCentrePoint().x, getCircleCentrePoint().y));
            Iterator<Utils.TickMarkData> it = marks.iterator();
            double startVal = attributes.getMinValue();
            double factor = 1.0 * attributes.getDegree() / (attributes.getMaxValue() - attributes.getMinValue());
            int spX0 = getCircleCentrePoint().x;
            int spY0 = (int) (getCircleCentrePoint().y - renderDiameter / 2 - BAR_WIDTH / 2);
            int spY1 = (int) (spY0 - attributes.getTickMarkLineLength() * 0.5);
            int spY2 = (int) (spY0 - attributes.getTickMarkLineLength());
            int spY3 = (int) (spY0 - attributes.getTickMarkLineLength() - 5);
            canvas.rotate(-attributes.getDegree() / 2.0f, getCircleCentrePoint().x, getCircleCentrePoint().y);
            float roatedDegree = 0.0f;
            while (it.hasNext()) {
                Utils.TickMarkData data = it.next();
                double markDegree = (data.value - startVal) * factor;
                canvas.rotate((float) (markDegree - roatedDegree), getCircleCentrePoint().x, getCircleCentrePoint().y);
                roatedDegree = (float) markDegree;
                if (data.isTiny) {
                    canvas.drawLine(spX0, spY1, spX0, spY2, textPaint);
                } else {
                    canvas.drawLine(spX0, spY0, spX0, spY2, textPaint);
                    String strValue = String.valueOf(data.value);
                    strValue = strValue.replaceAll("\\.?0*$", "");
                    canvas.drawText(strValue, spX0, spY3, textPaint);
                }
            }
            canvas.restore();
        }

        canvas.save();
        canvas.rotate(270 - attributes.getDegree() / 2, getCircleCentrePoint().x, getCircleCentrePoint().y);
        Utils.DrawAreaData drawArea = Utils.calculateDrawArea(renderDiameter, attributes.getDegree());
        RectF rectF = new RectF((float) (-renderDiameter / 2 + getCircleCentrePoint().x),
                (float) (-renderDiameter / 2 + getCircleCentrePoint().y),
                (float) (renderDiameter / 2 + getCircleCentrePoint().x),
                (float) (renderDiameter / 2 + getCircleCentrePoint().y));
        Paint paint1 = new Paint();
        paint1.setColor(attributes.getProgressBarBackgroundColor()); //设置圆环的颜色
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeWidth(BAR_WIDTH);
        paint1.setAntiAlias(true);
        paint1.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(rectF, 0, attributes.getDegree(), false, paint1);
        canvas.restore();
        return backgroundBitmap;
    }

}
