package bgby.skynet.org.uicomponent.cmptpage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import bgby.skynet.org.smarthomeui.R;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.base.BaseUiComponent;
import bgby.skynet.org.smarthomeui.layoutcomponent.ILayoutComponent;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends BaseUiComponent {
    private static final String TAG = "PageFragment";
    protected View fragmentContent;

    public PageFragment() {
        // Required empty public constructor
    }

    public static PageFragment newInstance(String componetID) {
        PageFragment fragment = new PageFragment();
        fragment.handleComponentID(componetID);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("CREATE FRAGMENT", "Create " + this.getClass().getSimpleName());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cmpt_page, container, false);
        fragmentContent = view.findViewById(R.id.cmpt_fragment_content);
        Fragment childFragment = createChildFragements(view);
        getChildFragmentManager().beginTransaction().add(R.id.cmpt_fragment_content, childFragment).commit();
        Log.i("CREATE FRAGMENT", "Finish Create " + this.getClass().getSimpleName());
        return view;
    }

    private Fragment createChildFragements(View view) {
//        List<ILayout> children = ((ControlPage)layoutData).getChildren();
        List<ILayoutComponent> childrenLayout = layoutData.getChildren();
        if (childrenLayout == null || childrenLayout.size() != 1){
            ErrorPageFragment errFrgmt = new ErrorPageFragment();
            errFrgmt.setErrMessage("页面内应该指定一个布局，实际上指定了" + (childrenLayout==null?0:childrenLayout.size())+"个");
            return errFrgmt;
        }
        ILayoutComponent compt = childrenLayout.get(0);
        Log.d(TAG, "Create child " + compt.getComponentId());

        Fragment fragment = compt.getFragment();
        return fragment;
    }

    @Override
    public String getDisplayName() {
        String name = Controllers.getControllerManager().getDisplayName("_page_"+layoutData.getPosition());
        if (name == null){
            return "第"+layoutData.getDeviceID()+"页";
        }else{
            return name;
        }
    }



}
