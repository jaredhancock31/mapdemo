package edu.txstate.jared.api;


import android.content.Context;
import android.location.Location;

import edu.txstate.jared.menudemo.Droplet;
import edu.txstate.jared.menudemo.DropletDiscoveryListener;

/**
 * Created by jared on 2/22/16.
 */
public class ReqManager {

    private static final String TAG = "REQMANAGER";
    private static final String HOST = "http://104.236.181.178";
    private DropletDiscoveryListener dropletListener;


    /**
     * Constructor passing the context as the class that will implement the dropletListener interface
     * @param context activity implementing the dropletListener interface
     */
    public ReqManager(Context context) {
        dropletListener = (DropletDiscoveryListener) context;
    }


    /**
     * Submits a new Droplet object to the server via an AsyncPost in the background
     * @param droplet droplet object to post to the server
     */
    public void startPost(Droplet droplet) {
        AsyncPost post = new AsyncPost();
        post.execute(droplet.getDropAsString());
    }

    public void lookForDropletsNearby(Location currentLocation) {

    }

}
