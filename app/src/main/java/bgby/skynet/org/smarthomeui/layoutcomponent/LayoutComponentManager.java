package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.skynet.bgby.layout.LayoutData;
import org.skynet.bgby.layout.LayoutException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Clariones on 6/28/2016.
 */
public class LayoutComponentManager {
    protected static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    protected static final String TAG = "LayoutComponentManager";
    protected static AtomicLong compIdSeed = new AtomicLong(1);
    protected static Map<String, Class<? extends ILayoutComponent>> registedTypes = new HashMap<>();
    protected List<ILayoutComponent> rootComponents;
    protected Map<String, ILayoutComponent> allComponents;

    public Map<String, ILayoutComponent> getAllComponents() {
        return allComponents;
    }

    public void registerLayoutComponentType(String type, Class<? extends ILayoutComponent> clazz) throws LayoutException {
        Class<? extends ILayoutComponent> oldData = registedTypes.put(type, clazz);
        if (oldData != null) {
            throw new LayoutException("Layout type " + type + " was double-registered. Must fix!");
        }
    }

    public List<ILayoutComponent> getRootComponents() {
        return rootComponents;
    }

    public Map<String, ILayoutComponent> createComponentsFromLayoutData(LayoutData[] datas) throws LayoutException {
        rootComponents = new ArrayList<>(datas.length);
        Map<String, ILayoutComponent> result = new HashMap<>();
        if (datas == null || datas.length == 0) {
            Log.w(TAG, "layout data is empty. No any layout component was created!");
            return result;
        }

        for (int i = 0; i < datas.length; i++) {
            LayoutData data = datas[i];
            ILayoutComponent cmpt = createOneLayoutComponent(data, result);
            cmpt.setPosition(i + 1);
            rootComponents.add(cmpt);
        }
        allComponents = result;
        return result;
    }

    protected ILayoutComponent createOneLayoutComponent(LayoutData data, Map<String, ILayoutComponent> result) throws LayoutException {
        String type = data.getType();
        ILayoutComponent component = createComponentByType(type);
        String componentID = getUniquedComponentId(type);
        component.setComponentId(componentID);
        component.setParams(data.getParams());
        result.put(componentID, component);
        List<LayoutData> children = data.getLayoutContent();
        if (children != null && children.size() > 0) {
            List<ILayoutComponent> childrenCmpts = new ArrayList<>(children.size());
            for (int i = 0; i < children.size(); i++) {
                LayoutData childData = children.get(i);
                ILayoutComponent child = createOneLayoutComponent(childData, result);
                child.setPosition(i + 1);
                childrenCmpts.add(child);
            }
            component.setChildren(childrenCmpts);
        }
        return component;
    }

    protected String getUniquedComponentId(String type) {
        long id = compIdSeed.getAndIncrement();
        return String.format("%s_%03d", type, id);
    }

    protected ILayoutComponent createComponentByType(String type) throws LayoutException {
        Class<? extends ILayoutComponent> clazz = registedTypes.get(type);
        if (clazz == null) {
            try {
                Class tryClazz = Class.forName(type);
                if (ILayoutComponent.class.isAssignableFrom(tryClazz)) {
                    clazz = tryClazz;
                } else {
                    throw new LayoutException("Type " + type + " is not an ILayoutComponent sub-class name");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new LayoutException("Type " + type + " is not registered and is not an valid class name itself neither.");
            }
        }
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new LayoutException("Cannot create instance of class " + clazz, e);
        }
    }

    public void initSupportedLayoutComponents() throws LayoutException {
        registerLayoutComponentType(ControlPage.TYPE, ControlPage.class);
        registerLayoutComponentType(NormalHvacComponent.TYPE, NormalHvacComponent.class);
        registerLayoutComponentType(SimpleLightComponent.TYPE, SimpleLightComponent.class);
        registerLayoutComponentType(SixGridLayoutBase.TYPE, SixGridLayoutBase.class);
    }
}
