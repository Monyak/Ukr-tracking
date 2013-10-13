package reedey.client.login;

import reedey.client.AppContext;
import reedey.client.MainWidget;
import reedey.client.Msg;
import reedey.client.utils.AbstractAsyncCallback;
import reedey.shared.tracking.entity.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginScreen extends Composite {

    private static LoginScreenUiBinder uiBinder = GWT.create(LoginScreenUiBinder.class);

    interface LoginScreenUiBinder extends UiBinder<Widget, LoginScreen> {
    }
    
    @UiField
    TextBox login;
    
    @UiField
    PasswordTextBox password;
    
    @UiField
    Button button;
    
    @UiField
    CheckBox register;
    
    @UiField
    HTML error;
    
    @UiField(provided=true)
	Msg msg = Msg.I;

    public LoginScreen() {
        initWidget(uiBinder.createAndBindUi(this));
        hideError();
    }
    
    @UiHandler("button")
    void onLoginClick(ClickEvent e) {
    	hideError();
        if (validateFields())
        	return;
        if (register.getValue()) {
        	// register
        	AppContext.get().getLoginService().createUser(
        			login.getText().trim(), 
        			password.getText().trim(),
        			new AbstractAsyncCallback<User>() {
						@Override
						public void onSuccess(User user) {
							if (user == null) {
								showError(msg.userExists());
							} else {
								AppContext.get().setUser(user);
								RootPanel.get().clear();
								RootPanel.get().add(new MainWidget());
							}
						}
					});
        } else {
        	// login
        	AppContext.get().getLoginService().login(
        			login.getText().trim(), 
        			password.getText().trim(),
        			new AbstractAsyncCallback<User>() {
						@Override
						public void onSuccess(User user) {
							if (user == null) {
								showError(msg.invalidLogin());
							} else {
								AppContext.get().setUser(user);
								RootPanel.get().clear();
								RootPanel.get().add(new MainWidget());
							}
						}
					});
        }
    }
    
    private boolean validateFields() {
    	if (login.getText().trim().isEmpty() || password.getText().trim().isEmpty()) {
    		showError(msg.loginNotBlank());
    		return true;
    	}
    	return false;
    }

    private void showError(String message) {
    	error.setText(message);
    	error.setVisible(true);
    }
    
    private void hideError() {
    	error.setVisible(false);
    }
}
