package bgby.skynet.org.uicomponent.simplelight;

import bgby.skynet.org.uicomponent.base.ILayoutComponent;

/**
 * Created by Clariones on 6/16/2016.
 */
public interface ISimpleLightDevice extends ILayoutComponent {

    boolean getState();

    void setState(boolean newState, IResponseListener listener);

    void toggleState(IResponseListener listener);
}
