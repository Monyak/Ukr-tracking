package reedey.client;


import reedey.client.service.LoginService;
import reedey.client.service.LoginServiceAsync;
import reedey.shared.tracking.entity.User;

import com.google.gwt.core.client.GWT;

public class AppContext {
private static AppContext instance;
	
	private LoginServiceAsync loginService;
	
	private User user;
	
	private AppContext() {}
	
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
