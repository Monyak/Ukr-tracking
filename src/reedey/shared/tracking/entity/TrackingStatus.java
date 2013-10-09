package reedey.shared.tracking.entity;

public enum TrackingStatus {
	NONE("tracking-red"), 
	PROCESSING("tracking-yellow"), 
	DELIVERING("tracking-green"), 
	DELIVERED("tracking-grey");
	
	private String style;
	TrackingStatus(String styleName) {
		this.style = styleName;
	}
	public String getStyle() {
		return style;
	}
}
