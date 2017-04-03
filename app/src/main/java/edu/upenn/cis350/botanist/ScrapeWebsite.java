package edu.upenn.cis350.botanist;

import java.io.*;
import java.util.List;
import java.util.regex.*;
import com.google.firebase.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by kathdix on 3/23/17.
 */

public class ScrapeWebsite {
    public static List<String> urls;
    public static Writer writer;
    public static FirebaseDatabase db;

    public static void main(String[] args) {
        //FirebaseDatabase d = FirebaseDatabase.getInstance("https://cis350-botanist.firebaseio.com/");
        URLGetter g = new URLGetter("https://bonnieplants.com");
        try {
            writer = new FileWriter(new File("plant_info.txt"), false);
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
                System.out.println(url);
                writeInfo(url);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("here");
                break;

            } catch (IllegalStateException e) {
                System.out.println("here!");

            }
        }
        System.out.println("\n\nEND OF EASY SECTION\n\n");
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
                System.out.println(url);
                getPlantsInCategory(url);
            } catch (IndexOutOfBoundsException e) {
                break;

            } catch (IllegalStateException e) {

            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getPlantsInCategory(String url) {
        URLGetter g = new URLGetter(url);
        String html = g.getContents();
        Pattern p = Pattern.compile("<a href=\"https://bonnieplants.com/product/.*?/\" class=\"woocommerce-LoopProduct-link\">");
        Matcher m = p.matcher(html);
        while (m.find()) {
            try {
                String pleaseWork = m.group();
                //System.out.println(pleaseWork);
                int endIndex = 0;
                for (int i = 9; i < pleaseWork.length(); i++) {
                    if (pleaseWork.charAt(i) == '"') {
                        endIndex = i;
                        break;
                    }
                }
                String newUrl = pleaseWork.substring(9, endIndex);
                System.out.println(newUrl);
                writeInfo(newUrl);
                } catch (IndexOutOfBoundsException e) {
                break;

            } catch (IllegalStateException e) {

            }
        }


    }

    private static void writeInfo(String url) {
        URLGetter g = new URLGetter(url);
        String html = g.getContents();
        if (url.equals("https://bonnieplants.com/product/custard-wax-bean/")) {
            //System.out.println(html);
        }
        Pattern p = Pattern.compile("--><title>.*?</title>");
        Matcher m = p.matcher(html);
        while (m.find()) {
            try {
                String pleaseWork = m.group();
                //System.out.println(pleaseWork);
                for (int i = 10; i < pleaseWork.length(); i++) {
                    if (!(pleaseWork.substring(i, i+1).matches("\\w") || pleaseWork.substring(i, i+1).matches("\\s"))) {
                        pleaseWork = pleaseWork.substring(10, i);
                        break;
                    }
                }
                System.out.println(pleaseWork);
                writer.append(pleaseWork + "\n" + url +"\n");
                findLight(html);
                writer.append("\n\n");
            } catch (IndexOutOfBoundsException e) {
                break;

            } catch (IllegalStateException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void findLight(String html) {
        Pattern pTwo = Pattern.compile("Light:</strong>.*?</li>");
        Matcher mTwo = pTwo.matcher(html);
        while (mTwo.find()) {
            String light = mTwo.group();
            System.out.println(mTwo.group());
            for (int i = 15; i < light.length(); i++) {
                if (light.substring(i, i+1).equals("<")) {
                    light = light.substring(15, i);
                    break;
                }
            }
            try {
                writer.append("Light needs: " + light);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
