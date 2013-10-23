package reedey.client.component.tracking;

import java.util.Date;

import reedey.client.AppContext;
import reedey.client.Msg;
import reedey.client.utils.AbstractAsyncCallback;
import reedey.shared.tracking.entity.HistoryItem;
import reedey.shared.tracking.entity.TrackingItem;
import reedey.shared.tracking.entity.TrackingStatus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TrackingListItem extends Composite {

	private static TrackingListItemUiBinder uiBinder = GWT
			.create(TrackingListItemUiBinder.class);

	interface TrackingListItemUiBinder extends
			UiBinder<Widget, TrackingListItem> {
	}
	
	private static final DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT);
	
	@UiField
	HorizontalPanel content;
	
	@UiField
	HTML nameLabel;
	
	@UiField
	FlowPanel currentPanel;
	
	@UiField
	HTML currentText;
	
	@UiField
	HTML lastChangedDate;
	
	@UiField
	ScrollPanel historyScrollPanel;
	
	@UiField
	VerticalPanel historyList;
	
	@UiField
	HTML deleteButton;
	
	@UiField
	HTML editButton;
	
	@UiField(provided=true)
	Msg msg = Msg.I;
	
	private TrackingItem current;

	public TrackingListItem(TrackingItem item) {
		current = item;
		initWidget(uiBinder.createAndBindUi(this));
		nameLabel.setHTML(SafeHtmlUtils.fromString(item.getName() != null ? item.getName() + " (" + item.getBarCode() + ")" : item.getBarCode()));
		HistoryItem lastItem = item.getItems().length > 0 ? item.getItems()[0] : emptyHistoryItem();
		currentText.setHTML(lastItem.getText());
		lastChangedDate.setHTML(format.format(lastItem.getDate()));
		
		TrackingStatus status = lastItem.getStatus();
		content.addStyleName(status.getStyle(false));
		historyScrollPanel.addStyleName(status.getStyle(true));
		for (int i = 1; i < item.getItems().length; i++) {
		    historyList.add(new HistoryItemWidget(item.getItems()[i]));
		}
	}
	
	@UiHandler("deleteButton")
	void onDeleteClick(ClickEvent e) {
		AppContext.get().getTrackingService().removeItem(
				current.getBarCode(), 
				new AbstractAsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						TrackingListItem.this.removeFromParent();
					}
				});
	}
	
	@UiHandler("editButton")
	void onEditClick(ClickEvent e) {
		Window.alert("Not implemented yet!");
	}

	private static final HistoryItem emptyHistoryItem() {
		return new HistoryItem(new Date(), "No information available for this item", TrackingStatus.NONE);
	}
}
