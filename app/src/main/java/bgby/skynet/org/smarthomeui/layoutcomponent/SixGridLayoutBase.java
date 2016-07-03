package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.Map;

import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.uicomponent.sixgridlayout.SixGridLayoutFragment;

public class SixGridLayoutBase extends LayoutComponentBaseImpl {

    public static final String TYPE = "sixgridLayout";
    public static final String PORTRAIT = "portrait";
    public static final String LANDSCALE = "landscape";
    private static final String TAG = "SixGridLayoutBase";
    private SixGridLayoutFragment fragment;

    @Override
    protected boolean validDevice(IDevice device) {
        return true;
    }

    @Override
    public String verifyParams() {
        return verifySelfDeviceConfig(getParams());
    }

    public String verifySelfDeviceConfig(Map<String, Object> params) {
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

        if (children == null || children.isEmpty()) {
            return "布局管理器内必须配置UI子组件";
        }

        int maxRows = 2;
        int maxCols = 3; // landscape
        if (strVal.equals(PORTRAIT)) {
            maxRows = 3;
            maxCols = 2;
        }

        for (ILayoutComponent child : children) {
            Map<String, Object> chdParams = child.getParams();
            String childDevId = (String) chdParams.get(PARAM_DEVICE_ID);
            String errMsg = "在6分格布局中的子元素" + childDevId + "必须配置布局参数 atRow 和 atCol";
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
            errMsg = chkInRange(1, maxRows, atRow, childDevId + "参数atRow错误：");
            if (errMsg != null) return errMsg;
            errMsg = chkInRange(1, maxCols, atRow, childDevId + "参数atCol错误：");
            if (errMsg != null) return errMsg;
            errMsg = chkInRange(1, maxRows, hasRows, childDevId + "参数hasRows错误：");
            if (errMsg != null) return errMsg;
            errMsg = chkInRange(1, maxCols, hasCols, childDevId + "参数hasCols错误：");
            if (errMsg != null) return errMsg;
            errMsg = chkInRange(1, maxRows, atRow+hasRows-1, childDevId + "行数超过边界：");
            if (errMsg != null) return errMsg;
            errMsg = chkInRange(1, maxCols, atCol+hasCols-1,childDevId + "列数超过边界：");
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
        SixGridLayoutFragment fgmt = SixGridLayoutFragment.newInstance(getComponentId());
        fragment = fgmt;
        return fragment;
    }
}
