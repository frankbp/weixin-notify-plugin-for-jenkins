package jenkins.plugin.weixinnotification;

/**
 * Created by jianjing on 2017/7/12.
 */
public class WeixinApi {
    protected static final String URL_SEND_MESSAGE = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";

    protected static final String URL_ACCESS_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";

    protected static final String ERROR_MESSAGE_INVALID_TOKEN = "invalid access_token";
    protected static final String ERROR_MESSAGE_EXPIRED_TOKEN = "access_token expired";

    protected static final String TEMPLATE_SEND_TEXT = "{\n" +
            "   \"touser\" : \"%s\",\n" +
            "   \"msgtype\" : \"text\",\n" +
            "   \"agentid\" : %s,\n" +
            "   \"text\" : {\n" +
            "       \"content\" : \"%s\"\n" +
            "   },\n" +
            "   \"safe\":0\n" +
            "}";

    protected static final String TEMPLATE_SEND_IMAGE = "{\n" +
            "   \"touser\" : \"%s\",\n" +
            "   \"msgtype\" : \"image\",\n" +
            "   \"agentid\" : %s,\n" +
            "   \"image\" : {\n" +
            "       \"media_id\" : \"%s\"\n" +
            "   },\n" +
            "   \"safe\":0\n" +
            "}";

    protected static final String TEMPLATE_SEND_VOICE = "{\n" +
            "   \"touser\" : \"%s\",\n" +
            "   \"msgtype\" : \"voice\",\n" +
            "   \"agentid\" : %s,\n" +
            "   \"voice\" : {\n" +
            "       \"media_id\" : \"%s\"\n" +
            "   },\n" +
            "   \"safe\":0\n" +
            "}";

    protected static final String TEMPLATE_SEND_FILE = "{\n" +
            "   \"touser\" : \"%s\",\n" +
            "   \"msgtype\" : \"file\",\n" +
            "   \"agentid\" : %s,\n" +
            "   \"file\" : {\n" +
            "       \"media_id\" : \"%s\"\n" +
            "   },\n" +
            "   \"safe\":0\n" +
            "}";

    protected static final String TEMPLATE_SEND_VIDEO = "{\n" +
            "   \"touser\" : \"%s\",\n" +
            "   \"msgtype\" : \"video\",\n" +
            "   \"agentid\" : %s,\n" +
            "   \"video\" : {\n" +
            "       \"media_id\" : \"%s\",\n" +
            "       \"title\" : \"%s\",\n" +
            "       \"description\" : \"%s\"\n" +
            "   },\n" +
            "   \"safe\":0\n" +
            "}";

    protected static final String TEMPLATE_SEND_TEXTCARD = "{\n" +
            "   \"touser\" : \"%s\",\n" +
            "   \"msgtype\" : \"textcard\",\n" +
            "   \"agentid\" : %s,\n" +
            "   \"textcard\" : {\n" +
            "       \"title\" : \"%s\",\n" +
            "       \"description\" : \"%s\",\n" +
            "       \"url\" : \"%s\"\n" +
            "   },\n" +
            "   \"safe\":0\n" +
            "}";

    protected static final String TEMPLATE_SEND_NEWS = "{\n" +
            "   \"touser\" : \"%s\",\n" +
            "   \"msgtype\" : \"news\",\n" +
            "   \"agentid\" : %s,\n" +
            "   \"news\" : {\n" +
            "       \"articles\" : [" +
            "           {" +
            "             \"title\" : \"%s\",\n" +
            "             \"description\" : \"%s\",\n" +
            "             \"url\" : \"%s\",\n" +
            "             \"picurl\" : \"%s\"\n" +
            "           },\n" +
            "         ]" +
            "     }" +
            "   \"safe\":0\n" +
            "}";
}
