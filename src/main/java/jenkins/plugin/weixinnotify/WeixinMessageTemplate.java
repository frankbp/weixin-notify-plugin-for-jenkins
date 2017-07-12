package jenkins.plugin.weixinnotify;

/**
 * Created by jianjing on 2017/7/12.
 */
public class WeixinMessageTemplate {

    protected static final String SEND_TEXT_TEMPLATE = String.format("{\n" +
            "   \"touser\" : \"%s\",\n" +
            "   \"msgtype\" : \"text\",\n" +
            "   \"agentid\" : %s,\n" +
            "   \"text\" : {\n" +
            "       \"content\" : \"%s\"\n" +
            "   },\n" +
            "   \"safe\":0\n" +
            "}");

    protected static final String SEND_IMAGE_TEMPLATE = String.format("{\n" +
            "   \"touser\" : \"%s\",\n" +
            "   \"msgtype\" : \"image\",\n" +
            "   \"agentid\" : %s,\n" +
            "   \"image\" : {\n" +
            "       \"media_id\" : \"%s\"\n" +
            "   },\n" +
            "   \"safe\":0\n" +
            "}");

    protected static final String SEND_VOICE_TEMPLATE = String.format("{\n" +
            "   \"touser\" : \"%s\",\n" +
            "   \"msgtype\" : \"voice\",\n" +
            "   \"agentid\" : %s,\n" +
            "   \"voice\" : {\n" +
            "       \"media_id\" : \"%s\"\n" +
            "   },\n" +
            "   \"safe\":0\n" +
            "}");

    protected static final String SEND_FILE_TEMPLATE = String.format("{\n" +
            "   \"touser\" : \"%s\",\n" +
            "   \"msgtype\" : \"file\",\n" +
            "   \"agentid\" : %s,\n" +
            "   \"file\" : {\n" +
            "       \"media_id\" : \"%s\"\n" +
            "   },\n" +
            "   \"safe\":0\n" +
            "}");

    protected static final String SEND_VIDEO_TEMPLATE = String.format("{\n" +
            "   \"touser\" : \"%s\",\n" +
            "   \"msgtype\" : \"video\",\n" +
            "   \"agentid\" : %s,\n" +
            "   \"video\" : {\n" +
            "       \"media_id\" : \"%s\",\n" +
            "       \"title\" : \"%s\",\n" +
            "       \"description\" : \"%s\"\n" +
            "   },\n" +
            "   \"safe\":0\n" +
            "}");

    protected static final String SEND_TEXTCARD_TEMPLATE = "{\n" +
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

    protected static final String TITLE = "构建结果";

    protected static final String DESCRIPTION = "<div class=\"gray\">" +
            "<table>" +
            "<tr><td>";
            //</div> <div class=\"normal\">恭喜你抽中iPhone 7一台，领奖码：xxxx</div><div class=\\\"highlight\\\">请于2016年10月10日前联系行政同事领取</div>";
}
