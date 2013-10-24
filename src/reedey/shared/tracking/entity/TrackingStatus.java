package reedey.shared.tracking.entity;

import java.io.Serializable;

public enum TrackingStatus implements Serializable {
	NONE("tracking-red"), 
	PROCESSING("tracking-yellow"), 
	DELIVERING("tracking-green"), 
	DELIVERED("tracking-grey");
	
	private String style;
	TrackingStatus(String styleName) {
		this.style = styleName;
	}
	public String getStyle(boolean dark) {
		return style + (dark ? "-dark" : "");
	}
}
