package reedey.client.service;

import reedey.shared.tracking.entity.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	
	void loginFromSession(AsyncCallback<User> callback);
	
	void login(String login, String password, AsyncCallback<User> callback);

	void createUser(String login, String password, AsyncCallback<User> callback);

	void logout(AsyncCallback<Void> callback);
}
