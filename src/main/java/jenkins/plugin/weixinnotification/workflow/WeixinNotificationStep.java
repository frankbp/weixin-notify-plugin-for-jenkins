package jenkins.plugin.weixinnotification.workflow;


import hudson.Extension;
import hudson.Util;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import jenkins.plugin.weixinnotification.WeixinNotification;
import jenkins.plugin.weixinnotification.WeixinServiceImpl;

import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.inject.Inject;


/**
 * Created by jianjing on 2017/7/14.
 */
public class WeixinNotificationStep extends AbstractStepImpl {
    public String to;

    @CheckForNull
    public String status;

    @DataBoundConstructor
    public WeixinNotificationStep(String to) {
        super();
        this.to = to;
    }


    @Nonnull
    public String getTo() {
        return parseUsers(to);
    }

    public @CheckForNull String getStatus() {
        return status == null ? "" : status;
    }

    @DataBoundSetter
    public void setStatus(@CheckForNull String status) {
        this.status = Util.fixNull(status);
    }


    private String parseUsers(String toUser) {
        StringBuilder toUsers = new StringBuilder("");

        for(String user : toUser.split(",")) {
            if (user.contains("@")) {
                toUsers.append(user.split("@")[0]);
                toUsers.append('|');
            }
        }

        return toUsers.toString();
    }


    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(WeixinNotificationStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "weixinNotification";
        }

        @Override
        public String getDisplayName() {
            return "Send Wechat Notification";
        }
    }

    public static class WeixinNotificationStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

        @Inject
        transient WeixinNotificationStep step;

        @StepContextParameter
        transient TaskListener listener;

        @StepContextParameter
        transient Run<?, ?> run;

        private String corpId;
        private String agentSecret;
        private String agentId;

        @Override
        protected Void run() throws Exception {
            WeixinNotification.WeixinNotifierDescriptor weixinDesc = Jenkins.getInstance().getDescriptorByType(WeixinNotification.WeixinNotifierDescriptor.class);
            this.corpId = weixinDesc.getCorpId();
            this.agentSecret = weixinDesc.getAgentSecret();
            this.agentId = weixinDesc.getAgentId();

            new WeixinServiceImpl(listener, run, step.getTo(), step.getStatus()).sendNews();

            return null;
        }
    }
}
