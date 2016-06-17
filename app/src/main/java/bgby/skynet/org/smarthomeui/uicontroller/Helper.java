package bgby.skynet.org.smarthomeui.uicontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class Helper {
	public static final String MATERIAL_PACKAGE_FILENAME = "uidata.zip";
	public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private Helper(){}

	public static void setProgress(UIControllerStatus status, IInitialProgressCallback progressCallback) {
		progressCallback.onProgress(status.getPercentage(), status.getName(), status.getDescription());
	}

	public static void reportError(String errorDetailReport, IInitialProgressCallback progressCallback) {
		progressCallback.onErrorReport(errorDetailReport);
	}

	public static class RestResponseData{

		protected String result;
		protected int errorCode;
		protected String request;
		public String getRequest() {
			return request;
		}
		public void setRequest(String request) {
			this.request = request;
		}

		protected JsonElement data;
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
		public int getErrorCode() {
			return errorCode;
		}
		public void setErrorCode(int errorCode) {
			this.errorCode = errorCode;
		}
		public JsonElement getData() {
			return data;
		}
		public void setData(JsonElement data) {
			this.data = data;
		}
	}
}
