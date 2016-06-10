package bgby.skynet.org.customviews.roundseekbar;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import bgby.skynet.org.customviews.R;

/**
 * Created by Clariones on 6/8/2016.
 */
public class Attributes {
    private float diameter = -1.0f;
    private int degree = 270;
    private float startDegree = 180.0f;
    private float minValue = 0.0f;
    private float maxValue = 100.0f;
    private Drawable wheel;
    private float valueLowLimit = 0.0f;
    private float valueHighLimit = 100.0f;
    private Drawable arrow;

    public float getDiameter() {
        return diameter;
    }

    public void setDiameter(float diameter) {
        this.diameter = diameter;
    }

    public float getStartDegree() {
        return startDegree;
    }

    public void setStartDegree(float startDegree) {
        this.startDegree = startDegree;
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

    public Drawable getWheel() {
        return wheel;
    }

    public void setWheel(Drawable wheel) {
        this.wheel = wheel;
    }

    public Drawable getArrow() {
        return arrow;
    }

    public void setArrow(Drawable arrow) {
        this.arrow = arrow;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public float getValueLowLimit() {
        return valueLowLimit;
    }

    public void setValueLowLimit(float valueLowLimit) {
        this.valueLowLimit = valueLowLimit;
    }

    public float getValueHighLimit() {
        return valueHighLimit;
    }

    public void setValueHighLimit(float valueHighLimit) {
        this.valueHighLimit = valueHighLimit;
    }

    public void loadFromTypedArray(TypedArray t) {
        diameter = t.getDimension(R.styleable.RoundSeekBar_diameter, diameter);
        degree = t.getInt(R.styleable.RoundSeekBar_degree, degree);
        startDegree = t.getFloat(R.styleable.RoundSeekBar_startDegree, startDegree);
        minValue = t.getFloat(R.styleable.RoundSeekBar_minValue, minValue);
        maxValue = t.getFloat(R.styleable.RoundSeekBar_maxValue, maxValue);
        valueLowLimit = t.getFloat(R.styleable.RoundSeekBar_valueLowLimit, minValue);
        valueHighLimit = t.getFloat(R.styleable.RoundSeekBar_valueHighLimit, maxValue);
        wheel = t.getDrawable(R.styleable.RoundSeekBar_wheel);
        arrow = t.getDrawable(R.styleable.RoundSeekBar_arrow);

    }

}
