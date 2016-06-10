package bgby.skynet.org.uicomponent.base;

/**
 * Created by Clariones on 6/3/2016.
 */
public interface IUiComponent {
    void setLayoutData(ILayoutComponent layoutData);

    ILayoutComponent getLayoutData();

    String getDisplayName();
}
