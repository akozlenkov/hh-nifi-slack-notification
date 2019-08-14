package ru.hh.nifi.bootstrap.notification.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.model.Attachment;
import com.github.seratch.jslack.api.webhook.Payload;
import org.apache.nifi.bootstrap.notification.AbstractNotificationService;
import org.apache.nifi.bootstrap.notification.NotificationContext;
import org.apache.nifi.bootstrap.notification.NotificationFailedException;
import org.apache.nifi.bootstrap.notification.NotificationType;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.components.PropertyDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SlackNotificationService extends AbstractNotificationService {
    private static final String SUCCESS_COLOR = "#36A64F";
    private static final String WARNING_COLOR = "#F5A742";
    private static final String DANGER_COLOR = "#F54B42";
    private static final String DEFAULT_COLOR = "#439FE0";

    private static final PropertyDescriptor SLACK_WEBHOOK_URL = new PropertyDescriptor.Builder()
            .name("Slack webhook url")
            .addValidator(StandardValidators.URL_VALIDATOR)
            .required(true)
            .build();

    private static final PropertyDescriptor SLACK_CHANNEL = new PropertyDescriptor.Builder()
            .name("Slack channel")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .required(false)
            .build();

    private static final PropertyDescriptor SLACK_USERNAME = new PropertyDescriptor.Builder()
            .name("Slack username")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .required(false)
            .defaultValue("nifi")
            .build();

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        final List<PropertyDescriptor> properties = new ArrayList<>();
        properties.add(SLACK_WEBHOOK_URL);
        properties.add(SLACK_CHANNEL);
        properties.add(SLACK_USERNAME);
        return properties;
    }

    @Override
    public void notify(NotificationContext notificationContext, NotificationType notificationType, String subject, String message) throws NotificationFailedException {
        Slack slack = Slack.getInstance();

        try {
            String url =  notificationContext.getProperty(SLACK_WEBHOOK_URL).getValue();

            Payload.PayloadBuilder payload = Payload.builder();
            if (notificationContext.getProperty(SLACK_CHANNEL).isSet()) {
                payload.channel(notificationContext.getProperty(SLACK_CHANNEL).getValue());
            }

            if (notificationContext.getProperty(SLACK_USERNAME).isSet()) {
                payload.username(notificationContext.getProperty(SLACK_USERNAME).getValue());
            }

            Attachment.AttachmentBuilder attachment = Attachment.builder();

            attachment.text(message).fallback(subject);

            switch (notificationType) {
                case NIFI_STARTED:
                    attachment.color(SUCCESS_COLOR);
                    break;
                case NIFI_DIED:
                    attachment.color(WARNING_COLOR);
                    break;
                case NIFI_STOPPED:
                    attachment.color(DANGER_COLOR);
                    break;
                default:
                    attachment.color(DEFAULT_COLOR);
                    break;
            }

            payload.attachments(Arrays.asList(attachment.build()));

            slack.send(url, payload.build());
        } catch (Exception e) {
            throw new NotificationFailedException("Failed to send Slack Notification", e);
        }
    }
}
