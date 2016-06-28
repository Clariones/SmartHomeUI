package bgby.skynet.org.smarthomeui.device;

/**
 * Created by Clariones on 6/28/2016.
 */
public class Hgw2000HBusLight  extends Hgw2000SwitchLight{
    public Hgw2000HBusLight() {
        super();
        supportedProfiles.clear();
        supportProfile("Honeywell HBus Light");
    }

    protected int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        if (level > 0){
            state = true;
        }else{
            state = false;
        }
    }

    @Override
    public boolean getState() {
        return state;
    }

    @Override
    public void setState(boolean state) {
       this.state = state;
        if (state && level <= 0){
            level = 100;
        }else if (!state && level >= 100){
            level = 0;
        }
    }

    @Override
    public void toggleState() {
        setState(!getState());
    }
}
