package bgby.skynet.org.smarthomeui.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import bgby.skynet.org.smarthomeui.uicontroller.UIControllerManager;
import bgby.skynet.org.smarthomeui.uimaterials.MaterialsManager;
import bgby.skynet.org.smarthomeui.layoutcomponent.ILayoutComponent;
import bgby.skynet.org.uicomponent.base.IUiComponent;

/**
 * Created by Clariones on 6/2/2016.
 */
public class Controllers {
    public static final String PREFERENCE_KEY_DIRECTION = "pref_key_screen_direction";
    public static final String DISPLAY_NAME_PAGE = "_page_";
    private static final String TAG = "Controllers";
    protected static Map<String, Set<IUiComponent>> deviceUiComponents = new HashMap<>();
    private static AtomicLong gComponentID = new AtomicLong(1);
    private static Map<String, ILayoutComponent> components = new HashMap<>();
    private static Map<String, ILayoutComponent> componentsById = new HashMap<>();
    private static UIControllerManager controllerManager;
    private static MaterialsManager materialsManager;
    protected static Activity curActivity;

    private Controllers() {
    }

    protected static String getUniquedComponentID(String prefix) {
        return prefix + String.format("_%03d", gComponentID.incrementAndGet());
    }

    public static ILayoutComponent getComponentByRuntimeID(String id) {
        return components.get(id);
    }

    public static ILayoutComponent getComponentByDeviceID(String id) {
        return componentsById.get(id);
    }

    public static void registerUIComponent(ILayoutComponent layoutComponent, IUiComponent uiComponent) {
        String devID = layoutComponent.getDeviceID();
        Set<IUiComponent> uiCmps = deviceUiComponents.get(devID);
        if (uiCmps == null) {
            uiCmps = new HashSet<>();
            deviceUiComponents.put(devID, uiCmps);
        }
        uiCmps.add(uiComponent);
        Log.i(TAG, "register UI component " + uiComponent + " to " + devID);
    }

    public static void unRegisterUIComponent(ILayoutComponent layoutComponent, IUiComponent uiComponent) {
        String devID = layoutComponent.getDeviceID();
        Set<IUiComponent> uiCmps = deviceUiComponents.get(devID);
        if (uiCmps == null) {
            return;
        }
        uiCmps.remove(uiComponent);
        Log.i(TAG, "remove UI component " + uiComponent + " from " + devID);
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

    public static void setScreenDirection(Activity activity) {
        curActivity = activity;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        int direction = prefs.getInt(PREFERENCE_KEY_DIRECTION, -1);
        if (direction == -1) {
            prefs.edit().putInt(PREFERENCE_KEY_DIRECTION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE).apply();
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            return;
        }


        int curRequestDirection = activity.getRequestedOrientation();
        if (direction != curRequestDirection) {
            switch (direction) {
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                default:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
            }
        }
    }


    public static void showError(final String title, final String description) {
        Log.e(TAG, title +" : " + description);
        if (curActivity == null){
            return;
        }

        curActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast t = Toast.makeText(curActivity, "错误：" + title + "\r\n" + description, Toast.LENGTH_SHORT);
                t.show();
            }
        });
    }
}
