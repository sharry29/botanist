package edu.upenn.cis350.botanist;

import java.io.*;
import java.util.List;
import java.util.regex.*;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by kathdix on 3/23/17.
 */

public class ScrapeWebsite {
    public static List<String> urls;
    public static Writer writer;
    public static FirebaseDatabase db;

    /**
     * Scrape the Bonnie's Plants website to get information about all of the different plants.
     * @param args
     */
    public static void main(String[] args) {
        URLGetter g = new URLGetter("https://bonnieplants.com");
        try {
            writer = new FileWriter(new File("plant_info.json"), false);
            writer.append("{\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.printStatusCode();
        String html = g.getContents();
        html.replaceAll("\\s+", "");
        Pattern p = Pattern.compile("href=\"/product/\\w*\">");
        Matcher m = p.matcher(html);
        while (m.find()) {
            try {
                String pleaseWork = m.group();
                String url = "https://bonnieplants.com/" + pleaseWork.substring(7, pleaseWork.length() - 2);
                writeInfo(url);
            } catch (IndexOutOfBoundsException e) {
                break;

            } catch (IllegalStateException e) {

            }
        }
        Pattern pVeg = Pattern.compile("href=\"/product-category/vegetables/.*?\">\\w*</a></li>");
        Matcher mVeg = pVeg.matcher(html);
        while (mVeg.find()) {
            try {
                String pleaseWork = mVeg.group();
                int endIndex = 0;
                for (int i = 35; i < pleaseWork.length(); i++) {
                    if (pleaseWork.charAt(i) == '"') {
                        endIndex = i;
                        break;
                    }
                }
                String url = "https://bonnieplants.com/product-category/vegetables/" + pleaseWork.substring(35, endIndex);
                getPlantsInCategory(url);
            } catch (IndexOutOfBoundsException e) {
                break;

            } catch (IllegalStateException e) {

            }
        }
        try {
            writer.append("}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all plants in a particular category. For example, all cucumbers.
     * @param url
     */
    private static void getPlantsInCategory(String url) {
        URLGetter g = new URLGetter(url);
        String html = g.getContents();
        Pattern p = Pattern.compile("<a href=\"https://bonnieplants.com/product/.*?/\" class=\"woocommerce-LoopProduct-link\">");
        Matcher m = p.matcher(html);
        while (m.find()) {
            try {
                String pleaseWork = m.group();
                int endIndex = 0;
                for (int i = 9; i < pleaseWork.length(); i++) {
                    if (pleaseWork.charAt(i) == '"') {
                        endIndex = i;
                        break;
                    }
                }
                String newUrl = pleaseWork.substring(9, endIndex);
                writeInfo(newUrl);
                } catch (IndexOutOfBoundsException e) {
                break;

            } catch (IllegalStateException e) {

            }
        }


    }

    /**
     * Write information about the plants to a file
     * @param url
     */
    private static void writeInfo(String url) {
        URLGetter g = new URLGetter(url);
        String html = g.getContents();
        if (url.equals("https://bonnieplants.com/product/custard-wax-bean/")) {
        }
        Pattern p = Pattern.compile("--><title>.*?</title>");
        Matcher m = p.matcher(html);
        while (m.find()) {
            try {
                String pleaseWork = m.group();
                for (int i = 10; i < pleaseWork.length(); i++) {
                    if (!(pleaseWork.substring(i, i+1).matches("\\w") || pleaseWork.substring(i, i+1).matches("\\s"))) {
                        pleaseWork = pleaseWork.substring(10, i);
                        break;
                    }
                }
                String light = findLight(html);

                writer.append("\"" + pleaseWork + "\" : {\n\"light\": \"" + light + "\",\n\"url\": \"" + url + "\"},\n");

            } catch (IndexOutOfBoundsException e) {
                break;

            } catch (IllegalStateException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Get information about the light requirements of the plants.
     * @param html
     * @return
     */
    private static String findLight(String html) {
        Pattern pTwo = Pattern.compile("Light:</strong>.*?</li>");
        Matcher mTwo = pTwo.matcher(html);
        while (mTwo.find()) {
            String light = mTwo.group();
            for (int i = 15; i < light.length(); i++) {
                if (light.substring(i, i+1).equals("<")) {
                    light = light.substring(15, i);
                    break;
                }
            }
            return light;
        }
        return null;
    }

}
