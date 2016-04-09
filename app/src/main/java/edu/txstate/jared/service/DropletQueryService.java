package edu.txstate.jared.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.txstate.jared.menudemo.Droplet;

/**
 * This service is destroyed when everything in onHandleIntent finishes
 */
public class DropletQueryService extends IntentService {

    private static final String TAG = "DROPLET_QUERY_SERVICE";

    private IBinder mBinder = new Binder();     // binder given to client activity
    private Intent requestIntent;

    public DropletQueryService() {
        super(TAG);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    protected void onHandleIntent(Intent requestIntent) {
        this.requestIntent = requestIntent;
        if (LocationResult.hasResult(requestIntent)) {
            LocationResult locationResult = LocationResult.extractResult(requestIntent);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                Log.d(TAG, "accuracy: " + location.getAccuracy() + " lat: " + location.getLatitude() + " long: " + location.getLongitude());

                try {
                    Log.d(TAG, "starting GET request");

//                    String basicAuth = "Basic " + Base64.encodeToString("txstate:poopscoop".getBytes(), Base64.NO_WRAP);
//                    conn.setRequestProperty("Authorization", basicAuth);

                    /* Form JSON parameters to send with HTTP request */
                    JSONObject json = new JSONObject();
                    json.put(Droplet.LATITUDE, location.getLatitude());
                    json.put(Droplet.LONGITUDE, location.getLongitude());

                    String params = "latitude=";
                    params += Double.toString(location.getLatitude());
                    params += "&longitude=";
                    params += Double.toString(location.getLongitude());

                    //TODO fix this. adding params to the url is kinda hacky
                    URL hostUrl = new URL("http://104.236.181.178:8000/droplets/q/?" + params);

                    HttpURLConnection conn = (HttpURLConnection) hostUrl.openConnection();
                    conn.setRequestMethod("GET");
//                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    Map<String, List<String>> map = conn.getHeaderFields();
                    for (Map.Entry entry : map.entrySet()) {
                        Log.d(TAG, entry.getKey() + ": " + entry.getValue());
                    }
//                    conn.setRequestProperty("Content-Type", "application/json;");
//                    conn.setRequestProperty("Accept", "application/json,text/plain");
//                    OutputStream os = conn.getOutputStream();

                    Log.d(TAG, "params: " + json.toString());
                    Log.d(TAG, "params (s): " + params);
//                    os.write(json.toString().getBytes("UTF-8"));        // send location parameters
//                    os.write(params.getBytes("UTF-8"));

                    String inputLine;
                    StringBuilder responseText = new StringBuilder();
                    int responseCode = conn.getResponseCode();

                    Log.d(TAG, "GET response Code : " + responseCode);
                    Log.d(TAG, "Response message: " + conn.getResponseMessage());
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        while ((inputLine = reader.readLine()) != null)
                            responseText.append(inputLine + "\n");
                        Log.d(TAG, "RESPONSE: " + responseText.toString());     // Log the response from server
                        reader.close();

                        parseJSON(responseText.toString());     // make Droplet Array from response
                    }
//                    os.flush();
//                    os.close();
                    conn.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public void parseJSON(String response) throws JSONException {
        JSONArray jsonArray = new JSONArray(response);
        ArrayList<Droplet> dropList = new ArrayList<Droplet>();     // pass this to Map to be drawn

        for (int i = 0; i < jsonArray.length(); i++ ) {
            JSONObject json = jsonArray.getJSONObject(i);
            String owner = json.getString(Droplet.OWNER);
            Double latitude = json.getDouble(Droplet.LATITUDE);
            Double longitude = json.getDouble(Droplet.LONGITUDE);
            String data = json.getString(Droplet.DATA);

            /* create found droplet instance and add to list */
            Droplet droplet = new Droplet(owner, latitude, longitude, data);
            dropList.add(droplet);
        }
        // TODO callback to tell the MapsActivity to draw some droplets
    }



    public String fetchCSRF() {
        String csrfToken = "";
        try {
            CookieManager manager = new CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);
            URL url = new URL("http://104.236.181.178:8000/q/");
            URLConnection conn = url.openConnection();
            conn.getContent();

            Map<String, List<String>> map = conn.getHeaderFields();
            for (Map.Entry entry : map.entrySet()) {
                Log.d(TAG, entry.getKey() + ": " + entry.getValue());
            }

            CookieStore cookieJar = manager.getCookieStore();
            List<HttpCookie> cookies = cookieJar.getCookies();

            String cookieString = "";
            csrfToken = "";

            for (HttpCookie c : cookies) {
                Log.d(TAG, "Got cookie: " + c.toString());

                cookieString += c.getName() + "=" + c.getValue() + ";";

                if (c.getName().equals("csrftoken")) {
                    csrfToken = c.getValue();
                }
            }

            String cookieStr = cookies.get(0).toString();
            Log.d(TAG, "cookieStr: " + cookieStr);

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csrfToken;
    }


}
