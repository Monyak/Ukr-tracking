package reedey.client.component.tracking;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TrackingListItem extends Composite {

	private static TrackingListItemUiBinder uiBinder = GWT
			.create(TrackingListItemUiBinder.class);

	interface TrackingListItemUiBinder extends
			UiBinder<Widget, TrackingListItem> {
	}

	public TrackingListItem() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
