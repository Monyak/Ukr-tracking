package reedey.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface Msg extends Messages {

	public static Msg I = GWT.create(Msg.class);
	
	@DefaultMessage("Error")
	String error();
	
	@DefaultMessage("Internal server error")
	String internalError();
	
	@DefaultMessage("Choose component")
	String chooseComponent();
	
	@DefaultMessage("Sign in")
	String signin();

	@DefaultMessage("Login")
	String login();

	@DefaultMessage("Password")
	String password();

	@DefaultMessage("Register")
	String register();

	@DefaultMessage("User with this name already exists")
	String userExists();

	@DefaultMessage("Invalid username or password")
	String invalidLogin();

	@DefaultMessage("Login or password should not be blank")
	String loginNotBlank();

	@DefaultMessage("Logout")
	String logout();

	@DefaultMessage("Tracking")
	String tracking();

	@DefaultMessage("Barcode")
	String barcode();

	@DefaultMessage("Name")
	String name();

	@DefaultMessage("Add")
	String add();

	@DefaultMessage("Settings")
	String settings();

	@DefaultMessage("Remove")
	String remove();

	@DefaultMessage("Edit")
	String edit();
	
	@DefaultMessage("Bar code should not be empty")
	String barcodeEmpty();
	
	@DefaultMessage("This item already exists")
	String itemExist();
	
	@DefaultMessage("Save new e-mail")
	String saveMail();
	
	@DefaultMessage("Confirmation message was sent. Please check your e-mail.")
	String confirmationMessageSent();
	
	@DefaultMessage("Your e-mail address for notifications")
	String yourMail();

	@DefaultMessage("E-mail should not be empty")
	String mailEmpty();
}