package bgby.skynet.org.customviews.roundseekbar;

import android.util.Log;

/**
 * Created by Clariones on 6/8/2016.
 */
public class Utils {
    private static final String TAG = "RoundSeekBar.Utils";

    private Utils(){}

    public static Double calcTouchDegree(float x0, float y0, float x1, float y1){
        double dtX = x1 - x0;
        double dtY = y1 - y0;

        double r = Math.sqrt(dtX * dtX + dtY * dtY);
        double rFactor = r / x0;
        Log.d(TAG, "Distance is " + r + " as " + rFactor);
        if (rFactor < 0.5 || rFactor > 1.3) {
            return null;
        }
        double sinVale = dtY / r;
        double rstAngel;

        if (dtX < 0) {
            rstAngel = Math.PI - Math.asin(sinVale);
        } else {
            if (dtY > 0){
                rstAngel = Math.asin(sinVale);
            }else{
                rstAngel = Math.PI * 2 + Math.asin(sinVale);
            }
        }
        return Math.toDegrees(rstAngel);
    }
}
