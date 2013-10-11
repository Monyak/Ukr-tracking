package reedey.client.service;

import reedey.shared.tracking.entity.User;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {
	User loginFromSession();

	User login(String login, String password);

	User createUser(String login, String password);

	void logout();

}
