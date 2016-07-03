package bgby.skynet.org.smarthomeui.uicontroller;

public interface IInitialProgressCallback {

	void onProgress(double percentage, String title, String description);

	void onErrorReport(String errorDetailReport);

	void onStartingFinished(boolean success);

}
