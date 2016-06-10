package bgby.skynet.org.customviews.circlecentrelayout;

import android.graphics.Point;

/**
 * Created by Clariones on 6/6/2016.
 */
public interface ICentredView {
    Point getCircleCentrePoint();
    void setMaxDiameter(float diameter);
    float getMaxDiameter();
    float getCavityRatio();
    void setCavityRatio(float cavityRatio);
    int getCavityDiameter();
}
