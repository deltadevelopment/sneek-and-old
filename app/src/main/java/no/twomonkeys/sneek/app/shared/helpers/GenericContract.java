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

    public static Contract generate_upload_url() {
        return new Contract() {
            @Override
            public Map generic_contract(Map map) {
                return (Map) map.get("upload_url");
            }
        };
    }

    public static Contract generic_parse() {
        return new Contract() {
            @Override
            public Map generic_contract(Map map) {
                return map;
            }
        };
    }


}
