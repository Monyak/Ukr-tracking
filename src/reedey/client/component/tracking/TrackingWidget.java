package reedey.client.component.tracking;

import java.util.Date;

import reedey.shared.tracking.entity.HistoryItem;
import reedey.shared.tracking.entity.TrackingItem;
import reedey.shared.tracking.entity.TrackingStatus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TrackingWidget extends Composite {

	private static TrackingWidgetUiBinder uiBinder = GWT
			.create(TrackingWidgetUiBinder.class);

	interface TrackingWidgetUiBinder extends UiBinder<Widget, TrackingWidget> {
	}
	
	@UiField
	TextBox barCodeTextBox;
	
	@UiField
	TextBox nameTextBox;

	@UiField
	HTML addButton;

	@UiField
	HTML settingsButton;
	
	@UiField
	VerticalPanel trackingList;

	public TrackingWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		loadItems();
	}

	private void loadItems() {
		TrackingItem[] items = new TrackingItem[] {
			new TrackingItem("RC12312314CH", "myItem", new HistoryItem[]{
				new HistoryItem(new Date(), "Delivered", TrackingStatus.DELIVERING),
				new HistoryItem(new Date(new Date().getTime() - 24*60*60*1000*5), "Processing", TrackingStatus.PROCESSING),
				new HistoryItem(new Date(new Date().getTime() - 24*60*60*1000*10), "Got", TrackingStatus.NONE)
			}),
			new TrackingItem("RC12312314CH", null, new HistoryItem[]{
					new HistoryItem(new Date(), "Processing", TrackingStatus.PROCESSING),
					new HistoryItem(new Date(new Date().getTime() - 24*60*60*1000*7), "Got", TrackingStatus.NONE)
			})
		};
		for (TrackingItem item : items) 
			trackingList.add(new TrackingListItem(item));
	}

	@UiHandler("addButton")
	void onAddClick(ClickEvent e) {
		Window.alert("Add!");
	}
	
	@UiHandler("settingsButton")
	void onSettingsClick(ClickEvent e) {
		Window.alert("Settings!");
	}

}
