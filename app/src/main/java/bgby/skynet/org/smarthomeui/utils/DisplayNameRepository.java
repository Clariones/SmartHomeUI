package bgby.skynet.org.smarthomeui.utils;

import com.google.gson.reflect.TypeToken;

import org.skynet.bgby.driverutils.SimpleFileRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Clariones on 6/30/2016.
 */
public class DisplayNameRepository extends SimpleFileRepository<Map<String, String>> {

    public static final String LAYOUT_DISPLAY = "layout.display";

    @Override
    protected String convertToJsonStr(Map<String, String> data) throws IOException {
        return gson.toJson(data);
    }

    @Override
    protected String getFilePostfix() {
        return ".names.json";
    }

    @Override
    protected Map<String, String> loadFromFile(FileInputStream fIns) {
        setDevelopingMode(false);
        Map<String, String> data = gson.fromJson(new InputStreamReader(fIns), new TypeToken<Map<String, String>>(){}.getType());
        return data;
    }

    @Override
    protected void verifyData(Map<String, String> data) throws IOException {

    }

    @Override
    protected String getDataKey(File dataFile, Map<String, String> result) {
        return LAYOUT_DISPLAY;
    }

    public Map<String, String> getData() {
        Map<String, String> data = getDataByID(LAYOUT_DISPLAY);
        if (data ==  null){
            return new HashMap<>();
        }
        return data;
    }

    public void save(Map<String, String> displayNameMap) {
        try {
            this.setData(LAYOUT_DISPLAY, displayNameMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
