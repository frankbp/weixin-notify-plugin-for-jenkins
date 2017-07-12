package jenkins.plugin.weixinnotification;

/**
 * Created by jianjing on 2017/7/11.
 */
public interface WeixinService {
        void sendText(String text);

        void sendImage(String imageId);

        void sendVoice(String voiceId);

        void sendVideo(String videoId);

        void sendFile(String fileId);

        void sendTextcard();

        void sendNews();
}
