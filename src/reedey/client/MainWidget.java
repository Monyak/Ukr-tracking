package reedey.client;

import reedey.client.component.EyeComponent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MainWidget extends Composite {
	
	interface MainWidgetUiBinder extends UiBinder<Widget, MainWidget> {}
	private static MainWidgetUiBinder uiBinder = GWT.create(MainWidgetUiBinder.class);
	
	@UiField
	HorizontalPanel header;
	
	@UiField
	SimplePanel content;
	
	public MainWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		this.getElement().getStyle().clearPosition();
	}

	public void initComponents(EyeComponent[] components) {
		for (EyeComponent cmp : components) {
			Button button = new Button(cmp.getName());
			button.setStyleName("header-button");
			button.addClickHandler(new HeaderButtonHandler(cmp));
			header.add(button);
		}
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
