package edu.upenn.cis350.botanist;

import java.util.List;
import java.util.regex.*;

/**
 * Created by kathdix on 3/23/17.
 */

public class ScrapeWebsite {

    public static List<String> urls;

    public static void main(String[] args) {
        URLGetter g = new URLGetter("https://bonnieplants.com");
        g.printStatusCode();
        String html = g.getContents();
        html.replaceAll("\\s+", "");
        Pattern p = Pattern.compile("href=\"/product/\\w*\">");
        Matcher m = p.matcher(html);
        while (m.find()) {
            try {
                String pleaseWork = m.group();
                System.out.println(pleaseWork);
            } catch (IndexOutOfBoundsException e) {
                break;

            } catch (IllegalStateException e) {

            }
        }
        System.out.println("\n\nEND OF EASY SECTION\n\n");
        Pattern pVeg = Pattern.compile("href=\"/product-category/vegetables/.*?\">\\w*</a></li>");
        Matcher mVeg = pVeg.matcher(html);
        while (mVeg.find()) {
            try {
                String pleaseWork = mVeg.group();
                //System.out.println(pleaseWork);
                int endIndex = 0;
                for (int i = 35; i < pleaseWork.length(); i++) {
                    if (pleaseWork.charAt(i) == '"') {
                        endIndex = i;
                        break;
                    }
                }
                //System.out.println(pleaseWork.substring(35, endIndex));
                String url = "https://bonnieplants.com/product-category/vegetables/" + pleaseWork.substring(35, endIndex);
                System.out.println(url);
                //URLGetter g2 = new URLGetter(url);
                //System.out.println(g2.getContents());
            } catch (IndexOutOfBoundsException e) {
                break;

            } catch (IllegalStateException e) {

            }
        }
    }
}
