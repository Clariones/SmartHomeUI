package bgby.skynet.org.smarthomeui.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import bgby.skynet.org.smarthomeui.uicontroller.UIControllerManager;
import bgby.skynet.org.smarthomeui.uimaterials.MaterialsManager;
import bgby.skynet.org.uicomponent.base.ILayoutComponent;

/**
 * Created by Clariones on 6/2/2016.
 */
public class Controllers {
    public static final String PREFERENCE_KEY_DIRECTION = "pref_key_screen_direction";
    private Controllers(){}
    private static AtomicLong gComponentID = new AtomicLong(1);
    private static Map<String, ILayoutComponent> components = new HashMap<>();
    private static UIControllerManager controllerManager;
    private static MaterialsManager materialsManager;

    protected static String getUniquedComponentID(String prefix){
        return prefix + String.format("_%03d",gComponentID.incrementAndGet());
    }
    public static ILayoutComponent getComponentByRuntimeID(String id){
        return components.get(id);
    }
    public static UIControllerManager getControllerManager() {
        return controllerManager;
    }

    public static void setControllerManager(UIControllerManager controllerManager) {
        Controllers.controllerManager = controllerManager;
    }

    public static MaterialsManager getMaterialsManager() {
        return materialsManager;
    }

    public static void setMaterialsManager(MaterialsManager materialsManager) {
        Controllers.materialsManager = materialsManager;
    }

    public static String regsiterComponent(ILayoutComponent component) {
        String id = getUniquedComponentID(component.getType());
        components.put(id, component);
        return id;
    }

    public static void setScreenDirection(Activity page) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(page);
        int direction = prefs.getInt(PREFERENCE_KEY_DIRECTION, -1);
        if (direction == -1){
            prefs.edit().putInt(PREFERENCE_KEY_DIRECTION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE).apply();
            page.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            return;
        }


        int curRequestDirection = page.getRequestedOrientation();
        if (direction != curRequestDirection){
            switch (direction){
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    page.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    page.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    page.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                default:
                    page.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
            }
        }
    }
}
