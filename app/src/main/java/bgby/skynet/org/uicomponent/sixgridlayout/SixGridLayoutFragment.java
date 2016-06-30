package bgby.skynet.org.uicomponent.sixgridlayout;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.Map;

import bgby.skynet.org.customviews.averagedgridlayout.AveragedGridLayout;
import bgby.skynet.org.smarthomeui.layoutcomponent.SixGridLayoutBase;
import bgby.skynet.org.uicomponent.base.BaseUiComponent;
import bgby.skynet.org.smarthomeui.layoutcomponent.ILayoutComponent;

/**
 * A simple {@link Fragment} subclass.
 */
public class SixGridLayoutFragment extends BaseUiComponent {


    private static final int NO_RATATE = 0;
    private static final int LANDSCAPE_RATATE = 1;
    private static final int PORTRAIT_ROTATE = 2;

    public SixGridLayoutFragment() {
        // Required empty public constructor
    }

    public static SixGridLayoutFragment newInstance(String componetID) {
        SixGridLayoutFragment fragment = new SixGridLayoutFragment();
        fragment.handleComponentID(componetID);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CREATE FRAGMENT", "Create " + this.getClass().getSimpleName());
        Context ctx = container.getContext();
        AveragedGridLayout view = new AveragedGridLayout(ctx);
        int curDirection = getActivity().getRequestedOrientation();
        if (curDirection == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || curDirection == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
            view.setCols(3);
            view.setRows(2);
        }else{
            view.setCols(2);
            view.setRows(3);
        }

        // TODO add children later
        createChildrenFragements(view, ctx);
        Log.d("CREATE FRAGMENT", "Finish Create " + this.getClass().getSimpleName());
        return view;
    }

    private void createChildrenFragements(AveragedGridLayout view, Context ctx) {
        List<ILayoutComponent> children = layoutData.getChildren();
        if (children == null || children.isEmpty()){
            return;
        }
        FragmentTransaction tr = getChildFragmentManager().beginTransaction();
        int rotateType = getRotateType();
        for(int i=0;i<children.size();i++){
            ILayoutComponent child = (ILayoutComponent) children.get(i);
            Map<String, Object> layoutParams = child.getParams();
            int atRow = getIntParam(layoutParams, "atRow", 0);
            int atCol = getIntParam(layoutParams, "atCol", 0);
            int hasCol = getIntParam(layoutParams, "hasCols", 1);
            int hasRow = getIntParam(layoutParams, "hasRows", 1);
            int childAtRow = atRow;
            int childAtCol = atCol;
            int childHasCol = hasCol;
            int childHasRow = hasRow;
            switch (rotateType){
                case LANDSCAPE_RATATE:
                case PORTRAIT_ROTATE:
                    // RA-(C+CS-1)+1
                    childAtCol = view.getCols() - (atRow+hasRow-1) +1 ;
                    childAtRow = atCol;
                    childHasCol = hasRow;
                    childHasRow = hasCol;
                    break;
            }
            Fragment fragment = child.getFragment();
            RelativeLayout wrapper = new RelativeLayout(ctx);
            wrapper.setId(100+i);
            AveragedGridLayout.LayoutParams lp = new AveragedGridLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setAtCol(childAtCol);
            lp.setAtRow(childAtRow);
            lp.setHasCols(childHasCol);
            lp.setHasRows(childHasRow);
            view.addView(wrapper,lp);
            tr.add(100+i, fragment);
        }
        tr.commit();
    }

    private int getIntParam(Map<String, Object> layoutParams, String atRow, int i) {
        Double val = (Double) layoutParams.get(atRow);
        if (val == null){
            return i;
        }
        return val.intValue();
    }

    private int getRotateType() {
        SixGridLayoutBase layout = (SixGridLayoutBase) getLayoutData();
        int curDirection = getActivity().getRequestedOrientation();
        String setDirectionStr = (String) layout.getParams().get("layoutDirection");
        if (setDirectionStr.equals("landscape")){
            if (curDirection == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || curDirection == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                return NO_RATATE;
            }
            return LANDSCAPE_RATATE;
        }else{
            if (curDirection == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || curDirection == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                return PORTRAIT_ROTATE;
            }
            return NO_RATATE;
        }

    }

}
