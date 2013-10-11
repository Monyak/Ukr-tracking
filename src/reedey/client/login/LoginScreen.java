package reedey.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
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

    public LoginScreen() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    @UiHandler("button")
    void onLoginClick(ClickEvent e) {
        Window.alert("Login!");
    }

}
