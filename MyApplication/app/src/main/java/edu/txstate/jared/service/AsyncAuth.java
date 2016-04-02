package edu.txstate.jared.service;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
import java.net.URLEncoder;
import java.util.List;

/**
 * This class is responsible for logging in or registering to the server and retrieving the
 * Authorization token
 *
 * Created by jared on 3/29/16.
 */
public class AsyncAuth extends AsyncTask<String, Void, Boolean> {

    public static final String TAG = "AUTH";

    public AsyncAuth() {
        //
    }

    @Override
    protected Boolean doInBackground(String... params) {

        try {
            CookieManager manager = new CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);
            URL url = new URL("http://104.236.181.178:8000/dummy/");
            URLConnection connection = url.openConnection();
            connection.getContent();

            CookieStore cookieJar = manager.getCookieStore();
            List<HttpCookie> cookies = cookieJar.getCookies();

            String cookieString = "";
            String csfrToken = "";

            for (HttpCookie c : cookies) {
                Log.d(TAG, "Got cookie: " + c.toString());

                cookieString += c.getName() + "=" + c.getValue() + ";";

                if (c.getName().equals("csrftoken")) {
                    csfrToken = c.getValue();
                }
            }

            String cookieStr = cookies.get(0).toString();
            Log.d(TAG, "cookieStr: " + cookieStr);

            String pStr = URLEncoder.encode("?username=johntest1&email=hipbd@slipry.net&password=Password1", "UTF-8");
            URL hostUrl = new URL("http://104.236.181.178:8000/rest-auth/login/");
            HttpURLConnection conn = (HttpURLConnection) hostUrl.openConnection();
            Log.d(TAG, "opened the conn");

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("X-CSRFToken", csfrToken);
            conn.setRequestProperty("Cookie", cookieString);
            conn.setRequestProperty("Connections", "keep-alive");

            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            out.write(pStr.getBytes());

            // Getting 400 here

            Log.d(TAG, "wrote the bytes");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            Integer responseCode = conn.getResponseCode();
            Log.d(TAG, "response code: " + responseCode.toString());
            String line;

            while ((line = reader.readLine()) != null) {
                Log.d(TAG, line);
            }
            out.flush();
            out.close();
            reader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
