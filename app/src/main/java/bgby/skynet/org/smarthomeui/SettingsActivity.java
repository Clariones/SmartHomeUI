package bgby.skynet.org.smarthomeui;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

import bgby.skynet.org.smarthomeui.utils.Controllers;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Controllers.setScreenDirection(this);
        setContentView(R.layout.activity_settings);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragmentSettings, new SettingsFragment())
                .commit();

    }
}
