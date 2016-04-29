package edu.txstate.jared.menudemo;


import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Created by jared on 2/28/16.
 * A data structure reopresenting a message submitted by a user.
 */
public class Droplet implements Parcelable {

    public static final String TAG = "DROPLET";

    /* constants */
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String OWNER = "owner";
    public static final String DATA = "data";

    private String owner;
    private double latitude;
    private double longitude;
    private String data;

    /**
     * Constructor.
     * @param owner User who submitted the Droplet.
     * @param latitude Latitude at which the user submitted the Droplet.
     * @param longitude Longitude at which the user submitted the Droplet.
     * @param data User-created message included with the Droplet.
     */
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


    /**
     * Creates a string that represents that Droplet's data as a URL query for a POST request.
     * @return Droplet data as URL query string.
     */
    public String getParamString() {
        String params = "owner=" + owner;
        params += "&latitude=" + Double.toString(latitude);
        params += "&longitude=" + Double.toString(longitude);
        params += "&data=" + data;
        return params;
    }

    /**
     * Getter for lattitude attribute.
     * @return Droplet's latitude attribute.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Getter for longitude attribute.
     * @return Droplet's longitude attribute.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Getter for Droplet's user-created message.
     * @return Droplet's data attribute.
     */
    public String getData() {
        return data;
    }

    /**
     * Getter for Droplet's owner attribute.
     * @return Username of Droplet's owner.
     */
    public String getOwner() {
        return owner;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.owner);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.data);
    }

    /**
     * Constructs Droplet object from Parcel.
     * @param in Parcel to create Droplet from.
     */
    protected Droplet(Parcel in) {
        this.owner = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.data = in.readString();
    }

    public static final Parcelable.Creator<Droplet> CREATOR = new Parcelable.Creator<Droplet>() {
        @Override
        public Droplet createFromParcel(Parcel source) {
            return new Droplet(source);
        }

        @Override
        public Droplet[] newArray(int size) {
            return new Droplet[size];
        }
    };
}
