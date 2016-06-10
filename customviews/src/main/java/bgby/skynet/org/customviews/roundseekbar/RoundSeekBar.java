package bgby.skynet.org.customviews.roundseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import bgby.skynet.org.customviews.R;
import bgby.skynet.org.customviews.circlecentrelayout.ICentredView;

/**
 * Created by Clariones on 6/8/2016.
 */
public class RoundSeekBar extends View implements ICentredView {
    private static final String TAG = "RoundSeekBar";
    private final Attributes attributes;
    protected OnProgressChangedListener listener;
    private Point circleCentrePoint;
    private float maxDiameter;
    private float cavityRatio;
    private float renderDiameter;
    private float progress;
    private Bitmap bmpBackground;
    private Paint onDrawPaint;
    private Bitmap bmpMask;

    public RoundSeekBar(Context context) {
        this(context, null);
    }

    public RoundSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attributes = new Attributes();
        if (attrs == null) {
            return;
        }
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.RoundSeekBar);
        attributes.loadFromTypedArray(t);
        t.recycle();
    }

    public float getDiameter() {
        return attributes.getDiameter();
    }

    public void setMinValue(float minValue) {
        attributes.setMinValue(minValue);
    }

    public int getDegree() {
        return attributes.getDegree();
    }

    public void setDiameter(float diameter) {
        attributes.setDiameter(diameter);
    }

    public void setArrow(Drawable arrow) {
        attributes.setArrow(arrow);
    }

    public float getValueLowLimit() {
        return attributes.getValueLowLimit();
    }

    public Drawable getWheel() {
        return attributes.getWheel();
    }

    public void setValueLowLimit(float valueLowLimit) {
        attributes.setValueLowLimit(valueLowLimit);
    }

    public void setMaxValue(float maxValue) {
        attributes.setMaxValue(maxValue);
    }

    public float getMinValue() {
        return attributes.getMinValue();
    }

    public void setDegree(int degree) {
        attributes.setDegree(degree);
    }

    public void setValueHighLimit(float valueHighLimit) {
        attributes.setValueHighLimit(valueHighLimit);
    }

    public float getMaxValue() {
        return attributes.getMaxValue();
    }

    public void setStartDegree(float startDegree) {
        attributes.setStartDegree(startDegree);
    }

    public Drawable getArrow() {
        return attributes.getArrow();
    }

    public float getStartDegree() {
        return attributes.getStartDegree();
    }

    public float getValueHighLimit() {
        return attributes.getValueHighLimit();
    }

    public void setWheel(Drawable wheel) {
        attributes.setWheel(wheel);
    }

    public OnProgressChangedListener getOnProgressChangedListener() {
        return listener;
    }

    public void setOnProgressChangedListener(OnProgressChangedListener listener) {
        this.listener = listener;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        float drawDiameter = Math.min(sizeWidth, sizeHeight);
        if (attributes.getDiameter() > 0 && attributes.getDiameter() < drawDiameter) {
            drawDiameter = attributes.getDiameter();
        }
        if (getMaxDiameter() > 0.0 && getMaxDiameter() < drawDiameter) {
            drawDiameter = getMaxDiameter();
        }
        renderDiameter = drawDiameter;
        circleCentrePoint = new Point((int) renderDiameter / 2, (int) renderDiameter / 2);
        Log.d(TAG, "Measurement is " + renderDiameter + "x" + renderDiameter);
        setMeasuredDimension((int) renderDiameter, (int) renderDiameter);
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
        maxDiameter = diameter;
    }

    @Override
    public float getCavityRatio() {
        return 0;
    }

    @Override
    public void setCavityRatio(float cavityRatio) {
        // This element don't have cavity ratio can be set
    }

    @Override
    public int getCavityDiameter() {
        return -1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "EVENT ACTION=" + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Log.i(TAG, "DOWN EVENT");
//                Utils.handleActionDown(config, event);
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "MOVE EVENT");
                Double standardTouchAngle = calcTouchDegree(event);
                if (standardTouchAngle == null) {
                    return false;
                }
                Double turnedAngel = handleSlideBoundary(standardTouchAngle);
                if (turnedAngel == null) {
                    return false;
                }
                double touchValue = convertToTouchValue(turnedAngel);
                Log.d(TAG, "Turn progress to " + touchValue);
                setProgress((float) touchValue);
                if (listener != null) {
                    listener.onProgressChanged(this, getProgress());
                }
                return true;
            default:
                break;
        }
        return true;
    }

    @Nullable
    private Double handleSlideBoundary(Double standardTouchAngle) {
        Double turnedAngel = (360 + standardTouchAngle - attributes.getStartDegree()) % 360;
        Log.d(TAG, "touch on " + turnedAngel);
        if (turnedAngel > attributes.getDegree()) {
            double unusedDegree = 360 - attributes.getDegree();
            double lowLimit = 360 - 10;
            double highLimit = attributes.getDegree() + 10;
            if (unusedDegree < 20) {
                lowLimit = 360 - unusedDegree / 2;
                highLimit = attributes.getDegree() + unusedDegree / 2;
            }
            if (turnedAngel > lowLimit) {
                turnedAngel = 0.0;
            } else if (turnedAngel < highLimit) {
                turnedAngel = Double.valueOf(attributes.getDegree());
            } else {
                turnedAngel = null;
            }
        }
        return turnedAngel;
    }

    private Double calcTouchDegree(MotionEvent event) {
        float x0 = renderDiameter / 2;
        float y0 = renderDiameter / 2;
        float x1 = event.getX();
        float y1 = event.getY();

        double dtX = x1 - x0;
        double dtY = y1 - y0;

        return Utils.calcTouchDegree(x0, y0, x1, y1);
    }

    private double convertToTouchValue(double turnedAngel) {
        double touchValue = turnedAngel / attributes.getDegree() * (attributes.getMaxValue() - attributes.getMinValue()) + attributes.getMinValue();
        if (touchValue < attributes.getValueLowLimit()) {
            touchValue = attributes.getValueLowLimit();
        } else if (touchValue > attributes.getValueHighLimit()) {
            touchValue = attributes.getValueHighLimit();
        }
        return touchValue;
    }

    private void prepareDrawing() {
        if (bmpBackground != null) {
            return;
        }
        int radius = (int) (renderDiameter / 2);
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        // 设置画布的颜色为透明
        // 划出你要显示的圆
        Bitmap mask = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas(mask);
        cc.drawCircle(radius, radius, radius - 2, mPaint);
        onDrawPaint = mPaint;
        bmpMask = mask;
        // 再绘制src源图
        Drawable bgDrawable = attributes.getWheel();
        if (bgDrawable == null) {
            bgDrawable = getResources().getDrawable(R.drawable.roundsliderbkg);
            attributes.setWheel(bgDrawable);
        }
        Bitmap bgBitmap = ((BitmapDrawable) bgDrawable).getBitmap();
        Bitmap tmpBmp = Bitmap.createScaledBitmap(bgBitmap, radius * 2, radius * 2, false);
        bmpBackground = tmpBmp;

        Drawable ptDrawable = attributes.getArrow();
        if (ptDrawable == null) {
            ptDrawable = getResources().getDrawable(R.drawable.roundsliderarrow);
            attributes.setArrow(ptDrawable);
        }
        ptDrawable.setBounds(radius * 9 / 10, 0, radius * 11 / 10, radius * 2 / 10);
    }


    protected void onDraw(Canvas canvas) {

        int radius = (int) (renderDiameter / 2 );
        canvas.save();
        double drawProgress = getProgress();
//        drawProgress = 16;
        if (drawProgress > attributes.getValueHighLimit()) {
            drawProgress = attributes.getValueHighLimit();
        } else if (drawProgress < attributes.getValueLowLimit()) {
            drawProgress = attributes.getValueLowLimit();
        }
        double progressAngel = (drawProgress - attributes.getMinValue()) / (attributes.getMaxValue() - attributes.getMinValue()) * attributes.getDegree();
        int rAngel = (int) -(270 - progressAngel - attributes.getStartDegree());
        canvas.rotate(rAngel, radius, radius);
//        super.onDraw(canvas);

        prepareDrawing();
        // 设置画布的颜色为透明
        canvas.drawColor(Color.TRANSPARENT);
        // 划出你要显示的圆
        canvas.saveLayer(0, 0, getMeasuredWidth(), getMeasuredHeight(), null, Canvas.ALL_SAVE_FLAG);
        // 先绘制遮罩层
        canvas.drawBitmap(bmpMask, 0, 0, onDrawPaint);
        // 设置混合模式
        onDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 再绘制src源图
        canvas.drawBitmap(bmpBackground, 0, 0, onDrawPaint);
        // 还原混合模式
        onDrawPaint.setXfermode(null);
        // 还原画布，相当于Ps的合并图层
        canvas.restore();

        Drawable ptDrawable = attributes.getArrow();
        ptDrawable.draw(canvas);

        canvas.restore();
    }
}

