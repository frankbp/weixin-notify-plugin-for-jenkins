package jenkins.plugin.weixinnotify;


import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Created by jianjing on 2017/7/11.
 */
public class WeixinNotify extends Notifier {

	private String accessToken;

	private boolean onStart;

	private boolean onSuccess;

	private boolean onFailed;


	public boolean isOnStart() {
		return onStart;
	}

	public boolean isOnSuccess() {
		return onSuccess;
	}

	public boolean isOnFailed() {
		return onFailed;
	}

	public String getAccessToken() {
		return accessToken;
	}

	@DataBoundConstructor
	public WeixinNotify(String accessToken, boolean onStart, boolean onSuccess, boolean onFailed, String jenkinsURL) {
		super();
		this.accessToken = accessToken;
		this.onStart = true;
		this.onSuccess = true;
		this.onFailed = true;
	}

	public WeixinService newWeixinService(AbstractBuild build, TaskListener listener) {
		return new WeixinServiceImpl(accessToken, onStart, onSuccess, onFailed, listener, build);
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
		return true;
	}


	@Override
	public WeixinNotifierDescriptor getDescriptor() {
		return (WeixinNotifierDescriptor) super.getDescriptor();
	}

	@Extension
	public static class WeixinNotifierDescriptor extends BuildStepDescriptor<Publisher> {


		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "微信通知";
		}

		public String getDefaultURL() {
			Jenkins instance = Jenkins.getInstance();
			assert instance != null;
			if(instance.getRootUrl() != null){
				return instance.getRootUrl();
			}else{
				return "";
			}
		}
	}
}