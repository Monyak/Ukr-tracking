package reedey.client;

import reedey.client.service.LoginService;
import reedey.client.service.LoginServiceAsync;
import reedey.client.service.MailService;
import reedey.client.service.MailServiceAsync;
import reedey.client.service.TrackingService;
import reedey.client.service.TrackingServiceAsync;
import reedey.shared.tracking.entity.User;

import com.google.gwt.core.client.GWT;

public class AppContext {
	private static AppContext instance;

	private LoginServiceAsync loginService;
	private TrackingServiceAsync trackingService;
	private MailServiceAsync mailService;

	private User user;

	private AppContext() {
	}

	public static AppContext get() {
		if (instance == null)
			instance = new AppContext();
		return instance;
	}

	public LoginServiceAsync getLoginService() {
		if (loginService == null)
			loginService = GWT.create(LoginService.class);
		return loginService;
	}

	public TrackingServiceAsync getTrackingService() {
		if (trackingService == null)
			trackingService = GWT.create(TrackingService.class);
		return trackingService;
	}

	public MailServiceAsync getMailService() {
		if (mailService == null)
			mailService = GWT.create(MailService.class);
		return mailService;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
