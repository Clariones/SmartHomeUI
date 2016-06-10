package bgby.skynet.org.smarthomeui.temperaty;

import android.app.Activity;
import android.os.Bundle;

import bgby.skynet.org.customviews.roundseekbar.OnProgressChangedListener;
import bgby.skynet.org.customviews.roundseekbar.RoundSeekBar;
import bgby.skynet.org.smarthomeui.R;

public class RoundSeekBarUnitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_seek_bar_unit);

        RoundSeekBar bar = (RoundSeekBar) findViewById(R.id.seekBar);
        bar.setOnProgressChangedListener(new OnProgressChangedListener() {
            @Override
            public void onProgressChanged(RoundSeekBar roundSeekBar, float progress) {

            }
        });
    }
}
