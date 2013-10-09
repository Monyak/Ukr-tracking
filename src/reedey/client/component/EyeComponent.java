package reedey.client.component;

import reedey.client.component.tracking.TrackingComponent;

import com.google.gwt.user.client.ui.Widget;

public interface EyeComponent {
	
	EyeComponent[] components = new EyeComponent[] {
			new TrackingComponent()
	};
	
	String getName();
	Widget getWidget();
}
