package top.trumeet.common.event.type;

import top.trumeet.common.event.Event;

/**
 * Created by Trumeet on 2018/2/7.
 */

public class TypeFactory {
    public static EventType create (Event event, String pkg) {
        switch (event.getType()) {
            case Event.Type.Command:
                return new CommandType(event.getInfo(),
                        pkg, event.getPayload());
            case Event.Type.Notification:
                return new NotificationType(event.getInfo(), pkg, event.getNotificationTitle(),
                        event.getNotificationSummary(), event.getPayload());
            case Event.Type.Registration:
                return new RegistrationType(event.getInfo(),
                        pkg, event.getPayload());
            case Event.Type.RegistrationResult:
                return new RegistrationResultType(event.getInfo(),
                        pkg, event.getPayload());
            default:
                return new UnknownType(event.getType(), event.getInfo(), pkg, event.getPayload());
        }
    }
}
