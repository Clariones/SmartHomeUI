package bgby.skynet.org.uicomponent.simplelight;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bgby.skynet.org.smarthomeui.R;
import bgby.skynet.org.smarthomeui.uimaterials.MaterialsManager;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.base.BaseUiComponent;

/**
 * Created by Clariones on 6/14/2016.
 */
public class SimpleLightFragment extends BaseUiComponent {
    protected static final String MATERIAL_BACKGROUD = "simpleLight/summary/1x1/background/";
    protected static final String MATERIAL_ON_ICON = "simpleLight/summary/1x1/state/on";
    protected static final String MATERIAL_OFF_ICON = "simpleLight/summary/1x1/state/off";
    protected static final String MATERIAL_NAME = "simpleLight/summary/1x1/displayName";
    private View viewBackground;
    private TextView txtName;
    private ImageView imgModeIcon;

    public static SimpleLightFragment newInstance(String componetID) {
        SimpleLightFragment fragment = new SimpleLightFragment();
        fragment.handleComponentID(componetID);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CREATE FRAGMENT", "Create " + this.getClass().getSimpleName());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_uicmpt_simple_light, container, false);
        viewBackground = view.findViewById(R.id.cmpt_simplelight_background);
        txtName = (TextView) view.findViewById(R.id.cmpt_simplelight_name);
        imgModeIcon = (ImageView) view.findViewById(R.id.cmpt_simplelight_mode_icon);

        applyMaterials();
        updateValues();
        Log.d("CREATE FRAGMENT", "Finish Create " + this.getClass().getSimpleName());

        imgModeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLight();
            }
        });
        return view;
    }

    private void updateValues() {
        ISimpleLightDevice device = (ISimpleLightDevice) this.getLayoutData();
        boolean isOn = device.getState();
        MaterialsManager mmng = Controllers.getMaterialsManager();
        if (isOn){
            mmng.applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, imgModeIcon,MATERIAL_ON_ICON, MaterialsManager.MATERIAL_ID_SWITCH_ON);
        }else{
            mmng.applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, imgModeIcon,MATERIAL_OFF_ICON, MaterialsManager.MATERIAL_ID_SWITCH_OFF);
        }
    }

    private void applyMaterials() {
        MaterialsManager mmng = Controllers.getMaterialsManager();
        int direction = getActivity().getRequestedOrientation();
        String materialBkg = null;
        if (direction == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT || direction == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            materialBkg = MATERIAL_BACKGROUD+"portrait";
        }else{
            materialBkg = MATERIAL_BACKGROUD+"landscape";
        }
        mmng.applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, viewBackground,materialBkg,null);
        mmng.applyMaterial(MaterialsManager.APPLY_TO_FONT, viewBackground,MATERIAL_NAME,null);
    }

    private void toggleLight() {
        ISimpleLightDevice device = (ISimpleLightDevice) this.getLayoutData();
        device.toggleState();
        updateValues();
    }
}
