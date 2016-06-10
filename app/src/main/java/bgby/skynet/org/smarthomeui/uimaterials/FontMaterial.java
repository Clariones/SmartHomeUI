package bgby.skynet.org.smarthomeui.uimaterials;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by Clariones on 6/2/2016.
 */
public class FontMaterial extends BaseMaterial {
    private String fontFamily;
    private Integer fontStyle;
    private Integer fontSize;
    private Integer fontColor;

    public FontMaterial(String family, String style, String size, String color) {
        if (family != null && !family.isEmpty()) {
            this.fontFamily = family;
        }
        if (style != null && !style.isEmpty()) {
            fontStyle = convertToStyle(style);
        }
        if (size != null && !size.isEmpty()) {
            try {
                fontSize = Integer.parseInt(size);
            } catch (Throwable t) {
            }
        }
        if (color != null && !color.isEmpty()) {
            try {
                fontColor = Color.parseColor(color);
            } catch (Throwable t) {
            }
        }
    }

    @Override
    public void applyToFont(TextView view) {
        TextView txt = (TextView) view;
        Typeface orgTF = txt.getTypeface();
        if (orgTF == null){
            orgTF = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        }
        if (fontFamily != null) {
            if (fontStyle != null) {
                orgTF = Typeface.create(fontFamily, fontStyle);
            } else {
                orgTF = Typeface.create(fontFamily, orgTF.getStyle());
            }
            txt.setTypeface(orgTF);
        } else if (fontStyle != null) {
            orgTF = Typeface.create(orgTF, fontStyle);
            txt.setTypeface(orgTF);
        }
        if (fontColor != null) {
            txt.setTextColor(fontColor);
        }
        if (fontSize != null) {
            txt.setTextSize(fontSize);
        }
    }

    private Integer convertToStyle(String style) {
        String strIn = style.toLowerCase();
        if (strIn.equals("normal")) {
            return Typeface.NORMAL;
        }
        if (strIn.equals("b")) {
            return Typeface.BOLD;
        }
        if (strIn.equals("bi")) {
            return Typeface.BOLD_ITALIC;
        }
        if (strIn.equals("i")) {
            return Typeface.ITALIC;
        }
        return null;
    }
}
