package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;
import android.util.Log;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.layout.ILayout;

import java.util.List;
import java.util.Map;

import bgby.skynet.org.smarthomeui.cmptlayout.SixGridLayoutFragment;

public class SixGridLayout extends BaseLayoutGroupComponent {

    public static final String TYPE = "sixgridLayout";
    public static final String PORTRAIT = "portrait";
    public static final String LANDSCALE = "landscape";
    private static final String TAG = "SixGridLayout";
    private SixGridLayoutFragment fragment;

    public String getType() {
        return TYPE;
    }

    @Override
    protected void preInitChildLayoutData(ILayout child, ILayout data) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void postInitChildLayoutData(ILayout child, ILayout data) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initByParameters(Map<String, Object> params) {
        // TODO Auto-generated method stub

    }

    @Override
    public String verifySelfDeviceConfig(Map<String, DeviceProfile> profiles, Map<String, String> deviceProfileNames) {
        if (getParams() == null) {
            return "6分格布局必须指定布局方向参数";
        }
        Object direction = getParams().get("layoutDirection");
        if (!(direction instanceof String)) {
            return "6分格布局布局方向参数layoutDirection必须是" + LANDSCALE + "/" + PORTRAIT;
        }
        String strVal = (String) direction;
        if (!strVal.equals(LANDSCALE) && !strVal.equals(PORTRAIT)) {
            return "6分格布局布局方向参数layoutDirection必须是" + LANDSCALE + "/" + PORTRAIT;
        }

        List<ILayout> children = getLayoutContent();
        if (children == null || children.isEmpty()) {
            return "布局管理器内必须配置UI子组件";
        }

        int maxRows = 2;
        int maxCols = 3; // landscape
        if (strVal.equals(PORTRAIT)) {
            maxRows = 3;
            maxCols = 2;
        }

        for (ILayout child : children) {
            Map<String, Object> chdParams = child.getParams();
            String errMsg = "在6分格布局中的子元素" + child.getType() + "必须配置布局参数 atRow 和 atCol";
            if (chdParams == null) {
                return errMsg;
            }
            Integer atRow = getIntParam(chdParams, "atRow", null);
            Integer atCol = getIntParam(chdParams, "atCol", null);
            if (atRow == null || atCol == null) {
                return errMsg;
            }
            Integer hasRows = getIntParam(chdParams, "hasRows", 1);
            Integer hasCols = getIntParam(chdParams, "hasCols", 1);
            errMsg = chkInRange(1, maxRows, atRow, child.getType() + "参数atRow错误：");
            if (errMsg != null) return errMsg;
            errMsg = chkInRange(1, maxCols, atRow, child.getType() + "参数atCol错误：");
            if (errMsg != null) return errMsg;
            errMsg = chkInRange(1, maxRows, hasRows, child.getType() + "参数hasRows错误：");
            if (errMsg != null) return errMsg;
            errMsg = chkInRange(1, maxCols, hasCols, child.getType() + "参数hasCols错误：");
            if (errMsg != null) return errMsg;
            errMsg = chkInRange(1, maxRows, atRow+hasRows-1, child.getType() + "行数超过边界：");
            if (errMsg != null) return errMsg;
            errMsg = chkInRange(1, maxCols, atCol+hasCols-1, child.getType() + "列数超过边界：");
            if (errMsg != null) return errMsg;

        }
        return null;
    }
    private String chkInRange(int min, int max, int val, String errMsg) {
        if (val >= min && val <= max) {
            return null;
        }
        return "6分格布局管理器子元素"+errMsg+val+" for ["+min+","+max+"]";
    }

    private Integer getIntParam(Map<String, Object> inParams, String key, Integer defaultVal) {
        Object val = inParams.get(key);
        if (val == null){
            return defaultVal;
        }
        if (!(val instanceof Double)){
            Log.i(TAG, key + " type is " + val.getClass());
            return null;
        }
        return ((Double) val).intValue();
    }

    @Override
    public Fragment getFragment() {
        SixGridLayoutFragment fgmt = SixGridLayoutFragment.newInstance(this.getComponentRuntimeID());
        fragment = fgmt;
        return fragment;
    }
}
