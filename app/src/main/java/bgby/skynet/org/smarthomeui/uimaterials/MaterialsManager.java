package bgby.skynet.org.smarthomeui.uimaterials;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Clariones on 6/1/2016.
 */
public class MaterialsManager {
    public static final String MATERIAL_ID_DEF0 = "app/default/system/0";
    public static final String MATERIAL_ID_DEF1 = "app/default/system/1";
    public static final String MATERIAL_ID_DEF2 = "app/default/system/2";
    public static final String MATERIAL_ID_DEF3 = "app/default/system/3";
    public static final String MATERIAL_ID_DEF4 = "app/default/system/4";
    public static final String MATERIAL_ID_DEF5 = "app/default/system/5";
    public static final String MATERIAL_ID_DEF6 = "app/default/system/6";
    public static final String MATERIAL_ID_DEF7 = "app/default/system/7";
    public static final String MATERIAL_ID_DEF8 = "app/default/system/8";
    public static final String MATERIAL_ID_DEF9 = "app/default/system/9";
    public static final String MATERIAL_ID_DEF_OTHER = "app/default/system/other";
    public static final String MATERIAL_ID_LIGHT_ON = "app/default/system/lighton";
    public static final String MATERIAL_ID_LIGH_OFF = "app/default/system/lightoff";
    public static final String MATERIAL_ID_SWITCH_ON = "app/default/system/switchon";
    public static final String MATERIAL_ID_SWITCH_OFF = "app/default/system/switchoff";
    public static final String MATERIAL_ID_CURTAIN_OPEN = "app/default/system/curtainopen";
    public static final String MATERIAL_ID_CURTAIN_CLOSE = "app/default/system/curtainclose";
    public static final String MATERIAL_ID_TEMPERATURE = "app/default/system/temperature";
    public static final String MATERIAL_ID_HUMIDITY = "app/default/system/humidity";
    public static final String MATERIAL_ID_PM2_5 = "app/default/system/pm2.5";
    public static final String MATERIAL_ID_LOGO = "app/default/system/logo";
    public static final String CONFIG_PROPERTIES = "config.properties";

    protected static final String TAG = "MaterialsManager";
    protected Map<String, Pattern> wildcharKeys;
    protected Context context;
    protected Map<String, IMaterial> defaultMaterials;
    protected Properties defaultMaterialConfig;
    protected Map<String, IMaterial> customMaterials;
    protected Properties customMaterialConfig;

    public IMaterial getMaterial(String key) {
        if (key  == null || key.isEmpty()){
            return null;
        }
        IMaterial material = getMaterialFromPackage(key, customMaterialConfig, customMaterials);
        if (material != null) {
            return material;
        }
        return getMaterialFromPackage(key, defaultMaterialConfig, defaultMaterials);
    }

    protected IMaterial getMaterialFromPackage(String key, Properties config, Map<String, IMaterial> materials) {
        if (config == null || materials == null) {
            return null;
        }
        String resourceName = config.getProperty(key);
        if (resourceName == null) {
            Iterator<Map.Entry<String, Pattern>> it = wildcharKeys.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Pattern> ent = it.next();
                String wKey = ent.getKey();
                Pattern pthKey = ent.getValue();
                Matcher m = pthKey.matcher(key);
                if (m.matches()) {
                    Log.d(TAG, "get metarial " + wKey + " for " + key);
                    resourceName = config.getProperty(wKey);
                    break;
                }
            }
        }
        if (resourceName == null) {
            return null;
        }
        IMaterial result = materials.get(resourceName);
        return result;
    }

    public void loadZipPackage(File propertyFile, File resFile) throws IOException {
        File extFolder = Environment.getExternalStorageDirectory();
        Log.i(TAG, "Extern Folder base on " + extFolder);
        wildcharKeys = new HashMap<>();
        loadDefaultMaterials();
        loadCustomMaterials(propertyFile, resFile);
    }

    protected void loadCustomMaterials(File propertyFile, File file) throws IOException {
        if (!propertyFile.exists()) {
            Log.i(TAG, "Custom material property " + propertyFile.getAbsolutePath() + " not found. Use default materials only");
            return;
        }
        if (!file.exists()) {
            Log.i(TAG, "Custom material package " + file.getAbsolutePath() + " not found. Use default materials only");
            return;
        }

        FileInputStream fIns = null;
        if (file.exists() && file.canRead() && file.isFile()) {
            fIns = new FileInputStream(file);
            Log.i(TAG, "Load custom material package.");
        }
        customMaterials = new HashMap<>();
        customMaterialConfig = loadCustomConfigProperties(propertyFile);
        loadMaterialsStream(fIns, customMaterialConfig, customMaterials);
        if (fIns != null) {
            fIns.close();
        }
    }

    private Properties loadCustomConfigProperties(File propertyFile) throws IOException {
        InputStream ins = null;
        try {
            ins = new FileInputStream(propertyFile);
            Reader reader = new InputStreamReader(ins, Charset.forName("UTF-8"));
            Properties props = new Properties();
            props.load(reader);
            return props;
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                }
            }
        }
    }

    protected void loadDefaultMaterials() throws IOException {
        defaultMaterials = new HashMap<>();
        defaultMaterialConfig = loadDefaultConfigProperties();
        InputStream defMaterialsPkg = getContext().getAssets().open("baseuidata.zip");
        loadMaterialsStream(defMaterialsPkg, defaultMaterialConfig, defaultMaterials);
        defMaterialsPkg.close();
    }

    private Properties loadDefaultConfigProperties() throws IOException {
        InputStream ins = null;
        try {
            ins = context.getAssets().open("baseuidata.properties");
            Reader reader = new InputStreamReader(ins, Charset.forName("UTF-8"));
            Properties props = new Properties();
            props.load(reader);
            return props;
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                }
            }
        }
    }

    protected void loadMaterialsStream(InputStream inputStream, Properties configProp, Map<String, IMaterial> materialsLib) throws IOException {

        if (inputStream != null) {
            loadFromZipPackage(inputStream, configProp, materialsLib);
        }
        parseMaterials(configProp, materialsLib);
    }

    private void loadFromZipPackage(InputStream inputStream, Properties configProp, Map<String, IMaterial> materialsLib) throws IOException {
        Collection<Object> values = configProp.values();
        Set<String> usedFiles = new HashSet<>();
        for (Object value : values) {
            String valueStr = ((String) value).trim();
            int pos = valueStr.indexOf(':');
            if (pos < 0) {
                continue;
            }
            String type = valueStr.substring(0, pos).trim();
            if (type.equalsIgnoreCase("file")) {
                usedFiles.add(valueStr.substring(pos + 1).trim());
            }
        }
        ZipInputStream zinIns = new ZipInputStream(inputStream);
        ZipEntry entry;
        Map<String, byte[]> zipEnts = new HashMap<>();
        byte[] buffer = new byte[1024 * 1024];
        while ((entry = zinIns.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                Log.d(TAG, "Ignore " + entry.getName());
                continue;
            }
            String fileName = entry.getName();
            if (!usedFiles.contains(fileName)) {
                continue;
            }
            Log.d(TAG, "Load bitmap " + entry.getName());
            Bitmap bm = BitmapFactory.decodeStream(zinIns);
            materialsLib.put("file:" + fileName, new DrawableMaterail(getContext(), bm));
        }
        zinIns.close();
    }

    protected void parseMaterials(Properties prop, Map<String, IMaterial> materialsLib) {
        for (Map.Entry<Object, Object> ent : prop.entrySet()) {
            String materialID = (String) ent.getKey();
            checkWildcharKey(materialID);
            String materialResource = (String) ent.getValue();
            String strOrgValue = materialResource.trim();
            int pos = strOrgValue.indexOf(':');
            if (pos < 0) {
                ent.setValue(null);
                continue;
            }
            String materialType = strOrgValue.substring(0, pos).toLowerCase().trim();
            String materialValue = strOrgValue.substring(pos + 1).trim();
            String cleanMaterialResourceName = materialType + ":" + materialValue;
            ent.setValue(cleanMaterialResourceName);
            if (materialType.equals("file")) {
                continue; // files are already loaded
            }
            if (materialsLib.containsKey(cleanMaterialResourceName)) {
                continue;
            }
            if (materialType.equals("color")) {
                materialsLib.put(cleanMaterialResourceName, createColorMaterial(materialValue));
            } else if (materialType.equalsIgnoreCase("font")) {
                materialsLib.put(cleanMaterialResourceName, createFontMaterial(materialValue));
            } else {
                Log.w(TAG, "Material [" + materialResource + "] not processed");
            }
        }
    }

    private void checkWildcharKey(String meterialID) {
        if (meterialID.indexOf('*') > 0) {
            String ptnKey = meterialID.replace("*", "[^/]+");
            wildcharKeys.put(meterialID, Pattern.compile(ptnKey));
        }
    }


    private FontMaterial createFontMaterial(String materialData) {
        String[] inputs = materialData.trim().split(",");
        if (inputs.length != 4) {
            return null;
        }
        try {
            String family = inputs[0].trim();
            String style = inputs[1].trim();
            String size = inputs[2].trim();
            String color = inputs[3].trim();
            return new FontMaterial(family, style, size, color);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    protected ColorMaterial createColorMaterial(String materialData) {
        return new ColorMaterial(Color.parseColor(materialData));
    }

    protected DrawableMaterail loadFileMaterial(byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();// Create object of bitmapfactory's option method for further option use
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return new DrawableMaterail(context, bitmap);
    }


//    protected void loadConfigureationProperties(Properties props, byte[] configBytes) throws IOException {
//        ByteArrayInputStream bin = new ByteArrayInputStream(configBytes);
//        props.load(bin);
//        bin.close();
//    }

    protected byte[] readFromZipEntry(ZipInputStream zinIns, byte[] buffer) throws IOException {
        ByteArrayOutputStream bout = null;
        try {
            bout = new ByteArrayOutputStream(1024);
            int cnt;
            while ((cnt = zinIns.read(buffer)) > 0) {
                bout.write(buffer, 0, cnt);
            }
            return bout.toByteArray();
        } finally {
            if (bout != null) {
                bout.close();
            }
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}