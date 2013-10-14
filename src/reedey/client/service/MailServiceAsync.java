package reedey.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MailServiceAsync {
	
	void activateEmail(String email, AsyncCallback<Void> callback);
}
