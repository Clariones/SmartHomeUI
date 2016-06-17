package bgby.skynet.org.smarthomeui.uimaterials;

import android.view.View;
import android.widget.ImageView;

/**
 * Created by Clariones on 6/1/2016.
 */
public class ColorMaterial extends BaseMaterial{
    protected int color;
    public int getColor(){
        return color;
    }

    public ColorMaterial(int initColor){
        this.color = initColor;
    }

    public String toString(){
        return String.valueOf(color);
    }

    @Override
    public void applyToBackgroup(View view) {
        view.setBackgroundColor(getColor());
    }

    @Override
    public void applyToDrawableImage(ImageView view) {
        view.setBackgroundColor(getColor());
    }
}
