package com.funny.spider;

import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

@Component
public class DuowanGifProcessor implements PageProcessor {



    /**
     * 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
     */
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);


    /**
     * process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
     */
    @Override
    public void process(Page page) {
        List<String> pageLinks = page.getHtml().xpath("//div[@class='rounded center']").links().all();
        // 此弹图来源url
        String source = pageLinks.get(0);


        try {
//            Gallery gallery = galleryService.findBySource(source);
//            if (gallery == null) {
//                // 弹图标题
//                String name = page.getHtml().xpath("//div[@class='title']").regex(".*<h1>.*：(.*)</h1>.*").toString();
//                gallery = new Gallery();
////                gallery.setName(name);
////                gallery.setSource(source);
//                gallery.setImages(new ArrayList<>());
//            }
//
//            Selectable picbox = page.getHtml().xpath("//div[@class='pic-box']");
//            List<String> urls = picbox.regex(".*data-img=\"(.*?)\" data-mp4").all();
//            List<String> names = picbox.regex(".*<p class=\"comment\">(.*?)</p>").all();
//
//            for (int i = 0; i < urls.size(); i++) {
////                gallery.getImages().add(Image.builder().name(names.get(i)).url(urls.get(i)).build());
//            }
//            galleryService.save(gallery);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // 部分二：定义如何抽取页面信息，并保存下来
//        page.putField("url", "");
//        page.putField("name", "");

        // 部分三：从页面发现后续的url地址来抓取

//        for (int i = 0; i < pageLinks.size(); i++) {
//            if (page.getUrl().toString().equals(pageLinks.get(i)) && i < pageLinks.size() - 1) {
//                page.addTargetRequest(pageLinks.get(i + 1));
//            }
//        }


//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
//        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
//        if (page.getResultItems().get("name")==null){
//            //skip this page
//            page.setSkip(true);
//        }
//        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));

//        System.out.println(page.getHtml().links().regex("http://tu\\.duowan\\.com/gallery/\\d+.html").all());
//        System.out.println(page.getHtml().links().regex("https://github\\.com/\\w+/\\w+").all());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new DuowanGifProcessor())
                // 从"https://github.com/code4craft"开始抓
                .addUrl("http://tu.duowan.com/scroll/136231.html")
                // 开启5个线程抓取
                .thread(5)
                .addPipeline(new ConsolePipeline())
                // 启动爬虫
                .run();
    }
}
