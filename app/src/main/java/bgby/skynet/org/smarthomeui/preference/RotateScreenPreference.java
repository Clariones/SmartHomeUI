package bgby.skynet.org.smarthomeui.preference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import bgby.skynet.org.smarthomeui.R;
import bgby.skynet.org.smarthomeui.utils.Controllers;

/**
 * Created by Clariones on 6/30/2016.
 */
public class RotateScreenPreference extends EditTextPreference {
    private static final String TAG = "RotateScreenPreference";
    protected Map<Integer, String> directionNames;

    public RotateScreenPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialWork(context);
    }

    public RotateScreenPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialWork(context);
    }

    public RotateScreenPreference(Context context) {
        super(context);
        initialWork(context);
    }

    protected void initialWork(Context context){
        directionNames = new HashMap<>();
        directionNames.put(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, context.getResources().getString(R.string.direction_landscape));
        directionNames.put(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE, context.getResources().getString(R.string.direction_landscape_reverse));
        directionNames.put(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, context.getResources().getString(R.string.direction_portait));
        directionNames.put(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT, context.getResources().getString(R.string.direction_portait_reverse));
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        if (getContext() instanceof  Activity) {
            Activity activity = (Activity) getContext();
            int curDirection = activity.getRequestedOrientation();
            String dirName = getDirectionDisplayName(curDirection);
            setSummary(dirName);
        }
        return view;
    }

    private String getDirectionDisplayName(int curDirection) {
        String name = directionNames.get(curDirection);
        if (name == null){
            return "";
        }
        return name;
    }

    @Override
    protected void onClick() {
        Activity activity = (Activity) getContext();
        int curDirection = activity.getRequestedOrientation();
        switch (curDirection){
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                saveDirection(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, activity);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                saveDirection(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT, activity);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                saveDirection(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE, activity);
                break;
            default:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                saveDirection(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activity);
        }
    }

    private void saveDirection(int direction, Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        prefs.edit().putInt(Controllers.PREFERENCE_KEY_DIRECTION, direction).apply();
    }

}
