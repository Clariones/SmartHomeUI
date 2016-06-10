package bgby.skynet.org.customviews.arcprogressbar;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SweepGradient;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clariones on 6/6/2016.
 */
public class Utils {
    private static final double GRADUATE_DISTANCE = 18.0;
    private static final String TAG = "ArcProgressBar.Utils";

    private Utils(){}

    public static DrawAreaData calculateDeltaAreaWithoutTickMark(int barWidth, DrawAreaData templateArea, int degree){
        double innerDiameter = ArcProgressBar.TEMPLATE_DIAMTER - barWidth;
        double outerDiameter = ArcProgressBar.TEMPLATE_DIAMTER + barWidth*1.5;

        DrawAreaData innerArea = new DrawAreaData(templateArea, innerDiameter/ArcProgressBar.TEMPLATE_DIAMTER);
        Log.i(TAG, "inner area is " + innerArea);
        DrawAreaData outerArea = new DrawAreaData(templateArea, outerDiameter/ArcProgressBar.TEMPLATE_DIAMTER);
        Log.i(TAG, "outer area is " + outerArea);
        outerArea.merge(innerArea);
        DrawAreaData rst = outerArea.deltaOfMerge(templateArea);
        Log.i(TAG, "Without tick mark delta area is " + rst);
        return rst;
    }

    public static DrawAreaData calculateDeltaAreaWithTickMark(int barWidth, DrawAreaData templateArea, int degree, float markLength, float textSize){
        double innerDiameter = ArcProgressBar.TEMPLATE_DIAMTER - barWidth;
        Paint tmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tmpPaint.setTextSize(textSize);
        tmpPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm = tmpPaint.getFontMetrics();
        Rect textBoundsRect = new Rect();
        tmpPaint.getTextBounds("100.0",0, 5,textBoundsRect);

        Log.i(TAG, String.format("Text size is top=%3.1f,ascent=%3.1f, descent=%3.1f, bottom=%3.1f for size%3.1f",
                fm.top, fm.ascent, fm.descent, fm.bottom, textSize));
        Log.i(TAG, String.format("    Bounder is %s", textBoundsRect));
        double delta = barWidth + markLength*2 + (fm.descent - fm.ascent)*2;
        double outerDiameter = ArcProgressBar.TEMPLATE_DIAMTER + delta;

        DrawAreaData innerArea = new DrawAreaData(templateArea, innerDiameter/ArcProgressBar.TEMPLATE_DIAMTER);
        DrawAreaData outerArea = new DrawAreaData(templateArea, outerDiameter/ArcProgressBar.TEMPLATE_DIAMTER);
        outerArea.merge(innerArea);

        if (degree <= 180){
            double alpha = Math.toRadians(degree/2);
            double dw = textBoundsRect.width() * Math.cos(alpha) / 2;
            double dh = textBoundsRect.width() * Math.sin(alpha) / 2;
            DrawAreaData deltaArea = outerArea.deltaOfMerge(templateArea);
            Log.i(TAG, String.format("    Delta area 1 is %s", deltaArea));
            deltaArea.x0 -= dw;
            deltaArea.x1 += dw;
            deltaArea.y1 += dh;
            Log.i(TAG, String.format("    Delta area 2 is %s", deltaArea));
            return deltaArea;
        }else{
            double alpha = Math.toRadians(180-degree/2);
            double dh = textBoundsRect.width() * Math.cos(alpha) / 2;
            double dw = 0;
            DrawAreaData deltaArea = outerArea.deltaOfMerge(templateArea);
            Log.i(TAG, String.format("    Delta area 1 is %s", deltaArea));
            deltaArea.y1 += dh;
            Log.i(TAG, String.format("    Delta area 2 is %s,%f", deltaArea,dh));
            return deltaArea;
        }
    }

    public static DrawAreaData calculateDrawArea(double diameter, int degree) {
        double r = diameter/2;
        DrawAreaData result;
        float x0=0,y0=0,x1=0,y1=0;

        if (degree >= 360){
            x1= (float) diameter;
            y1= (float) diameter;
            return new DrawAreaData(-r, -r, r, r);
        }

        if (degree > 180){
            double radian = Math.toRadians(degree/2);
            double width = diameter;
            double height = r*(1 - Math.cos(radian));
            result = new DrawAreaData(-r, -r, r, r*Math.abs(Math.cos(radian)));
            return result;
        }

        if (degree == 180){
            x1 = (float) diameter;
            y1 = (float) r;
            result = new DrawAreaData(-r, -r, r, 0.0);
            return result;
        }

        // < 180
        double radian = Math.toRadians(degree/2);
        result = new DrawAreaData(-r * Math.sin(radian), -r, r * Math.sin(radian), -r*Math.cos(radian));
        return result;
    }

    public static List<TickMarkData> calcGraduation(int degree, double diameter, double startValue, double endValue){
//        double totalDistance = config.getRadian()/180.0 * Math.PI * config.getRadius();
        double totalDistance = degree /180.0 * Math.PI * diameter / 2;
        int pieces = (int) (totalDistance/GRADUATE_DISTANCE);
        double onePiece = (endValue - startValue)/pieces;
        if (onePiece<=1){
            return pickSmallGrad(onePiece, startValue, endValue);
        }else {
            return pickLargeGrad(onePiece, startValue, endValue);
        }
    }

    private static List<TickMarkData> pickLargeGrad(double onePiece, double startValue, double endValue) {
        List<TickMarkData> tickMarks = new ArrayList<>();
        int scale = 1;
        double piece = onePiece;
        int calcTimes = 0;
//        double[] factors = {5,2};
        int[] factors={10,10};
        List<Double> sGrads = new ArrayList<Double>();
        List<Double> gGrads = new ArrayList<Double>();
        while(true){
            int factor = factors[calcTimes % 2];
//            System.out.printf("    scale=%d, piece=%f\n", scale, piece);
            calcTimes++;
            piece = piece / factor;
            scale = scale * factor;
            if (piece < 1){
                break;
            }
//            System.out.printf("now scale=%d, piece=%f\n", scale, piece);
        }
//        System.out.printf("final scale=%d, piece=%f\n", scale, piece);

        int startP = (int) Math.round(startValue*1.0/scale);
        int endP = (int)(endValue/scale);
        int curGrad = startP;
//        int times = 1;
        while(curGrad <= endP){
            if (curGrad % 5 == 0) {
                gGrads.add((double) (curGrad * scale));
                tickMarks.add(new TickMarkData(curGrad * scale, false));
            }else{
                sGrads.add((double) (curGrad * scale));
                tickMarks.add(new TickMarkData(curGrad * scale, true));
            }
            curGrad ++;
        }
        return tickMarks;
    }

    private static List<TickMarkData> pickSmallGrad(double onePiece, double startValue, double endValue) {
        List<TickMarkData> tickMarks = new ArrayList<>();
        double unit = 1.0f;
        int scale = 1;
        double piece = onePiece;
        int calcTimes = 0;
        int[] factors = {10,10};
        while(true){
            int factor = factors[calcTimes % 2];
            calcTimes++;
            piece = piece * factor;
            if (piece >= 1){
                break;
            }
            unit = unit / factor;
            scale = scale * factor;
//            System.out.printf("now the unit=%f, piece=%f\n", unit, piece);
        }
//        System.out.printf("final unit=%f, piece=%f, scale=%d\n", unit, piece, scale);

        int startP = (int) Math.round(startValue * scale);
        int endP = (int)(endValue * scale);
        int bigUnit = scale*5;
        int curGrad = startP;
//        int times = 1;
        List<Double> sGrads = new ArrayList<Double>();
        List<Double> gGrads = new ArrayList<Double>();
        while(curGrad <= endP){
            if (curGrad % 5 == 0) {
                gGrads.add(curGrad * 1.0 / scale);
                tickMarks.add(new TickMarkData(curGrad * 1.0 / scale, false));
            }else{
                sGrads.add(curGrad * 1.0 / scale);
                tickMarks.add(new TickMarkData(curGrad * 1.0 / scale, true));
            }
            curGrad ++;
        }
        return tickMarks;
    }

    public static SweepGradient getGradient(Attributes config, float x, float y){
        int[] colors = {
                config.getProgressBarLowColor(),
                config.getProgressBarMiddleColor(),
                config.getProgressBarHighColor(), config.getProgressBarHighColor(),
                config.getProgressBarLowColor(),config.getProgressBarLowColor(),
        };
        float totalFactor = config.getDegree()/360.0f;
        float startPoint = config.getMinValue();
        float endPoint = config.getMaxValue();
        float totalRange = endPoint - startPoint;
        float [] positions = {
                0f, // low-point end
                0.5f*totalFactor, // middle of low&high-point
                totalFactor,    // high-point end
                totalFactor + (1-totalFactor)*0.25f,    // hidden high-point
                totalFactor + (1-totalFactor)*0.75f,    // hidden low-point
                1f  // start point
        };
        SweepGradient shader = new SweepGradient(x,y, colors, positions);
        return shader;
    }

    public static class TickMarkData{
        public double value;
        public boolean isTiny;

        public TickMarkData(double value, boolean isTiny) {
            this.value = value;
            this.isTiny = isTiny;
        }
    }
    public static class DrawAreaData{
        public double x0;
        public double y0;
        public double x1;
        public double y1;

        public String toString(){
            return String.format("DrawArea:{%3.1f,%3.1f, %3.1f,%3.1f}", x0,y0,x1,y1);
        }
        public DrawAreaData(double x0, double y0, double x1, double y1) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
        }

        public DrawAreaData(DrawAreaData template, double factor) {
            this.x0 = template.x0 * factor;
            this.y0 = template.y0 * factor;
            this.x1 = template.x1 * factor;
            this.y1 = template.y1 * factor;
        }

        public void merge(DrawAreaData other) {
            if (other.x0 < this.x0){
                x0 = other.x0;
            }
            if (other.y0 < this.y0){
                y0 = other.y0;
            }
            if (other.x1 > this.x1){
                x1 = other.x1;
            }
            if (other.y1 > this.y1){
                y1 = other.y1;
            }
        }

        public DrawAreaData deltaOfMerge(DrawAreaData innerArea) {
            double dx0 = this.x0 > innerArea.x0 ? 0.0 : this.x0 - innerArea.x0;
            double dy0 = this.y0 > innerArea.y0 ? 0.0 : this.y0 - innerArea.y0;
            double dx1 = this.x1 < innerArea.x1 ? 0.0 : this.x1 - innerArea.x1;
            double dy1 = this.y1 < innerArea.y1 ? 0.0 : this.y1 - innerArea.y1;
            return new DrawAreaData(dx0, dy0, dx1, dy1);
        }

        public void addDeltaArea(DrawAreaData deltaArea) {
            this.x0 += deltaArea.x0;
            this.y0 += deltaArea.y0;
            this.x1 += deltaArea.x1;
            this.y1 += deltaArea.y1;
        }

    }
}
