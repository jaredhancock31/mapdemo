package edu.txstate.jared.menudemo;


import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Created by jared on 2/28/16.
 */
public class Droplet {

    private int drop_id;
    private int user_id;
    private double latitude;
    private double longitude;
    private String message;
    private Timestamp timestamp;


    public Droplet(int user_id, double latitude, double longitude, String message, Timestamp timestamp) {
        this.user_id = user_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
        this.timestamp = timestamp;
    }


    public HashMap<String, String> getDropAsHashMap() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", Integer.toString(user_id));
        params.put("latitude", Double.toString(latitude));
        params.put("longitude", Double.toString(longitude));
        params.put("message", message);
        params.put("timestamp", timestamp.toString());
        return params;
    }


    public String getDropAsString() {
        String params = "user_id=";
        params += Integer.toString(user_id);
        params += "&latitude=" + Double.toString(latitude);
        params += "&longitude=" + Double.toString(longitude);
        params += "&message=" + message;
        params += "&timestamp=" + timestamp.toString();

        return params;

    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public int getDrop_id() {
        return drop_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getMessage() {
        return message;
    }

}
