package reedey.server.tracking;

import reedey.shared.tracking.entity.TrackingStatus;

public final class StatusHandler {

    public static TrackingStatus getTrackingStatus(String message) {
        if (message.contains(Messages.getString("track.not.registered")) //$NON-NLS-1$
                || message.contains(Messages.getString("track.translated"))
                || message.contains(Messages.getString("track.incorrect")))  //$NON-NLS-1$
            return TrackingStatus.NONE;
        if (message.contains(Messages.getString("track.handed"))) //$NON-NLS-1$
            return TrackingStatus.DELIVERED;
        if (message.contains(Messages.getString("track.not.handed"))) //$NON-NLS-1$
            return TrackingStatus.DELIVERING;
        return TrackingStatus.PROCESSING;
    }
    
    public static boolean isOrigin(String message) {
        return message.contains(Messages.getString("track.translated"));  //$NON-NLS-1$
    }

    public static String getTrackingMessage(TrackingStatus status) {
        switch (status) {
        case DELIVERED:
            return Messages.getString("status.delivered"); //$NON-NLS-1$
        case DELIVERING:
            return Messages.getString("status.delivering"); //$NON-NLS-1$
        case NONE:
            return Messages.getString("status.none"); //$NON-NLS-1$
        case PROCESSING:
            return Messages.getString("status.processing"); //$NON-NLS-1$
        default:
            return null;
        }
    }
}
