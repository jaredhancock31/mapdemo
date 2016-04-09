package edu.txstate.jared.menudemo;


import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Created by jared on 2/28/16.
 */
public class Droplet {

    /* constants */
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String OWNER = "owner";
    public static final String DATA = "data";

    private String owner;
    private double latitude;
    private double longitude;
    private String data;

    /* constructor */
    public Droplet(String owner, double latitude, double longitude, String data) {
        this.owner = owner;
        this.latitude = latitude;
        this.longitude = longitude;
        this.data = data;

    }


    /**
     * Creates a JSON representation of a Droplet instance
     * @return json object of droplet
     */
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            json.put(OWNER, owner);
            json.put(LATITUDE, latitude);
            json.put(LONGITUDE, longitude);
            json.put(DATA, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    public String getParamString() {
        String params = "owner=" + owner;
        params += "&latitude=" + Double.toString(latitude);
        params += "&longitude=" + Double.toString(longitude);
        params += "&data=" + data;
        return params;

    }




    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getData() {
        return data;
    }

}
