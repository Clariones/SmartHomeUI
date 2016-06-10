package bgby.skynet.org.smarthomeui.uicontroller;

public class Helper {
	public static final String MATERIAL_PACKAGE_FILENAME = "uidata.zip";

	private Helper(){}

	public static void setProgress(UIControllerStatus status, IInitialProgressCallback progressCallback) {
		progressCallback.onProgress(status.getPercentage(), status.getName(), status.getDescription());
	}

	public static void reportError(String errorDetailReport, IInitialProgressCallback progressCallback) {
		progressCallback.onErrorReport(errorDetailReport);
	}
}
