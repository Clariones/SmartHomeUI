package bgby.skynet.org.smarthomeui.uicontroller;

import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.restserver.IRestClientContext;

/**
 * Created by Clariones on 6/22/2016.
 */
public interface IRestCommandListener {
    void handleResponse(IRestRequest request, IRestClientContext restClientContext, IHttpResponse httpResponse);
}
