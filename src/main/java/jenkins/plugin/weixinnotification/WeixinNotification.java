package jenkins.plugin.weixinnotification;


import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;

import hudson.tasks.*;

import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;


/**
 * Created by jianjing on 2017/7/11.
 */
public class WeixinNotification extends Notifier implements SimpleBuildStep {

	public String toUser;
	public String buildStatus;

	@DataBoundConstructor
	public WeixinNotification(String toUser, String buildStatus) {
		super();
		this.toUser = toUser;
		this.buildStatus = buildStatus;
	}

	public WeixinNotification() {
		super();
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	// Build from plugin
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
		listener.getLogger().println(toUser);
	    new WeixinServiceImpl(listener, build, toUser).sendNews();
		return true;
	}

	@Override
	public WeixinNotifierDescriptor getDescriptor() {

		return (WeixinNotifierDescriptor) super.getDescriptor();
	}

	// Build from pipeline
	@Override
	public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws InterruptedException, IOException {
		taskListener.getLogger().println(toUser);
	    new WeixinServiceImpl(taskListener, run, toUser, buildStatus).sendNews();
	}

	@Extension
	public static class WeixinNotifierDescriptor extends BuildStepDescriptor<Publisher> {

		private String corpId;
		private String agentSecret;
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
			return "Wechat Notification";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
			corpId = formData.getString("corpId");
			agentSecret = formData.getString("agentSecret");
			agentId = formData.getString("agentId");
			save();
			return super.configure(req, formData);
		}

		public String getCorpId() {
			return corpId;
		}

		public String getAgentSecret() {
			return agentSecret;
		}

		public String getAgentId() {
			return agentId;
		}
	}
}