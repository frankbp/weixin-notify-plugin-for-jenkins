package jenkins.plugin.weixinnotify;

import java.io.IOException;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

import jenkins.model.Jenkins;
import org.apache.http.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by jianjing on 2017/7/11.
 */
public class WeixinServiceImpl implements WeixinService {
    private Logger logger = LoggerFactory.getLogger(WeixinService.class);

    private BuildListener listener;
    private AbstractBuild build;
    private String corpId;
    private String corpSecret;
    private String agentId;

    private static final String apiUrl = "https://qyapi.weixin.qq.com/cgi-bin";

    private static String token;

    {
        corpId = new WeixinNotify().getDescriptor().getCorpId();
        corpSecret = new WeixinNotify().getDescriptor().getCorpSecret();
        agentId = new WeixinNotify().getDescriptor().getAgentId();
    }

    public WeixinServiceImpl(BuildListener listener, AbstractBuild build) {
        this.listener = listener;
        this.build = build;
    }

    public void sendContent(String msgType, String content) {
        this.listener.getLogger().println("sendContent");
        if (!sendMessageWithToken(content)) {
            requestForToken(this.corpId, this.corpSecret);
            sendMessageWithToken(content);
        }
    }

    @Override
    public void sendText(String text) {
        sendContent("text", text);
    }

    @Override
    public void sendImage(String imageId) {
        sendContent("image", imageId);
    }

    @Override
    public void sendVoice(String voiceId) {
        sendContent("voice", voiceId);
    }

    @Override
    public void sendVideo(String videoId) {
        sendContent("video", videoId);
    }

    @Override
    public void sendFile(String fileId) {
        sendContent("file", fileId);
    }

    @Override
    public void sendTextcard() {
        this.listener.getLogger().println("sendTextcard");
        String content = String.format(WeixinMessageTemplate.SEND_TEXTCARD_TEMPLATE,
                "jianjing",
                this.agentId,
                WeixinMessageTemplate.TITLE,
                "项目名称: " + this.build.getProject().getDisplayName() +
                        "\n构建开始时间: " + this.build.getTimestamp().getTime().toString() +
                        "\n构建持续时间: " + this.build.getDurationString() +
                        "\n构建结果: " + this.build.getResult().toString(),
                this.build.getProject().getAbsoluteUrl() + this.build.getId());
        sendContent("textcard", content);
    }

    private String requestForToken(String corpId, String corpSecret) {
        String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s", this.corpId, this.corpSecret);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);
                listener.getLogger().println(responseString);
                Map<String, String> responseMap = JSON.parseObject(responseString, new TypeReference<Map<String, String>>(){});
                listener.getLogger().println(responseMap);
                token = responseMap.get("access_token").toString(); //缓存token
                return token;

            } catch (Exception e) {
                listener.getLogger().println(e.toString());
            } finally {
                response.close();
            }
        } catch (IOException e) {
            listener.getLogger().println(e.toString());
        }
        return null;
    }

    private boolean sendMessageWithToken(String content) {
        listener.getLogger().println("sendMessageWithToken");

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(WeixinApi.URL_SEND_MESSAGE + token);

        try {
                StringEntity params = new StringEntity(content, "UTF-8");
            listener.getLogger().println(content);
                httppost.setEntity(params);
                httppost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                CloseableHttpResponse response = httpClient.execute(httppost);
                try {
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity);
                    Map<String, String> responseMap = JSON.parseObject(responseString, new TypeReference<Map<String, String>>() {});
                    listener.getLogger().println(responseMap);
                    String errcode = responseMap.get("errcode").toString();
                    listener.getLogger().println(errcode);
                    if (!errcode.equals("0")) {
                        String errmsg = responseMap.get("errmsg").toString();
                        listener.getLogger().println(errmsg);
                        if (errmsg.equals("invalid access_token")) {
                            return false;
                        }
                        listener.getLogger().println(String.format("发送消息失败, errcode=%s, errmsg=%s", errcode, errmsg));
                    }

                } catch (IOException e) {
                    listener.getLogger().println(String.format("发送消息失败, exception=%s", e.toString()));
                } finally {
                    response.close();
                }
            } catch (IOException e) {
                listener.getLogger().println(String.format("发送消息失败, exception=%s", e.toString()));
            } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                listener.getLogger().println("httpClient关闭失败");
            }
        }
            return true;
    }
}
