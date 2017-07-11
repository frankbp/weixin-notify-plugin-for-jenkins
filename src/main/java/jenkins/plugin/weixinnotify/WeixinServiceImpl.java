package jenkins.plugin.weixinnotify;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import hudson.ProxyConfiguration;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyException;
import java.util.List;
import java.util.Map;

/**
 * Created by jianjing on 2017/7/11.
 */
public class WeixinServiceImpl implements WeixinService {
    private Logger logger = LoggerFactory.getLogger(WeixinService.class);

    private String jenkinsURL;

    private boolean onStart;

    private boolean onSuccess;

    private boolean onFailed;

    private TaskListener listener;

    private AbstractBuild build;

    private static final String apiUrl = "https://qyapi.weixin.qq.com/cgi-bin";

    private String api;

    private static String token;

    public WeixinServiceImpl(String token, boolean onStart, boolean onSuccess, boolean onFailed, TaskListener listener, AbstractBuild build) {
        this.onStart = onStart;
        this.onSuccess = onSuccess;
        this.onFailed = onFailed;
        this.listener = listener;
        this.build = build;
        this.api = apiUrl + token;
    }

    @Override
    public void start() {
        String pic = "http://icon-park.com/imagefiles/loading7_gray.gif";
        String title = String.format("%s%s开始构建", build.getProject().getDisplayName(), build.getDisplayName());
        String content = String.format("项目[%s%s]开始构建", build.getProject().getDisplayName(), build.getDisplayName());

        String link = getBuildUrl();
        if (onStart) {
            logger.info("send link msg from " + listener.toString());
            sendMessage();
            //sendLinkMessage(link, content, title, pic);
        }

    }

    private String getBuildUrl() {
        if (jenkinsURL.endsWith("/")) {
            return jenkinsURL + build.getUrl();
        } else {
            return jenkinsURL + "/" + build.getUrl();
        }
    }

    @Override
    public void success() {
        String pic = "http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png";
        String title = String.format("%s%s构建成功", build.getProject().getDisplayName(), build.getDisplayName());
        String content = String.format("项目[%s%s]构建成功, summary:%s, duration:%s", build.getProject().getDisplayName(), build.getDisplayName(), build.getBuildStatusSummary().message, build.getDurationString());

        String link = getBuildUrl();
        logger.info(link);
        if (onSuccess) {
            logger.info("send link msg from " + listener.toString());
            sendMessage();
            //sendLinkMessage(link, content, title, pic);
        }
    }

    @Override
    public void failed() {
        String pic = "http://www.iconsdb.com/icons/preview/soylent-red/x-mark-3-xxl.png";
        String title = String.format("%s%s构建失败", build.getProject().getDisplayName(), build.getDisplayName());
        String content = String.format("项目[%s%s]构建失败, summary:%s, duration:%s", build.getProject().getDisplayName(), build.getDisplayName(), build.getBuildStatusSummary().message, build.getDurationString());

        String link = getBuildUrl();
        logger.info(link);
        if (onFailed) {
            logger.info("send link msg from " + listener.toString());
            //sendLinkMessage(link, content, title, pic);
        }
    }

    private void sendTextMessage(String msg) {

    }


    private String requestForToken() {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=wx9e2efe8d9730e164&corpsecret=Q_WIUw2b_ij5drP4Ir60p41JovaMMfYPfANRgGoUaFY";

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);
                Map<String, String> responseMap = JSON.parseObject(responseString, new TypeReference<Map<String, String>>(){});

                token = responseMap.get("access_token").toString();
                return token;

            } catch (IOException e) {
                logger.error("");
            } finally {
                response.close();
            }
        } catch (IOException e) {
            logger.error("");
        }
        return null;
    }

    private void sendMessage() {

        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
        if (token != null) {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(url);
            try {
                String body = "{\"touser\": \"jianjing\"," +
                        "\"msgtype\": \"text\"," +
                        "\"agentid\": \"1000007\"," +
                        "\"text\":" +
                        "{\"content\" : \"测试\"}," +
                        "\"safe\":0" +
                        "}";
                StringEntity params =new StringEntity(body);
                httppost.setEntity(params);
                CloseableHttpResponse response = httpclient.execute(httppost);
                try {
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity);
                    Map<String, String> responseMap = JSON.parseObject(responseString, new TypeReference<Map<String, String>>() {
                    });
                    String errcode = responseMap.get("errcode").toString();
                    if (!errcode.equals("0")) {
                        logger.error("发送消息失败");
                    }
                } catch (IOException e) {
                    logger.error("");
                } finally {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("");
            }
        }
    }

/**
    private void sendLinkMessage(String link, String msg, String title, String pic) {
        org.apache.http.client.HttpClient client = getHttpClient();
        PostMethod post = new PostMethod(api);

        JSONObject body = new JSONObject();
        body.put("msgtype", "link");


        JSONObject linkObject = new JSONObject();
        linkObject.put("text", msg);
        linkObject.put("title", title);
        linkObject.put("picUrl", pic);
        linkObject.put("messageUrl", link);

        body.put("link", linkObject);
        try {
            post.setRequestEntity(new StringRequestEntity(body.toJSONString(), "application/json", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("build request error", e);
        }
        try {
            client.executeMethod(post);
            logger.info(post.getResponseBodyAsString());
        } catch (IOException e) {
            logger.error("send msg error", e);
        }
        post.releaseConnection();
    }


    private HttpClient getHttpClient() {
        HttpClient client = new HttpClient();
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins != null && jenkins.proxy != null) {
            ProxyConfiguration proxy = jenkins.proxy;
            if (proxy != null && client.getHostConfiguration() != null) {
                client.getHostConfiguration().setProxy(proxy.name, proxy.port);
                String username = proxy.getUserName();
                String password = proxy.getPassword();
                // Consider it to be passed if username specified. Sufficient?
                if (username != null && !"".equals(username.trim())) {
                    logger.info("Using proxy authentication (user=" + username + ")");
                    client.getState().setProxyCredentials(AuthScope.ANY,
                            new UsernamePasswordCredentials(username, password));
                }
            }
        }
        return client;
    }*/
}
