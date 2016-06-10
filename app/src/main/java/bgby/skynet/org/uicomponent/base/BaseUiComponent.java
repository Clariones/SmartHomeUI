package bgby.skynet.org.uicomponent.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bgby.skynet.org.smarthomeui.uimaterials.IMaterial;
import bgby.skynet.org.smarthomeui.utils.Controllers;

/**
 * Created by Clariones on 6/3/2016.
 */
public class BaseUiComponent extends Fragment implements  IUiComponent{
    protected static final String ARG_PAGE_POSITION = "arg_page_position";
    private static final String TAG = "BaseUiComponent";
    protected ILayoutComponent layoutData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String componetID = getArguments().getString(ARG_PAGE_POSITION);
        setLayoutData( Controllers.getComponentByRuntimeID(componetID));
    }

    @Override
    public void setLayoutData(ILayoutComponent layoutData) {
        this.layoutData = layoutData;
    }

    @Override
    public ILayoutComponent getLayoutData() {
        return layoutData;
    }

    protected void handleComponentID(String componetID) {
        Bundle args = new Bundle();
        args.putString(ARG_PAGE_POSITION, componetID);
        setArguments(args);
    }

    @Override
    public String getDisplayName(){
        return layoutData.getDisplayName();
    }

    protected void applyImageDrable(ImageView imgView, String meaterialName) {
        IMaterial drawable = Controllers.getMaterialsManager().getMaterial(meaterialName);
        if (drawable == null){
            Log.d(TAG, "apply image for " + meaterialName + " not found");
            return;
        }
        Log.d(TAG, "apply image for " + meaterialName + " not found");
        drawable.applyToImageDrawble(imgView);
    }

    protected void applyBackground(View view, String meaterialName) {
        IMaterial drawable = Controllers.getMaterialsManager().getMaterial(meaterialName);
        if (drawable == null){
            Log.d(TAG, "apply background for " + meaterialName + " not found");
            return;
        }
        drawable.applyToBackgroup(view);
        Log.d(TAG, "apply background for " + meaterialName);
    }

    protected void applyFont(TextView textView, String meaterialName) {

        IMaterial drawable = Controllers.getMaterialsManager().getMaterial(meaterialName);
        if (drawable == null){
            Log.d(TAG, "apply font for " + meaterialName + " not found");
            return;
        }
        Log.d(TAG, "apply font for " + meaterialName );
        drawable.applyToFont(textView);
    }
}
