package jenkins.plugin.weixinnotify;


import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;

import hudson.tasks.*;

import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;


/**
 * Created by jianjing on 2017/7/11.
 */
public class WeixinNotify extends Notifier implements SimpleBuildStep {

	private String toUser;

	@DataBoundConstructor
	public WeixinNotify(String toUser) {
		super();
		this.toUser = toUser;
	}

	@DataBoundSetter
    public void setToUser(String toUser) {
	    this.toUser = toUser;
    }

	public WeixinNotify() {
		super();
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
		listener.getLogger().println(toUser);
	    new WeixinServiceImpl(listener, build, toUser).sendTextcard();
		return true;
	}

	@Override
	public WeixinNotifierDescriptor getDescriptor() {

		return (WeixinNotifierDescriptor) super.getDescriptor();
	}

	@Override
	public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws InterruptedException, IOException {
		taskListener.getLogger().println(toUser);
	    new WeixinServiceImpl(taskListener, run, toUser).sendTextcard();
	}

	@Extension
	public static class WeixinNotifierDescriptor extends BuildStepDescriptor<Publisher> {

		private String corpId;
		private String corpSecret;
		private String agentId;

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		public WeixinNotifierDescriptor() {
			load();
		}

		@Override
		public String getDisplayName() {

			return "微信通知";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
			corpId = formData.getString("corpId");
			corpSecret = formData.getString("corpSecret");
			agentId = formData.getString("agentId");
			save();
			return super.configure(req, formData);
		}

		public String getCorpId() {
			return corpId;
		}

		public String getCorpSecret() {
			return corpSecret;
		}

		public String getAgentId() {
			return agentId;
		}
	}
}