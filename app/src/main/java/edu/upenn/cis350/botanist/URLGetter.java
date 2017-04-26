package edu.upenn.cis350.botanist;

import java.net.*;
import java.util.*;

/**
 * Created by kathdix on 3/23/17.
 */

public class URLGetter {

    private URL url;
    private HttpURLConnection httpConnection;

    /**
     * Creates a URL from the given string.
     * Open the connection to be used later.
     * @param url the url to get the information from
     */
    public URLGetter(String url) {

        try {
            this.url = new URL(url);

            URLConnection connection = this.url.openConnection();
            httpConnection = (HttpURLConnection) connection;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * This method will get the HTML contents.
     * @return the arraylist of contents of the page.
     */
    public String getContents() {
        String str = "";

        Scanner in;
        try {
            in = new Scanner(httpConnection.getInputStream());

            while (in.hasNextLine()) {
                String line = in.nextLine();
                str += line;
            }
        } catch (Exception e) {
        }

        return str;

    }
}
