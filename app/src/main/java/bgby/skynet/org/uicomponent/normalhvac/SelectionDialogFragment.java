package bgby.skynet.org.uicomponent.normalhvac;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bgby.skynet.org.smarthomeui.R;

/**
 * Created by Clariones on 6/11/2016.
 */
public class SelectionDialogFragment extends DialogFragment {
    private static final String TAG = "SelectionDialogFragment";

    public static interface SelectionDialogListener {
        void onSelected(DialogInterface dialog, String selected, int which);
        void onCancel(DialogInterface dialog, String selected, int which);
    }
    protected String selected;
    protected String[] texts;
    protected Drawable[] icons;
    protected String title;
    protected SelectionDialogListener listener;

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String[] getTexts() {
        return texts;
    }

    public void setTexts(String[] texts) {
        this.texts = texts;
    }

    public Drawable[] getIcons() {
        return icons;
    }

    public void setIcons(Drawable[] icons) {
        this.icons = icons;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SelectionDialogListener getListener() {
        return listener;
    }

    public void setListener(SelectionDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setAdapter(createAdapter(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getListener() != null){
                    if (which >= texts.length  || which < 0){
                        getListener().onSelected(dialog, null, which );
                    }else {
                        getListener().onSelected(dialog, texts[which], which);
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getListener() != null){
                    if (which >= texts.length || which < 0){
                        getListener().onCancel(dialog, null, which );
                    }else {
                        getListener().onCancel(dialog, texts[which], which);
                    }
                }
            }
        });

        return builder.create();
    }

    private SimpleAdapter createAdapter() {
        Drawable[] icons = getIcons();


        List<Map<String, Object>> dataList = new ArrayList<>();
        String[] modes = getTexts();
        for(int i=0;i<modes.length;i++){
            Map<String, Object> item = new HashMap<>();
            item.put("text", modes[i]);
            item.put("icon", icons[i]);
            dataList.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(getContext(), dataList, R.layout.dialog_normal_hvac_selection,
                new String[]{"icon", "text"},
                new int[]{R.id.item_image, R.id.item_text});

        Log.i(TAG, "R.layout.dialog_normal_hvac_selection = " + R.layout.dialog_normal_hvac_selection);
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (!(data instanceof  Drawable)){
                    return false;
                }
                if (view instanceof ImageView){
                    ImageView imgView = (ImageView) view;
                    imgView.setImageDrawable((Drawable) data);
                }else{
                    view.setBackground((Drawable) data);
                }
                return true;
            }
        });

        return adapter;
    }


}
