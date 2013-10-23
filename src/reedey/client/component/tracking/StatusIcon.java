package reedey.client.component.tracking;

import reedey.shared.tracking.entity.TrackingStatus;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class StatusIcon extends SimplePanel {
	
	private CheckBox checkBox;
	private TrackingStatus status;

	public StatusIcon(TrackingStatus status, int flags) {
		this.status = status;
		HorizontalPanel panel = new HorizontalPanel();
		checkBox = new CheckBox();
		HTML html = new HTML();
		panel.add(checkBox);
		panel.add(html);
		checkBox.setValue((flags & (1 << status.ordinal())) > 0);
		html.addStyleName(status.getStyle(false));
		html.addStyleName("tracking-mail-color");
		this.setWidget(panel);
	}

	public TrackingStatus getStatus() {
		return status;
	}
	
	public boolean getValue() {
		return checkBox.getValue();
	}
}
