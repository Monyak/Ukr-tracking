package reedey.client.component.tracking;

import reedey.client.Msg;
import reedey.client.component.EyeComponent;

import com.google.gwt.user.client.ui.Widget;

public class TrackingComponent implements EyeComponent {

	private Widget widget;
	
	@Override
	public String getName() {
		return Msg.I.tracking();
	}

	@Override
	public Widget getWidget() {
		return widget == null ? (widget = new TrackingWidget()) : widget;
	}

}
