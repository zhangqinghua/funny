package com.funny;

import com.funny.entity.Image;
import com.funny.entity.Tag;
import com.funny.repository.ImageResitory;
import com.funny.service.ImageService;
import com.funny.service.TagService;
import com.funny.service.WeixinService;
import com.funny.spider.DuowanGifProcessor;
import com.funny.spider.TestPipeline;
import com.funny.task.ScheduledTasks;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FunnyApplicationTests {

    @Autowired
    private DuowanGifProcessor duowanGifProcessor;

    @Autowired
    private TestPipeline testPipeline;
    @Autowired
    private ScheduledTasks scheduledTasks;


//    @Test
//    public void contextLoads() throws Exception {
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
//    }

    @Test
    public void contextLoads() throws Exception {
        String url = "http://tu.duowan.com/scroll/{index}.html";
        for (int i = 136359; i > 136000; i--) {
            Spider.create(duowanGifProcessor)
                    // 从"https://github.com/code4craft"开始抓
                    .addUrl(url.replace("{index}", i + ""))
                    // 开启5个线程抓取
                    .thread(10)
                    .addPipeline(testPipeline)
                    // 启动爬虫
                    .run();
        }
    }

    @Test
    public void testPush() {
        scheduledTasks.pushAriticleToWeixin();
    }

    @Autowired
    private TagService tagService;
    @Autowired
    private ImageResitory imageResitory;
    @Autowired
    private ImageService imageService;

    @Test
    public void test3() throws Exception {

        Tag tag = tagService.findOne(1l);

        Page<Image> page = imageService.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            ListJoin<Image, Tag> tagJoin = root.joinList("tags");

            Predicate p4 = cb.equal(tagJoin.get("id"), "2");


            predicates.add(p4);
            System.out.println(root.get("tags").as(List.class));
//            predicates.add(cb.equal(, "1"));
            predicates.add(cb.isNotNull(root.get("url")));

            query.where(predicates.toArray(new Predicate[predicates.size()]));

            query.orderBy(cb.desc(root.get("updateTime")));
            return null;
        }, new PageRequest(1 - 1, 10));

        System.out.println(page.getContent());
    }

    @Test
    public void test4() throws Exception {

    }
}