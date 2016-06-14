package no.twomonkeys.sneek.app.shared.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    public static Contract v1_get_stream() {
        return new Contract() {
            @Override
            public Map generic_contract(Map map) {
                return (Map) map.get("stream");
            }
        };
    }

    public static Contract get_feed() {
        return new Contract() {
            @Override
            public Map generic_contract(Map map) {
                ArrayList streamsRaw = (ArrayList) map.get("streams");
                ArrayList storiesRaw = (ArrayList) map.get("stories");

                for (Object stream : streamsRaw) {
                    storiesRaw.add(stream);
                }

                HashMap<String, Object> returnMap = new HashMap<>();
                returnMap.put("stories", storiesRaw);


                return returnMap;
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

    public static Contract v1_get_user_username_exists() {
        return new Contract() {
            @Override
            public Map generic_contract(Map map) {
                return map;
            }
        };
    }

    public static Contract v1_get_user() {
        return new Contract() {
            @Override
            public Map generic_contract(Map map) {
                return (Map) map.get("user");
            }
        };
    }



    public static Contract v1_post_user() {
        return new Contract() {
            @Override
            public Map generic_contract(Map map) {
                Map newMap = (Map) map.get("user");
                newMap.put("story", map.get("story"));
                newMap.put("user_session", map.get("user_session"));

                return map;
            }
        };
    }

    public static Contract v1_post_login() {
        return new Contract() {
            @Override
            public Map generic_contract(Map map) {
                Map newMap = (Map) map.get("user_session");
                newMap.put("user", map.get("user"));
                newMap.put("stalkings", map.get("stalkings"));

                return map;
            }
        };
    }

    public static Contract v1_get_stream_by_name() {
        return new Contract() {
            @Override
            public Map generic_contract(Map map) {
                return (Map) map.get("stream");
            }
        };
    }

    public static Contract v1_get_user_by_username() {
        return new Contract() {
            @Override
            public Map generic_contract(Map map) {
                return (Map) map.get("user");
            }
        };
    }
}
