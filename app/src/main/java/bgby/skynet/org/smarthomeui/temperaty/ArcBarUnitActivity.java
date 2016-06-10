package bgby.skynet.org.smarthomeui.temperaty;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import bgby.skynet.org.customviews.arcprogressbar.ArcProgressBar;
import bgby.skynet.org.smarthomeui.R;

public class ArcBarUnitActivity extends Activity {

    private ArcProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arc_bar_unit);

        bar = (ArcProgressBar) findViewById(R.id.progressBar);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setProgress( bar.getProgress()-1);
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setProgress( bar.getProgress()+1);
            }
        });
    }
}
