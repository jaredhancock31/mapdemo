package edu.txstate.jared.api;


import edu.txstate.jared.menudemo.DataDrop;

/**
 * Created by jared on 2/22/16.
 */
public class ReqManager {

    private static final String HOST = "http://104.236.181.178";



    public ReqManager() {

    }


    public void startPost(DataDrop droplet) {
        AsyncPost post = new AsyncPost();
        post.execute(droplet.getDropAsString());
    }


    public static ReqManager getReqManager() {
        return new ReqManager();
    }
}
