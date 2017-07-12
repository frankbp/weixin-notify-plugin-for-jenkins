package jenkins.plugin.weixinnotification;


/**
 * Created by jianjing on 2017/7/12.
 */
public class WeixinMessageTemplate {

    protected static final String TITLE = "构建%s";

    protected static final String DESCRIPTION = "" +
            "<div>项目名称: %s</div>" +
            "<div>构建开始时间: %s</div>" +
            "<div>构建持续时间: %s</div>" +
            "<div>构建结果: %s</div>";
}
