package wiki.zimo.douyinvideocrawler.service;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class CrawlerService {
    @Value("${BASE_API_URL}")
    private String BASE_API_URL;

    public String demo2(String url) throws IOException {
        HttpURLConnection conn = getConn(url);
        conn.connect();
        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            String resultHtml = inputscreamToString(inputStream);
            Document doc = Jsoup.parse(resultHtml);
            Element video = doc.getElementById("theVideo");
            url = video.attr("src").replace("playwm", "play");
            conn = getConn(url);
            conn.connect();
            code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_MOVED_TEMP) {
                String location = conn.getHeaderField("Location");
                return location;
            }
        }
        return null;
    }

    public String demo1(String url) throws IOException {
        HttpURLConnection conn = getConn(url);
        conn.connect();
        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            String resultHtml = inputscreamToString(inputStream);
            Document doc = Jsoup.parse(resultHtml);
            Elements scriptElements = doc.getElementsByTag("script");
            for (Element e : scriptElements) {
                String text = e.toString();
                if (text.contains("douyin_falcon:page/reflow_video/index")) {
                    text = text.substring(text.indexOf('{'), text.lastIndexOf('}')).replaceAll("\\s+", "");
                    text = text.substring(text.indexOf('{', text.indexOf(')')), text.lastIndexOf('}') + 1);
                    JSONObject json = JSONObject.parseObject(text);
                    String itemId = json.getString("itemId");
                    String dytk = json.getString("dytk");
                    url = BASE_API_URL.replace("{}", itemId);
                    url = url.replace("{}", dytk);
                    conn = getConn(url);
                    conn.connect();
                    code = conn.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        text = inputscreamToString(conn.getInputStream());
                        json = JSONObject.parseObject(text).getJSONArray("item_list").getJSONObject(0);
                        json = json.getJSONObject("video").getJSONObject("play_addr_lowbr");
                        url = json.getJSONArray("url_list").getString(0);//.replace("play", "playwm");
                        conn = getConn(url);
                        conn.connect();
                        code = conn.getResponseCode();
                        if (code == HttpURLConnection.HTTP_MOVED_TEMP) {
                            String location = conn.getHeaderField("Location");
                            return location;
                        }
                    }
                }
            }
        }
        return null;
    }

    private HttpURLConnection getConn(String url) throws IOException {
        URL baseURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) baseURL.openConnection();
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
        return conn;
    }

    private String inputscreamToString(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = 0;
        byte[] bt = new byte[1024];
        while ((len = in.read(bt)) != -1) {
            out.write(bt, 0, len);
        }
        String content = new String(out.toByteArray());
        return content;
    }
}
