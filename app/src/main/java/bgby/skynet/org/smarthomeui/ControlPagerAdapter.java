package bgby.skynet.org.smarthomeui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.smarthomeui.layoutcomponent.ILayoutComponent;
import bgby.skynet.org.uicomponent.cmptpage.ErrorPageFragment;

/**
 * Created by Clariones on 6/2/2016.
 */
public class ControlPagerAdapter extends FragmentStatePagerAdapter {
    private List<ILayoutComponent> pages;
    private List<android.app.Fragment> pageUIs;

    public ControlPagerAdapter(FragmentManager fm) {
        super(fm);
        pages = Controllers.getControllerManager().getLayoutComponentManager().getRootComponents();
    }

    @Override
    public Fragment getItem(int position) {
        if (pages == null || pages.isEmpty()){
            ErrorPageFragment errorPageFragment = new ErrorPageFragment();
            errorPageFragment.setErrMessage("未获得正确的页面布局");
            return errorPageFragment;
        }
        if (position >= pages.size()){
            ErrorPageFragment errorPageFragment = new ErrorPageFragment();
            errorPageFragment.setErrMessage("页面位置"+(1+position)+"超出页面数量"+pages.size());
            return errorPageFragment;
        }

        ILayoutComponent pageCmpt = (ILayoutComponent) pages.get(position);
        Fragment fragment = pageCmpt.getFragment();
        return fragment;
    }


    @Override
    public int getCount() {
        return pages.size();
    }
}
