package reedey.client;

import reedey.client.component.EyeComponent;
import reedey.client.login.LoginScreen;
import reedey.client.utils.AbstractAsyncCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MainWidget extends Composite {
	
	interface MainWidgetUiBinder extends UiBinder<Widget, MainWidget> {}
	private static MainWidgetUiBinder uiBinder = GWT.create(MainWidgetUiBinder.class);
	
	@UiField
	HorizontalPanel header;
	
	@UiField
	SimplePanel content;
	
	@UiField(provided=true)
	Msg msg = Msg.I;
	
	public MainWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		this.getElement().getStyle().clearPosition();

        this.initComponents(EyeComponent.components);
	}

	public void initComponents(EyeComponent[] components) {
		for (EyeComponent cmp : components) {
			Button button = new Button(cmp.getName());
			button.setStyleName("header-button");
			button.addClickHandler(new HeaderButtonHandler(cmp));
			header.add(button);
		}
		Button button = new Button(Msg.I.logout());
		button.setStyleName("header-button logout-button");
		button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AppContext.get().getLoginService().logout(AbstractAsyncCallback.VOID);
				RootPanel.get().clear();
				AppContext.get().setUser(null);
				RootPanel.get().add(new LoginScreen());
			}
		});
		header.add(button);
		if (components.length == 1)
			content.setWidget(components[0].getWidget());
	}
	
	private class HeaderButtonHandler implements ClickHandler {

		private EyeComponent component;

		private HeaderButtonHandler(EyeComponent cmp) {
			this.component = cmp;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			content.setWidget(component.getWidget());
		}
		
	}
}
