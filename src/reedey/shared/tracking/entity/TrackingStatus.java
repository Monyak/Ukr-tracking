package reedey.shared.tracking.entity;

import java.io.Serializable;

public enum TrackingStatus implements Serializable {
	NONE("tracking-red"),  //$NON-NLS-1$
	PROCESSING("tracking-yellow"),  //$NON-NLS-1$
	DELIVERING("tracking-green"),  //$NON-NLS-1$
	DELIVERED("tracking-grey"); //$NON-NLS-1$
	
	private String style;
	TrackingStatus(String styleName) {
		this.style = styleName;
	}
	public String getStyle(boolean dark) {
		return style + (dark ? "-dark" : ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
