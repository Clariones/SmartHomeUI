package bgby.skynet.org.uicomponent.simplelight;

import java.util.Map;

/**
 * Created by Clariones on 6/22/2016.
 */
public interface IResponseListener {
    void onResponse(int errorCode, String errTitle, Map<String, Object> responseData);
}
