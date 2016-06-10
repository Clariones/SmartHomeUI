package bgby.skynet.org.customviews.roundseekbar;

import org.junit.Test;

/**
 * Created by Clariones on 6/8/2016.
 */
public class UtilsTest {

    @Test
    public void testCalcTouchDegree() throws Exception {
        float x0 = 100.0f;
        float y0 = 100.0f;
        float x1 = 0.0f;
        float y1 = 273.20f;

        double rstAngle = Utils.calcTouchDegree(x0,y0, x1, y1);
        System.out.printf("%3.1f,%3.1f --> %3.1f,%3.1f is %3.1f degress\n", x1,y1, x0, y0, rstAngle);
    }
}