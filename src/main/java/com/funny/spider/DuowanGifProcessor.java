package com.funny.spider;

import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.regex.Pattern;

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

        // 抽取标题，并判断是否gif图片
        String title = page.getHtml().xpath("//div[@class='title']/h1/text()").toString();
        if (!title.contains("今日囧图")) {
            System.err.println(String.format("非GIF图片, 不处理！\nurl: %s \ntitle: %s", page.getUrl(), title));
            page.setSkip(true);
            return;
        }

        // 图片内容部分
        Selectable picbox = page.getHtml().xpath("//div[@class='pic-box']");
        List<String> urls = picbox.regex(".*data-img=\"(.*?)\" data-mp4").all();
        List<String> names = picbox.regex(".*<p class=\"comment\">(.*?)</p>").all();

        page.putField("urls", urls);
        page.putField("names", names);



        // 第二页，第三页
        String currentPageLink = page.getUrl().toString();
        List<String> pageLinks = page.getHtml().xpath("//div[@class='mod-page']/a").links().all();
        for (int i = 0; i < pageLinks.size(); i++) {
            if (currentPageLink.equals(pageLinks.get(i)) && i < pageLinks.size() - 1) {
                page.addTargetRequest(pageLinks.get(i + 1));
                break;
            }
        }


    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String url = "http://tu.duowan.com/scroll/{index}.html";

        for (int i = 100; i > 1; i--) {
            Spider.create(new DuowanGifProcessor())
                    // 从"https://github.com/code4craft"开始抓
                    .addUrl(url.replace("{index}", i + ""))
                    // 开启5个线程抓取
                    .thread(1)
                    .addPipeline(new TestPipeline())
                    // 启动爬虫
                    .run();
        }
    }

}
