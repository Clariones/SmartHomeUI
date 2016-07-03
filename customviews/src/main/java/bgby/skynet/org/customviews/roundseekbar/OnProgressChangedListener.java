package bgby.skynet.org.customviews.roundseekbar;

/**
 * Created by Clariones on 6/8/2016.
 */
public interface OnProgressChangedListener {
    void onProgressChanged(RoundSeekBar roundSeekBar, float progress);

    void onStartTrackingTouch(RoundSeekBar roundSeekBar);

    void onStopTrackingTouch(RoundSeekBar roundSeekBar);
}
