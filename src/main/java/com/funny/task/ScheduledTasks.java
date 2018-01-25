package com.funny.task;

import com.funny.entity.Image;
import com.funny.entity.Joke;
import com.funny.service.ImageService;
import com.funny.service.JokeService;
import com.funny.service.WeixinService;
import com.funny.utils.Utils;
import com.funny.vo.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ScheduledTasks {

    @Autowired
    private ImageService imageService;
    @Autowired
    private JokeService jokeService;
    @Autowired
    private WeixinService weixinService;

    /**
     * 推送图片
     * <p>
     * 每隔一段时间修改图片的修改日期，这样图片就从发布到首页上去。
     */
    @Scheduled(cron = "0 0/10 * * * *")
    public void pushJoke() {
        try {
            List<Joke> jokes = jokeService.findAll((root, query, cb) -> {
                query.where(cb.equal(root.get("status"), 2));
                query.orderBy(cb.asc(root.get("updateTime")));
                return null;
            }, new PageRequest(0, 1)).getContent();

            jokes.get(0).setUpdateTime(new Date());

            jokeService.save(jokes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0/10 * * * *")
    public void pushImage() {
        try {

            List<Image> gifs = getImages(1, true, true, false);
            gifs.get(0).setUpdateTime(new Date());
            imageService.save(gifs);

            List<Image> images = getImages(1, false, true, false);
            images.get(0).setUpdateTime(new Date());
            imageService.save(images);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Scheduled(cron = "0 0 0/10 * * *")
    public void pushAriticleToWeixin() {
        try {
            System.out.println("开始发布微信文章");

            JSON articles = new JSON();

            articles.put("articles[0]", getImageArticle(10, true).getObj());
            articles.put("articles[1]", getImageArticle(10, false).getObj());
            articles.put("articles[2]", getJokeArticle().getObj());
            weixinService.addNews(articles);
            System.out.println("结束发布微信文章");
        } catch (Exception e) {
            System.out.println("发布微信文章失败");
            e.printStackTrace();
        }
    }

    private JSON getImageArticle(int size, boolean isGif) throws Exception {

        // 读取微信图片文章模板文件
        File weixin_article_temp = ResourceUtils.getFile("classpath:weixin_article_temp.txt");
        InputStreamReader read = new InputStreamReader(new FileInputStream(weixin_article_temp), "utf-8");//考虑到编码格式
        BufferedReader br = new BufferedReader(read);
        StringBuilder template = new StringBuilder();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            template.append(line);
        }

        // 从数据库中选取最新的图片出来
        List<Image> images = getImages(size, isGif, false, true);


        // 上传图片到微信
        String title = "";
        String thumb_media_id = "";
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);

            // 下载图片到本低
            File file = Utils.saveUrlAs(image.getUrl(), "temp");

            if (file == null || file.length() > 2 * 1024 * 1024) {
                System.err.println("文件下载失败或超出2MB大小");
                continue;
            }


            // 上传图片到微信，上传失败的移除
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", input);
            JSON result = weixinService.addMaterial("image", multipartFile);

            file.delete(); // 删除文件


            if (result.isTrue("url", "")) {
                System.err.println("上传图片失败：" + result.toString());
                continue;
            }

            title = image.getDescription();
            thumb_media_id = result.getStr("media_id");
            content.append(template.toString().replace("${index}", (i + 1) + "")
                    .replace("${url}", result.getStr("url"))
                    .replace("${title}", image.getDescription()));
        }

        // 组装微信文章
        JSON article = new JSON();
        article.put("author", "GIF趣图"); // 作者
        article.put("show_cover_pic", "0"); // 不显示封面
        article.put("content_source_url", "http://119.29.231.216"); // 图文消息的原文地址，即点击“阅读原文”后的URL
        article.put("title", title); // 标题
        article.put("thumb_media_id", thumb_media_id); // 图片素材id
        article.put("content", content.toString());

        return article;
    }

    private JSON getJokeArticle() throws Exception {
        // 读取微信文章模板文件
        File weixin_article_temp = ResourceUtils.getFile("classpath:weixin_article_joke_temp.txt");
        InputStreamReader read = new InputStreamReader(new FileInputStream(weixin_article_temp), "utf-8");//考虑到编码格式
        BufferedReader br = new BufferedReader(read);
        StringBuilder template = new StringBuilder();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            template.append(line);
        }

        // 读取最新的段子
        List<Joke> jokes = jokeService.findAll((root, query, cb) -> {
            query.where(cb.equal(root.get("status"), 2));
            query.orderBy(cb.desc(root.get("updateTime")));
            return null;
        }, new PageRequest(0, 20)).getContent();


        // 将每一个段子组装成为文章
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < jokes.size(); i++) {

            String index = (i + 1) + "";
            if (i + 1 < 10) {
                index = "0" + index;
            }
            content.append(template.toString().replace("${index}", index).replace("${content}", jokes.get(i).getDescription()));
        }

        // 组装微信文章
        JSON article = new JSON();
        article.put("author", "GIF趣图"); // 作者
        article.put("show_cover_pic", "0"); // 不显示封面
        article.put("content_source_url", "http://119.29.231.216"); // 图文消息的原文地址，即点击“阅读原文”后的URL
        article.put("title", jokes.get(jokes.size() - 1).getDescription()); // 标题
        article.put("thumb_media_id", "yEHAhkOfTEbrKnC6q6qFpWulBfv_Qb_8nP76hGQcC2o"); // 图片素材id
        article.put("content", content.toString());

        return article;
    }


    private List<Image> getImages(int size, boolean isGif, boolean isAsc, boolean isWeixin) throws Exception {
        return imageService.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (isGif) {
                predicates.add(cb.equal(root.get("suffix"), "gif"));

            } else {
                predicates.add(cb.notEqual(root.get("suffix"), "gif"));
            }

            if (isWeixin) {
                predicates.add(cb.lessThan(root.get("size"), 2 * 1024));// 大小在2mb以内
            }

            predicates.add(cb.equal(root.get("status"), 2));// 处于上线状态
            predicates.add(cb.isNotNull(root.get("url")));// url 不能为空
            query.where(predicates.toArray(new Predicate[predicates.size()]));

            if (isAsc) {
                query.orderBy(cb.asc(root.get("updateTime")));
            } else {
                query.orderBy(cb.desc(root.get("updateTime")));
            }
            return null;
        }, new PageRequest(0, size)).getContent();
    }
}
