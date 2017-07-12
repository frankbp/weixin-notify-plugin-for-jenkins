package jenkins.plugin.weixinnotify;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.http.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;


/**
 * Created by jianjing on 2017/7/11.
 */
public class WeixinServiceImpl implements WeixinService {

    private AbstractBuild build;
    private String corpId;
    private String corpSecret;
    private String agentId;
    private String toUser;

    private Run run;

    private PrintStream logger;

    private static final String apiUrl = "https://qyapi.weixin.qq.com/cgi-bin";

    private static String token;

    {
        corpId = new WeixinNotify().getDescriptor().getCorpId();
        corpSecret = new WeixinNotify().getDescriptor().getCorpSecret();
        agentId = new WeixinNotify().getDescriptor().getAgentId();
    }

    public WeixinServiceImpl(BuildListener listener, AbstractBuild build, String toUser) {
        this.logger = listener.getLogger();
        this.build = build;
        this.toUser = toUser;
    }

    public WeixinServiceImpl(TaskListener listener, Run run, String toUser) {
        this.logger = listener.getLogger();
        this.run = run;
        this.toUser = toUser;
    }

    public void sendContent(String msgType, String content) {
        this.logger.println("sendContent");
        if (!sendMessageWithToken(content)) {
            requestForToken();
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
        this.logger.println("sendTextcard");
        this.logger.println(toUser.toString());
        if (toUser instanceof String) {
            toUser = (String)toUser;
        }

//        if (toUserObject instanceof ArrayList) {
//            StringBuilder temp = new StringBuilder();
//            for (Object object : (ArrayList)toUserObject) {
//                if (object instanceof ArrayList) {
//                    temp.append(String.join("|", (ArrayList)object));
//                }
//            }
//            toUser = temp.toString();
//        }
        this.logger.println(toUser);
        String content = String.format(WeixinMessageTemplate.SEND_TEXTCARD_TEMPLATE,
                toUser,
                this.agentId,
                WeixinMessageTemplate.TITLE,
                generateDescription(),
                generateUrl());
        sendContent("textcard", content);
    }

    private String generateDescription() {
        if (this.build != null) {
            return String.format(WeixinMessageTemplate.DESCRIPTION,
                    this.build.getProject().getDisplayName(),
                    this.build.getTimestamp().getTime().toString(),
                    this.build.getDurationString(),
                    this.build.getResult().toString());
        }
        if (this.run != null) {
            return String.format(WeixinMessageTemplate.DESCRIPTION,
                    this.run.getParent().getDisplayName(),
                    this.run.getTimestamp().getTime().toString(),
                    this.run.getDurationString(),
                    this.run.getResult().toString());
        }
        return "";
    }

    private String generateUrl() {
        if (this.build != null) {
            return this.build.getProject().getAbsoluteUrl() + this.build.getId();
        }
        if (this.run != null) {
            return this.run.getAbsoluteUrl() + this.run.getId();
        }
        return "";
    }

    private String requestForToken() {
        String url = String.format(WeixinApi.URL_ACCESS_TOKEN, this.corpId, this.corpSecret);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);
                logger.println(responseString);
                Map<String, String> responseMap = JSON.parseObject(responseString, new TypeReference<Map<String, String>>(){});
                logger.println(responseMap);
                token = responseMap.get("access_token").toString(); //缓存token
                return token;

            } catch (Exception e) {
                logger.println(e.toString());
            } finally {
                response.close();
            }
        } catch (IOException e) {
            logger.println(e.toString());
        }
        return null;
    }

    private boolean sendMessageWithToken(String content) {
        logger.println("sendMessageWithToken");

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(String.format(WeixinApi.URL_SEND_MESSAGE, token));

        try {
                StringEntity params = new StringEntity(content, "UTF-8");
            logger.println(content);
                httppost.setEntity(params);
                httppost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                CloseableHttpResponse response = httpClient.execute(httppost);
                try {
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity);
                    Map<String, String> responseMap = JSON.parseObject(responseString, new TypeReference<Map<String, String>>() {});
                    logger.println(responseMap);
                    String errcode = responseMap.get("errcode").toString();
                    logger.println(errcode);
                    if (!errcode.equals("0")) {
                        String errmsg = responseMap.get("errmsg").toString();
                        logger.println(errmsg);
                        if (errmsg.equals("invalid access_token")) {
                            return false;
                        }
                        logger.println(String.format("发送消息失败, errcode=%s, errmsg=%s", errcode, errmsg));
                    }

                } catch (IOException e) {
                    logger.println(String.format("发送消息失败, exception=%s", e.toString()));
                } finally {
                    response.close();
                }
            } catch (IOException e) {
                logger.println(String.format("发送消息失败, exception=%s", e.toString()));
            } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.println("httpClient关闭失败");
            }
        }
            return true;
    }
}
