package reedey.client;

import reedey.client.login.LoginScreen;
import reedey.client.widgets.MessageBox;
import reedey.shared.tracking.entity.User;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class ReEdey implements EntryPoint {
	
	public void onModuleLoad() {
		String debugStr = Window.Location.getParameter("debug");
		boolean debug = Boolean.valueOf(debugStr);
		if (debug) {
			GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				
				@Override
				public void onUncaughtException(Throwable e) {
					MessageBox.show("Error", e.getMessage());
				}
			});
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				RootPanel.get().add(new Image("img/ajax-loader.gif"));
				AppContext.get().getLoginService().loginFromSession(new AsyncCallback<User>() {

					@Override
					public void onFailure(Throwable caught) {
						RootPanel.get().clear();
						MessageBox.show("Error", "Internal server error");
					}

					@Override
					public void onSuccess(User user) {
						RootPanel.get().clear();
						if (user == null) {
							RootPanel.get().add(new LoginScreen());
						} else {
							AppContext.get().setUser(user);
							RootPanel.get().add(new MainWidget());
						}
					}
				});
				
			}
		});
	}
}
