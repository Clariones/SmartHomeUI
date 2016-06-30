package bgby.skynet.org.customviews.arcprogressbar;

import android.content.res.TypedArray;
import android.graphics.Color;

import bgby.skynet.org.customviews.R;


/**
 * Created by Clariones on 6/6/2016.
 */
public class Attributes {

    private float diameter = -1.0f;
    private int degree = 180;
    private boolean hasTickMark = true;
    private float tickMarkLineLength = 0.0f;
    private float tickMarkTextSize = 0.0f;
    private int tickMarkColor = Color.WHITE;
    private int progressBarBackgroundColor = Color.BLACK;
    private int progressBarLowColor = Color.BLUE;
    private int progressBarMiddleColor = Color.GREEN;
    private int progressBarHighColor = Color.RED;
    private float minValue = 0.0f;
    private float maxValue = 100.0f;
    private float cavityRatio;
    private float barWidth =  10.0f;

    public float getBarWidth() {
        return barWidth;
    }

    public void setBarWidth(float barWidth) {
        this.barWidth = barWidth;
    }

    public void loadFromTypedArray(TypedArray t) {
        diameter = t.getDimension(R.styleable.ArcProgressBar_diameter, diameter);
        degree = t.getInt(R.styleable.ArcProgressBar_degree, degree);
        if (degree < 5 || degree > 355){
            throw new RuntimeException("degree must be in [5,355]");
        }
        hasTickMark = t.getBoolean(R.styleable.ArcProgressBar_hasTickMark, hasTickMark);
        tickMarkLineLength = t.getDimension(R.styleable.ArcProgressBar_tickMarkLineLength, tickMarkLineLength);
        tickMarkTextSize = t.getDimension(R.styleable.ArcProgressBar_tickMarkTextSize, tickMarkTextSize);
        barWidth = t.getDimension(R.styleable.ArcProgressBar_progressBarWidth, barWidth);

        tickMarkColor = t.getColor(R.styleable.ArcProgressBar_tickMarkColor, tickMarkColor);
        progressBarBackgroundColor = t.getColor(R.styleable.ArcProgressBar_progressBarBackgroundColor, progressBarBackgroundColor);
        progressBarLowColor = t.getColor(R.styleable.ArcProgressBar_progressBarLowColor, progressBarLowColor);
        progressBarMiddleColor = t.getColor(R.styleable.ArcProgressBar_progressBarMiddleColor, progressBarMiddleColor);
        progressBarHighColor = t.getColor(R.styleable.ArcProgressBar_progressBarHighColor, progressBarHighColor);
        minValue = t.getFloat(R.styleable.ArcProgressBar_minValue, minValue);
        maxValue = t.getFloat(R.styleable.ArcProgressBar_maxValue, maxValue);
        cavityRatio = t.getFloat(R.styleable.ArcProgressBar_cavityRatio, cavityRatio);
    }

    public float getDiameter() {
        return diameter;
    }

    public void setDiameter(float diameter) {
        this.diameter = diameter;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public boolean isHasTickMark() {
        return hasTickMark;
    }

    public void setHasTickMark(boolean hasTickMark) {
        this.hasTickMark = hasTickMark;
    }

    public float getTickMarkLineLength() {
        return tickMarkLineLength;
    }

    public void setTickMarkLineLength(float tickMarkLineLength) {
        this.tickMarkLineLength = tickMarkLineLength;
    }

    public float getTickMarkTextSize() {
        return tickMarkTextSize;
    }

    public void setTickMarkTextSize(float tickMarkTextSize) {
        this.tickMarkTextSize = tickMarkTextSize;
    }

    public int getTickMarkColor() {
        return tickMarkColor;
    }

    public void setTickMarkColor(int tickMarkColor) {
        this.tickMarkColor = tickMarkColor;
    }

    public int getProgressBarBackgroundColor() {
        return progressBarBackgroundColor;
    }

    public void setProgressBarBackgroundColor(int progressBarBackgroundColor) {
        this.progressBarBackgroundColor = progressBarBackgroundColor;
    }

    public int getProgressBarLowColor() {
        return progressBarLowColor;
    }

    public void setProgressBarLowColor(int progressBarLowColor) {
        this.progressBarLowColor = progressBarLowColor;
    }

    public int getProgressBarMiddleColor() {
        return progressBarMiddleColor;
    }

    public void setProgressBarMiddleColor(int progressBarMiddleColor) {
        this.progressBarMiddleColor = progressBarMiddleColor;
    }

    public int getProgressBarHighColor() {
        return progressBarHighColor;
    }

    public void setProgressBarHighColor(int progressBarHighColor) {
        this.progressBarHighColor = progressBarHighColor;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getCavityRatio() {
        return cavityRatio;
    }

    public void setCavityRatio(float cavityRatio) {
        this.cavityRatio = cavityRatio;
    }
}
