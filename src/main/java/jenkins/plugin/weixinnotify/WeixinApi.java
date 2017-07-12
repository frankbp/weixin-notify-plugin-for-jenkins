package jenkins.plugin.weixinnotify;

/**
 * Created by jianjing on 2017/7/12.
 */
public class WeixinApi {
    protected static final String URL_SEND_MESSAGE = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";

    protected static final String URL_ACCESS_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
}
