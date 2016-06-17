package bgby.skynet.org.smarthomeui.uimaterials;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Clariones on 6/2/2016.
 */
public class DrawableMaterail extends BaseMaterial {
    private final BitmapDrawable drawable;
    protected Bitmap bitmap;
    protected Context initialContext;

    public BitmapDrawable getDrawable() {
        return drawable;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Context getInitialContext() {
        return initialContext;
    }

    public void setInitialContext(Context initialContext) {
        this.initialContext = initialContext;
    }

    public DrawableMaterail(Context context, Bitmap bitmap) {
        super();
        this.initialContext = context;
        this.bitmap = bitmap;
        this.drawable = new BitmapDrawable(context.getResources(), bitmap);
//        bitmap.recycle();
    }

    @Override
    public void applyToBackgroup(View view) {
        view.setBackground(this.getDrawable());
    }

    @Override
    public void applyToDrawableImage(ImageView view) {
        view.setImageDrawable(getDrawable());
    }
}
