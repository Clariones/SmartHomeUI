package bgby.skynet.org.smarthomeui.device;

/**
 * Created by Clariones on 6/16/2016.
 */
public interface ISimpleDimmerDevice extends ISimpleLightDevice {

    /**
     *
     * @param level: 0~100
     */
    void setLevel(int level);

    int getLevel();
}
