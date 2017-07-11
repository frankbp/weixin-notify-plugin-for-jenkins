package jenkins.plugin.weixinnotify;

/**
 * Created by jianjing on 2017/7/11.
 */
public interface WeixinService {
        void start();

        void success();

        void failed();
}
