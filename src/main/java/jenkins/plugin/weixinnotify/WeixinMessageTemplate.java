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

    protected static final String DESCRIPTION = "项目名称: %s\n构建开始时间: %s\n构建持续时间: %s\n构建结果: %s";
}
