package no.twomonkeys.sneek.app.shared.models;

import java.util.Map;

/**
 * Created by simenlie on 12.05.16.
 */
public class MomentModel extends CRUDModel {
    public int id, media_type, caption_position, story_id;

    private String created_at, media_key, media_url, thumbnail_url, caption;

    public MomentModel(Map map) {
        build(map);
    }

    public void build(Map map) {
        created_at = (String) map.get("created_at");
        media_key = (String) map.get("media_key");
        media_url = (String) map.get("media_url");
        thumbnail_url = (String) map.get("thumbnail_url");
        caption = (String) map.get("caption");
        id = integerFromObject(map.get("id"));
        media_type = integerFromObject(map.get("media_type"));
        caption_position = integerFromObject(map.get("caption_position"));
        story_id = integerFromObject(map.get("story_id"));
    }

    public String getMedia_key() {
        return media_key;
    }

    public String getMedia_url() {
        return media_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public String getCaption() {
        return caption;
    }

    public String getCreated_at() {
        return created_at;
    }
}
