package no.twomonkeys.sneek.app.shared.models;

import java.util.Map;

import no.twomonkeys.sneek.app.shared.MapCallback;

/**
 * Created by simenlie on 12.05.16.
 */
public abstract class CRUDModel {
    public MapCallback onDataReturned() {
        return new MapCallback() {
            @Override
            public void callbackCall(Map map) {
                build(map);
            }
        };
    }

    public int integerFromObject(Object o) {

        if (o == null){
            return 0;
        }
        if (o instanceof Double){

            return (int)((Double) o).intValue();
        }

        return Integer.getInteger((String) o);
    }

    public boolean booleanFromObject(Object o) {

        if (o == null){
            return false;
        }
        if (o instanceof Boolean){

            return (boolean) o;
        }

        return Boolean.getBoolean((String)o);
    }

    abstract void build(Map map);
}
