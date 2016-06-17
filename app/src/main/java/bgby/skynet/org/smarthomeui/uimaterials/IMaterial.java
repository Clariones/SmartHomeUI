package bgby.skynet.org.smarthomeui.uimaterials;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Clariones on 6/2/2016.
 */
public interface IMaterial {
    void applyToBackgroup(View view);

    void applyToFont(TextView view);

    void applyToDrawableImage(ImageView imgView);
}
