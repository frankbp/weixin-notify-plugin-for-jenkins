package jenkins.plugin.weixinnotification;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import hudson.model.*;

import jenkins.model.Jenkins;
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
    private String agentSecret;
    private String agentId;
    private String toUser;
    private String buildStatus = "";

    private Run run;

    private PrintStream logger;

    private static String token;
    private static Map<String, String> BUILD_RESULT_MAP = new HashMap<>();


    {
        corpId = new WeixinNotification().getDescriptor().getCorpId();
        agentSecret = new WeixinNotification().getDescriptor().getAgentSecret();
        agentId = new WeixinNotification().getDescriptor().getAgentId();

        BUILD_RESULT_MAP.put("SUCCESS", "成功");
        BUILD_RESULT_MAP.put("FAILURE", "失败");
        BUILD_RESULT_MAP.put("UNSTABLE", "不稳定");
    }

    public WeixinServiceImpl(BuildListener listener, AbstractBuild build,
                             String toUser) {
        this.logger = listener.getLogger();
        this.build = build;
        this.toUser = toUser;
    }

    public WeixinServiceImpl(TaskListener listener, Run run,
                             String toUser, String buildStatus) {
        this.logger = listener.getLogger();
        this.run = run;
        this.toUser = toUser;
        this.buildStatus = buildStatus;
    }

    public void sendContent(String content) {
        if (token == null || !sendMessageWithToken(content)) {
            requestForToken();
            sendMessageWithToken(content);
        }
    }

    @Override
    public void sendText(String text) {
        String content = String.format(WeixinApi.TEMPLATE_SEND_TEXT,
                toUser,
                this.agentId,
                text);
        sendContent(content);
    }

    @Override
    public void sendImage(String imageId) {
        String content = String.format(WeixinApi.TEMPLATE_SEND_IMAGE,
                toUser,
                this.agentId,
                imageId);
        sendContent(content);
    }

    @Override
    public void sendVoice(String voiceId) {
        String content = String.format(WeixinApi.TEMPLATE_SEND_IMAGE,
                toUser,
                this.agentId,
                voiceId);
        sendContent(content);
    }

    @Override
    public void sendVideo(String videoId) {
        String content = String.format(WeixinApi.TEMPLATE_SEND_IMAGE,
                toUser,
                this.agentId,
                videoId);
        sendContent(content);
    }

    @Override
    public void sendFile(String fileId) {
        String content = String.format(WeixinApi.TEMPLATE_SEND_IMAGE,
                toUser,
                this.agentId,
                fileId);
        sendContent(content);
    }

    @Override
    public void sendTextcard() {
        String content = String.format(WeixinApi.TEMPLATE_SEND_TEXTCARD,
                toUser,
                this.agentId,
                String.format(WeixinMessageTemplate.TITLE,
                        getBuildResult()),
                generateDescription(),
                generateUrl());
        sendContent(content);
    }

    @Override
    public void sendNews() {
        String content = String.format(WeixinApi.TEMPLATE_SEND_NEWS,
                    toUser,
                    this.agentId,
                    String.format(WeixinMessageTemplate.TITLE, getBuildResult()),
                    generateDescription(),
                    generateUrl(),
                    generateIcon());
        sendContent(content);
    }


    private String generateDescription() {
        if (this.build != null) {
            return String.format(WeixinMessageTemplate.DESCRIPTION,
                    this.build.getProject().getDisplayName(),
                    this.build.getId(),
                    this.build.getTimestamp().getTime().toString(),
                    this.build.getDurationString(),
                    getBuildResult());
        }
        if (this.run != null) {
            return String.format(WeixinMessageTemplate.DESCRIPTION,
                    this.run.getParent().getDisplayName(),
                    this.run.getId(),
                    this.run.getTimestamp().getTime().toString(),
                    this.run.getDurationString(),
                    getBuildResult());
        }
        return "";
    }

    private String getBuildResult() {
        String buildResult = "";

        if (this.build != null) {
            buildResult = this.build.getResult().toString();
        } else {
            if (this.run != null) {
                if (this.run.getResult() != null) {
                    buildResult = this.run.getResult().toString();
                }
            } else {
                if (!this.buildStatus.equals("")) {
                    buildResult = buildStatus;
                }
            }
        }
        this.logger.println((String.format("构建结果: %s", buildResult)));

        return BUILD_RESULT_MAP.getOrDefault(buildResult, "未知");
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

    private String getJenkinsIconUrl() {

        return Jenkins.getInstance().getRootUrl() + "static/e59dfe28/images/48x48/";
    }

    private String getSuccessIcon() {
        return getJenkinsIconUrl() + "blue.png";
    }

    private String getFailIcon() {
        return getJenkinsIconUrl() + "red.png";
    }

    private String getUnstableIcon() {
        return getJenkinsIconUrl() + "yellow.png";
    }

    private String getUnknownIcon() {
        return getJenkinsIconUrl() + "grey.png";
    }

    private String generateIcon() {
        if (getBuildResult().equals("成功")) {
            return getSuccessIcon();
        }
        if (getBuildResult().equals("失败")) {
            return getFailIcon();
        }
        if (getBuildResult().equals("不稳定")) {
            return getUnstableIcon();
        }
        return getUnknownIcon();
    }

    private String requestForToken() {
        String url = String.format(WeixinApi.URL_ACCESS_TOKEN, this.corpId, this.agentSecret);

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
                        if (errmsg.equals(WeixinApi.ERROR_MESSAGE_INVALID_TOKEN) ||
                                errmsg.equals(WeixinApi.ERROR_MESSAGE_EXPIRED_TOKEN)) {
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
