package bgby.skynet.org.customviews.arcprogressbar;

import org.junit.Test;

/**
 * Created by Clariones on 6/6/2016.
 */
public class UtilsTest {

    @Test
    public void testCalculateDrawArea() throws Exception {
        int[] degree = new int[]{60, 90, 120, 180, 240, 270, 360, 380};
        for (int i = 0; i < degree.length; i++) {
            Utils.DrawAreaData data = Utils.calculateDrawArea(400.0, degree[i]);
            System.out.printf("%d:\t%s\n",degree[i], data);
            Utils.DrawAreaData d2 = new Utils.DrawAreaData(data, 0.5);
            System.out.printf("%d:\t%s\n",degree[i], d2);
        }
    }
}