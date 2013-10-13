package reedey.client.component.tracking;

import reedey.client.AppContext;
import reedey.client.Msg;
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
	
	@UiField(provided=true)
	Msg msg = Msg.I;
	
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
			MessageBox.show("", msg.barcodeEmpty());
		} else {
			AppContext.get().getTrackingService().addItem(
					AppContext.get().getUser().getId(),
					barCodeTextBox.getText().trim(),
					nameTextBox.getText().trim().isEmpty() ? null : nameTextBox.getText().trim(),
					new AbstractAsyncCallback<TrackingItem>() {
						@Override
						public void onSuccess(TrackingItem result) {
							if (result == null) {
								MessageBox.show("", msg.itemExist());
							} else {
								if (trackingList.getWidgetCount() > 0)
									trackingList.insert(new TrackingListItem(result), 0);
								else trackingList.add(new TrackingListItem(result));
							}
						}
					});
		}
	}
	
	@UiHandler("settingsButton")
	void onSettingsClick(ClickEvent e) {
		Window.alert("Not implemented yet!");
	}

}
