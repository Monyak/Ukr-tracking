package reedey.client.utils;

import reedey.client.login.LoginScreen;
import reedey.client.widgets.MessageBox;
import reedey.shared.exceptions.SessionExpiredException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class AbstractAsyncCallback<T> implements AsyncCallback<T> {

	public static AbstractAsyncCallback<Void> VOID = new AbstractAsyncCallback<Void>() {
		@Override
		public void onSuccess(Void result) {}
	};
	
	@Override
	public void onFailure(Throwable caught) {
		if (caught instanceof SessionExpiredException) {
			MessageBox.show("Error", "Session expired", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					RootPanel.get().clear();
					RootPanel.get().add(new LoginScreen());
				}
			});
		} else {
			String errorMessage = errorMessage();
			MessageBox.show("Error", errorMessage != null ? errorMessage
					: "Internal server error <br/>" + caught.getMessage());
		}
	}
	
	public String errorMessage() {
		return null;
	}

}
