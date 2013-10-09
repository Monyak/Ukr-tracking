package reedey.client.component.tracking;

import reedey.shared.tracking.entity.TrackingItem;

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
	HTML nameLabel;
	
	@UiField
	FlowPanel currentPanel;
	
	@UiField
	HTML currentText;
	
	@UiField
	HTML lastChangedDate;
	
	@UiField
	VerticalPanel historyList;
	
	@UiField
	HTML deleteButton;
	
	@UiField
	HTML editButton;
	

	public TrackingListItem(TrackingItem item) {
		initWidget(uiBinder.createAndBindUi(this));
		nameLabel.setHTML(SafeHtmlUtils.fromString(item.getName() != null ? item.getName() : item.getBarCode()));
		currentText.setHTML(SafeHtmlUtils.fromString(item.getItems()[0].getText()));
		lastChangedDate.setHTML(format.format(item.getItems()[0].getDate()));
		currentPanel.addStyleName(item.getItems()[0].getStatus().getStyle());
	}
	
	@UiHandler("deleteButton")
	void onDeleteClick(ClickEvent e) {
		Window.alert("Remove!");
	}
	
	@UiHandler("editButton")
	void onEditClick(ClickEvent e) {
		Window.alert("Edit!");
	}

}
