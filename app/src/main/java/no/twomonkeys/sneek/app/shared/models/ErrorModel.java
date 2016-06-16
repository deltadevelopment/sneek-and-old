package no.twomonkeys.sneek.app.shared.models;


import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public ErrorModel(Context context, Map JSONDictionary) {
        this.context = context;
        build(JSONDictionary);
    }

    public void build(Map JSONDictionary) {
        //Check if the dic contains a data object
        Map data = (Map) JSONDictionary.get("data");
        if (data == null) {
            addError((String) JSONDictionary.get("message_id"), "message_id");
        } else {
            Map errorDictionary = (Map) data.get("error");
            Map errorsDictionary = (Map) data.get("errors");

            if (errorDictionary != null) {
                traverseErrors(errorDictionary);
            } else if (errorsDictionary != null) {
                traverseErrors(errorsDictionary);
            }
        }
    }

    public void traverseErrors(Map errorDictionary) {
        for (Object key : errorDictionary.keySet()) {
            ArrayList<String> keyErrors = (ArrayList) errorDictionary.get((String) key);
            addError(keyErrors.get(0), (String) key);
        }
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
        Log.v("String is"," string " + string);
        int text_id = context.getResources().getIdentifier(string, "string", context.getPackageName());
        return context.getString(text_id);
    }

    public boolean hasErrors() {
        if (errorsMutable == null) {
            return false;
        }
        return errorsMutable.size() > 0 ? true : false;
    }
}
