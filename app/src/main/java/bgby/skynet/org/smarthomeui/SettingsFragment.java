package bgby.skynet.org.smarthomeui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

import bgby.skynet.org.smarthomeui.preference.RotateScreenPreference;
import bgby.skynet.org.smarthomeui.utils.Controllers;


/**
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
//    protected static final String KEY_MULTICAST_ADDRESS = "pref_key_multicast_address";
    protected static final String KEY_ROTATE_SCREEN = "pref_key_rotate_screen";
    protected static final String KEY_MULTICAST_PORT = "pref_key_multicast_port";
    protected static final String KEY_PROXY_ADDRESS = "pref_key_driverproxy_address";
    protected static final String KEY_PROXY_PORT = "pref_key_driverproxy_port";
    protected static final String KEY_MINE_ID = "pref_key_mine_id";
    protected static final String KEY_MATERIAL_FOLDER = "pref_key_material_folder";
    protected static final String[] MINE_KEYS = {
//            KEY_MULTICAST_ADDRESS,
            KEY_MULTICAST_PORT,
            KEY_PROXY_ADDRESS,
            KEY_PROXY_PORT,
            KEY_MINE_ID,
            KEY_MATERIAL_FOLDER,
    };
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        initSummaries();
    }

    private void initSummaries() {
        for(String key : MINE_KEYS){
            EditTextPreference pref = (EditTextPreference) findPreference(key);
            String value = pref.getText();
            pref.setSummary(value);
        }
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_ROTATE_SCREEN) || key.equals(Controllers.PREFERENCE_KEY_DIRECTION)){
            RotateScreenPreference pref = (RotateScreenPreference) findPreference(key);
//            pref.setSummary("AHA");
        }else{
            Log.i("OK", "onSharedPreferenceChanged: key="+key+", value="+sharedPreferences.getString(key, null));
            EditTextPreference pref = (EditTextPreference) findPreference(key);
            pref.setSummary(sharedPreferences.getString(key, null));
        }
    }
}
