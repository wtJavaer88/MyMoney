package com.wnc.mymoney.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupHelper
{

    public static String getJsonResult(String url) throws Exception
    {
        return Jsoup
                .connect(url)
                .timeout(60000)
                .header("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36")
                .ignoreContentType(true).execute().body();
    }

    public static Document getDocumentResult(String url) throws Exception
    {
        return Jsoup
                .connect(url)
                .timeout(60000)
                .header("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
                .get();
    }
}
