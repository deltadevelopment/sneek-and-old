package no.twomonkeys.sneek.app.shared.models;


import android.content.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import no.twomonkeys.sneek.R;

/**
 * Created by simenlie on 01.06.16.
 */
public class ErrorModel {

    private HashMap<String, String[]> errorsMutable;
    Context context;

    public ErrorModel(Context context) {
        this.context = context;
    }

    public void addError(String value, String key) {
        if (errorsMutable == null) {
            errorsMutable = new HashMap<>();
        }
        String[] valueAr = {value};
        errorsMutable.put(key, valueAr);
    }

    public String errorForKey(String key) {
        if (errorsMutable != null) {
            if (errorsMutable.get(key) != null) {
                String error = errorsMutable.get(key)[0];

                return getLocalizedString(error);
            }
        }
        return null;
    }

    public String getLocalizedString(String string) {
        int text_id = context.getResources().getIdentifier(string, "string", context.getPackageName());
        return context.getString(text_id);
    }


}
