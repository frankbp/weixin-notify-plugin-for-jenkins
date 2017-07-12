package jenkins.plugin.weixinnotify;


import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;

import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;


/**
 * Created by jianjing on 2017/7/11.
 */
public class WeixinNotify extends Notifier {


	private String corpSecret;
	private String agentId;
	private String corpId;
	private String message;

	@DataBoundConstructor
	public WeixinNotify() {
		super();
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
		new WeixinServiceImpl(listener, build).sendTextcard();
		return true;
	}

	@Override
	public WeixinNotifierDescriptor getDescriptor() {

		return (WeixinNotifierDescriptor) super.getDescriptor();
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