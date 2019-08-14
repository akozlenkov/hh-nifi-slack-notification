### NiFi bootstrap slack notification services

#### Build
mvn clean compile assembly:single

#### Install
cp -f target/hh-nifi-slack-notification-1.0-SNAPSHOT-jar-with-dependencies.jar $NIFI_HOME/lib/bootstrap/

#### Configure
*$NIFI_HOME/conf/bootstrap-notification-services.xml*
```
...
    <service>
        <id>slack-notification</id>
        <class>ru.hh.nifi.bootstrap.notification.slack.SlackNotificationService</class>
        <property name="Slack webhook url">https://slack/webhook/url</property>
        <property name="Slack channel">some_channel</property>
        <property name="Slack username">some_username</property>
    </service>
...
```

*$NIFI_HOME/conf/bootstrap.conf*
```
...
# XML File that contains the definitions of the notification services
notification.services.file=./conf/bootstrap-notification-services.xml

# In the case that we are unable to send a notification for an event, how many times should we retry?
notification.max.attempts=5

# Comma-separated list of identifiers that are present in the notification.services.file; which services should be used to notify when NiFi is started?
nifi.start.notification.services=slack-notification

# Comma-separated list of identifiers that are present in the notification.services.file; which services should be used to notify when NiFi is stopped?
nifi.stop.notification.services=slack-notification

# Comma-separated list of identifiers that are present in the notification.services.file; which services should be used to notify when NiFi dies?
nifi.dead.notification.services=slack-notification
...
```
