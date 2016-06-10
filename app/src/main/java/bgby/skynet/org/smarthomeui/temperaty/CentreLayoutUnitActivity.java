package bgby.skynet.org.smarthomeui.temperaty;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import bgby.skynet.org.customviews.arcprogressbar.ArcProgressBar;
import bgby.skynet.org.customviews.roundseekbar.OnProgressChangedListener;
import bgby.skynet.org.customviews.roundseekbar.RoundSeekBar;
import bgby.skynet.org.smarthomeui.R;

public class CentreLayoutUnitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centre_layout_unit);

        final ArcProgressBar progressBar = (ArcProgressBar) findViewById(R.id.progressBar);
        final RoundSeekBar seekBar = (RoundSeekBar) findViewById(R.id.seekBar);
        final TextView txtTemp = (TextView) findViewById(R.id.txtTemp);
        seekBar.setOnProgressChangedListener(new OnProgressChangedListener() {
            @Override
            public void onProgressChanged(RoundSeekBar roundSeekBar, float progress) {
                progressBar.setProgress(progress);
                txtTemp.setText(String.format("%3.1f", progress));
            }
        });
    }
}
