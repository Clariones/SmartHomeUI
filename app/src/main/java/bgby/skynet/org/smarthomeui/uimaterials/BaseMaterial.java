package bgby.skynet.org.smarthomeui.uimaterials;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Clariones on 6/2/2016.
 */
public class BaseMaterial implements IMaterial {
    @Override
    public void applyToBackgroup(View view) {
        // by default, nothing to do
    }

    @Override
    public void applyToFont(TextView view) {
        // by default, nothing to do
    }

    @Override
    public void applyToImageDrawble(ImageView view) {
        // by default, do nothing
    }
}
