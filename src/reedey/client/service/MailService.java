package reedey.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("mail")
public interface MailService extends RemoteService {
	
	void activateEmail(String email);
}
