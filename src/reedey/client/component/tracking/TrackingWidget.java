package reedey.client.component.tracking;

import reedey.client.AppContext;
import reedey.client.Msg;
import reedey.client.utils.AbstractAsyncCallback;
import reedey.client.widgets.MessageBox;
import reedey.shared.tracking.entity.TrackingItem;
import reedey.shared.tracking.entity.TrackingStatus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
	
	@UiField
	TextBox mailTextBox;
	
	@UiField
	HTML mailSaveButton;
	
	@UiField
	HorizontalPanel colorList;
	
	StatusIcon[] statuses;
	
	@UiField(provided=true)
	Msg msg = Msg.I;
	
	public TrackingWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		initWidgets();
		loadItems();
	}


	private void loadItems() {
		AppContext.get().getTrackingService().getItems(
				new AbstractAsyncCallback<TrackingItem[]>() {
					@Override
					public void onSuccess(TrackingItem[] items) {
						for (TrackingItem item : items) 
							trackingList.add(new TrackingListItem(item));
					}
				});
	}
	
	private void initWidgets() {
		mailTextBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				if (mailTextBox.getText().equals(msg.yourMail())) {
					mailTextBox.removeStyleName("tracking-mail-blur");
					mailTextBox.setText("");
				}
			}
		});
		mailTextBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (mailTextBox.getText().isEmpty()) {
					mailTextBox.addStyleName("tracking-mail-blur");
					mailTextBox.setText(msg.yourMail());
				}
			}
		});
		String email = AppContext.get().getUser().getEmail();
		mailTextBox.setText(email == null ? msg.yourMail() : email);
		if (email == null)
			mailTextBox.addStyleName("tracking-mail-blur");
		
		int flags = AppContext.get().getUser().getFlags();
		TrackingStatus[] values = TrackingStatus.values();
		statuses = new StatusIcon[values.length];
		int i = 0;
		for (TrackingStatus status : values)
			colorList.add(statuses[i++] = new StatusIcon(status, flags));
	}

	@UiHandler("addButton")
	void onAddClick(ClickEvent e) {
		if (barCodeTextBox.getText().trim().isEmpty()) {
			MessageBox.show("", msg.barcodeEmpty());
		} else {
			AppContext.get().getTrackingService().addItem(
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
		boolean isVisible = mailTextBox.isVisible();
		mailTextBox.setVisible(!isVisible);
		mailSaveButton.setVisible(!isVisible);
		if (isVisible)
			settingsButton.removeStyleName("tracking-mail-blur");
		else settingsButton.addStyleName("tracking-mail-blur");
	}
	
	@UiHandler("mailSaveButton")
	void onMailSaveClick(ClickEvent e) {
		String text = mailTextBox.getText().trim();
		if (text.isEmpty() || text.equals(msg.yourMail())) {
			MessageBox.show("", msg.mailEmpty());
			return;
		}
		if (text.equals(AppContext.get().getUser().getEmail())) {
			return;
		}
		mailSaveButton.addStyleName("tracking-mail-blur");
		AppContext.get().getMailService().activateEmail(text, 
				new AbstractAsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						mailSaveButton.removeStyleName("tracking-mail-blur");
						MessageBox.show("", msg.confirmationMessageSent());
					}
					@Override
					public String errorMessage() {
						mailSaveButton.removeStyleName("tracking-mail-blur");
						return super.errorMessage();
					}
				});
	}

}
