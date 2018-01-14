package com.funny;

import com.funny.service.ImageService;
import com.funny.service.WeixinService;
import com.funny.spider.DuowanGifProcessor;
import com.funny.spider.TestPipeline;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FunnyApplicationTests {

    @Autowired
    private DuowanGifProcessor duowanGifProcessor;

    @Autowired
    private TestPipeline testPipeline;


    @Test
    public void contextLoads() throws Exception {
//        String url = "http://tu.duowan.com/scroll/{index}.html";
//        for (int i = 133493; i > 132000; i--) {
//            Spider.create(duowanGifProcessor)
//                    // 从"https://github.com/code4craft"开始抓
//                    .addUrl(url.replace("{index}", i + ""))
//                    // 开启5个线程抓取
//                    .thread(10)
//                    .addPipeline(testPipeline)
//                    // 启动爬虫
//                    .run();
//        }
    }
}