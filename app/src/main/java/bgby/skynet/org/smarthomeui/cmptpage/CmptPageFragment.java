package bgby.skynet.org.smarthomeui.cmptpage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.skynet.bgby.layout.ILayout;

import java.util.List;

import bgby.skynet.org.smarthomeui.R;
import bgby.skynet.org.smarthomeui.layoutcomponent.ControlPage;
import bgby.skynet.org.uicomponent.base.BaseUiComponent;
import bgby.skynet.org.uicomponent.base.ILayoutComponent;

/**
 * A simple {@link Fragment} subclass.
 */
public class CmptPageFragment extends BaseUiComponent {
    protected View fragmentContent;

    public CmptPageFragment() {
        // Required empty public constructor
    }

    public static CmptPageFragment newInstance(String componetID) {
        CmptPageFragment fragment = new CmptPageFragment();
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
        List<ILayout> children = ((ControlPage)layoutData).getLayoutContent();
        if (children == null || children.size() != 1){
            ErrorPageFragment errFrgmt = new ErrorPageFragment();
            errFrgmt.setErrMessage("页面内应该指定一个布局，实际上指定了" + (children==null?0:children.size())+"个");
            return errFrgmt;
        }
        ILayoutComponent compt = (ILayoutComponent) children.get(0);
        Log.d("CREATE CHILD", String.valueOf(compt));

        Fragment fragment = compt.getFragment();
        return fragment;
    }

}
