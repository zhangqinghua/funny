package com.funny.service;

import com.funny.utils.Utils;
import com.funny.vo.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Component
public class WeixinService {

    private RestTemplate restTemplate = new RestTemplate();


    /**
     * 账号相关
     */
    @Value("${weixin.appid}")
    private String appid;
    @Value("${weixin.secret}")
    private String secret;


    /**
     * 凭证
     * 凭证获取时间，单位：Linux时间
     * 凭证有效时间，单位：秒
     */
    private String accessToken;
    private long accessTokenExpiresTime;
    private int accessTokenExpiresIn = 7200 * 1000;


    /**
     * 临时票据
     * 临时票据获取时间，单位：Linux时间
     * 临时票据有效时间，单位：秒
     */
    private String ticket;
    private long ticketExpiresTime;
    private int ticketExpiresIn;


    /**
     * 签名
     * 生成签名的随机串
     * 生成签名的时间戳
     */
    private String signature;
    @Value("${weixin.noncestr}")
    private String noncestr;
    private long signatureTimestamp;


    private String getSignature(String url) throws Exception {
        signatureTimestamp = System.currentTimeMillis();
        signature = "jsapi_ticket={jsapi_ticket}&noncestr={noncestr}&timestamp={timestamp}&url={url}";
        signature = signature.replace("{jsapi_ticket}", getJsapiTicket()).replace("{noncestr}", noncestr).replace("{timestamp}", signatureTimestamp + "").replace("{url}", url);
        signature = Utils.getSha1(signature);
        return signature;
    }

    public String getAccessToken() throws Exception {
        // 如果凭证不存在或者过时，则重新获取
        if (Utils.isEmpty(accessToken) || System.currentTimeMillis() - accessTokenExpiresTime > accessTokenExpiresIn) {
            String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + secret;
            JSON result = new JSON(restTemplate.getForObject(access_token_url, String.class));

            if (!Utils.isEmpty(result.getStr("errmsg"))) {
                throw new Exception("获取AccessToken失败: " + result.getStr("errmsg"));
            }

            accessToken = result.getStr("access_token");
            accessTokenExpiresIn = Integer.parseInt(result.getStr("expires_in")) * 1000;
            accessTokenExpiresTime = System.currentTimeMillis();
        }
        return accessToken;
    }

    private String getJsapiTicket() throws Exception {
        // 如果凭证不存在或者过时，则重新获取
        if (Utils.isEmpty(ticket) || System.currentTimeMillis() - ticketExpiresTime > ticketExpiresIn) {
            String ticketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + getAccessToken() + "&type=jsapi";
            JSON result = new JSON(restTemplate.getForObject(ticketUrl, String.class));
            ticket = result.getStr("ticket");
            ticketExpiresIn = Integer.parseInt(result.getStr("expires_in")) * 1000;
            ticketExpiresTime = System.currentTimeMillis();
        }
        return ticket;
    }

    /**
     * 获取素材总数
     *
     * @return {"voice_count":COUNT,"video_count":COUNT,"image_count":COUNT,"news_count":COUNT} / {"errcode":-1,"errmsg":"system error"}
     */
    public JSON getMaterialcount() throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=" + getAccessToken();
        String result = restTemplate.getForObject(url, String.class);
        return new JSON(Utils.invalidWeixinReturn(result));
    }

    /**
     * 获取素材列表
     */
    public JSON batchgetMaterial(String type, int pageNo, int pageSize) throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=" + getAccessToken();
        JSON parames = new JSON();
        parames.put("type", type);
        parames.put("offset", (pageNo - 1) * pageSize);
        parames.put("count", pageSize);

        JSON result = new JSON(Utils.invalidWeixinReturn(restTemplate.postForEntity(url, parames.toString(), String.class).getBody()));


        if (!result.isTrue("errcode", "")) {
            throw new Exception(result.getStr("errmsg"));
        }
        return result;
    }


    /**
     * 新增其他类型永久素材
     */
    public JSON addMaterial(String type, MultipartFile media) throws Exception {
        String url = url = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=" + getAccessToken() + "&type=image";

        ByteArrayResource mediaAsResource = new ByteArrayResource(media.getBytes()) {
            @Override
            public String getFilename() {
                return media.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("media", mediaAsResource);
        param.add("fileName", media.getOriginalFilename());

        String result = restTemplate.postForObject(url, param, String.class);

        return new JSON(Utils.invalidWeixinReturn(result));
    }


    /**
     * 删除永久素材
     *
     * @param mediaId 要删除的素材的media_id
     * @return {"errcode":ERRCODE,"errmsg":ERRMSG} 正常情况下调用成功时，errcode将为0。
     * @throws Exception 其它异常
     */
    public JSON delMaterial(String mediaId) throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=" + getAccessToken();
        JSON parames = new JSON();
        parames.put("media_id", mediaId);
        String result = restTemplate.postForObject(url, parames.toString(), String.class);
        return new JSON(Utils.invalidWeixinReturn(result));
    }

    /**
     * 新增永久图文素材
     *
     * @param articles 图文素材
     * @return 返回的即为新增的图文消息素材的media_id
     * @throws Exception 链接异常
     */
    public JSON addNews(JSON articles) throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=" + getAccessToken();
        String result = restTemplate.postForObject(url, Utils.unInvalidWeixinReturn(articles.toString()), String.class);
        return new JSON(Utils.invalidWeixinReturn(result));
    }

    /**
     * 修改永久图文素材
     *
     * @param media_id
     * @param index
     * @return
     * @throws Exception
     */
    public JSON updateNews(String media_id, int index) throws Exception {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/material/update_mpnews?access_token=" + getAccessToken();
        JSON parames = new JSON();
        parames.put("media_id", media_id);
        parames.put("index", index);
        parames.put("articles/title", "测试修改");

        System.out.println(parames.toString());
        String result = restTemplate.postForObject(url, Utils.unInvalidWeixinReturn(parames.toString()), String.class);
        return new JSON(Utils.invalidWeixinReturn(result));
    }

    public JSON test(String fileName) {
        FileSystemResource resource = new FileSystemResource(new File(fileName));

        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", resource);
        param.add("key", "90060c17e3f423652255可能是电话号码，是否拨号?fdc9455cf528 ");
        param.add("secret", "741f71ecdb76dce5f68bb6af30ac9def ");

        String result = restTemplate.postForObject("https://helloacm.com/api/images-compressor/check/", param, String.class);

        return new JSON(Utils.invalidWeixinReturn(result));
    }
}
