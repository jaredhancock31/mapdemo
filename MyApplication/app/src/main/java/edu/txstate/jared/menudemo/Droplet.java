package edu.txstate.jared.menudemo;


import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Created by jared on 2/28/16.
 */
public class Droplet {

//    private int user_id;
    private String owner;
    private double latitude;
    private double longitude;
    private String message;

    /* constructor */
    public Droplet(String owner, double latitude, double longitude, String message) {
        this.owner = owner;
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;

    }


    public HashMap<String, String> getDropAsHashMap() {
        HashMap<String, String> params = new HashMap<>();
//        params.put("user_id", Integer.toString(user_id));
        params.put("owner", owner);
        params.put("latitude", Double.toString(latitude));
        params.put("longitude", Double.toString(longitude));
        params.put("message", message);
        return params;
    }


    public String getParamString() {
//        String params = "user_id=";
//        params += Integer.toString(user_id);
        String params = "owner=" + owner;
        params += "&latitude=" + Double.toString(latitude);
        params += "&longitude=" + Double.toString(longitude);
        params += "&message=" + message;

        return params;

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
