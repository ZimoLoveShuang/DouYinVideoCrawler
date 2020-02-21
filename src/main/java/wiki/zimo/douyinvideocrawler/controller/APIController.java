package wiki.zimo.douyinvideocrawler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wiki.zimo.douyinvideocrawler.service.CrawlerService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class APIController {
    @Autowired
    private CrawlerService crawler;

    @RequestMapping("/analysis")
    public Map<String, Object> get(@RequestParam(name = "url", required = true) String url) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            map.put("code", 0);
            map.put("msg", "analysis success");
            if (Math.random() > 0.5) {
                map.put("url", crawler.demo1(url));
            } else {
                map.put("url", crawler.demo2(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 1);
            map.put("msg", "analysis fail. reason: " + e.getLocalizedMessage());
            return map;
        }
        return map;
    }
}
