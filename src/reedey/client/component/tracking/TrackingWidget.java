package reedey.client.component.tracking;

import reedey.client.AppContext;
import reedey.client.utils.AbstractAsyncCallback;
import reedey.client.widgets.MessageBox;
import reedey.shared.tracking.entity.TrackingItem;

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
		AppContext.get().getTrackingService().getItems(
				AppContext.get().getUser().getId(),
				new AbstractAsyncCallback<TrackingItem[]>() {
					@Override
					public void onSuccess(TrackingItem[] items) {
						for (TrackingItem item : items) 
							trackingList.add(new TrackingListItem(item));
					}
				});
	}

	@UiHandler("addButton")
	void onAddClick(ClickEvent e) {
		if (barCodeTextBox.getText().trim().isEmpty()) {
			MessageBox.show("", "Bar code should not be empty");
		} else {
			AppContext.get().getTrackingService().addItem(
					AppContext.get().getUser().getId(),
					barCodeTextBox.getText().trim(),
					nameTextBox.getText().trim().isEmpty() ? null : nameTextBox.getText().trim(),
					new AbstractAsyncCallback<TrackingItem>() {
						@Override
						public void onSuccess(TrackingItem result) {
							if (result == null) {
								MessageBox.show("", "This item already exists");
							} else {
								trackingList.add(new TrackingListItem(result));
							}
						}
					});
		}
	}
	
	@UiHandler("settingsButton")
	void onSettingsClick(ClickEvent e) {
		Window.alert("Settings!");
	}

}
