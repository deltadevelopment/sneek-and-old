package no.twomonkeys.sneek.app.shared.models;

import java.util.Map;

/**
 * Created by simenlie on 12.05.16.
 */
public class ResponseModel {
    public boolean success;
    public String message;
    public String message_id;
    public Map data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
