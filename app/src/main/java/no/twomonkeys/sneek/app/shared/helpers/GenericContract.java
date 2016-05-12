package no.twomonkeys.sneek.app.shared.helpers;

import java.util.Map;

/**
 * Created by simenlie on 12.05.16.
 */
public class GenericContract {


    public static Contract get_story() {
      return new Contract() {
            @Override
            public Map generic_contract(Map map) {
                return (Map) map.get("story");
            }
        };
    }

    public static Contract get_feed() {
        return new Contract() {
            @Override
            public Map generic_contract(Map map) {
                return map;
            }
        };
    }

}
