package reedey.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MessageBox {
	public static void show(final String header, final String content) {
        final DialogBox box = new DialogBox();
        final VerticalPanel panel = new VerticalPanel();
        box.setText(header);
        Label label = new Label(content);
        label.setStyleName("message-label");
        panel.add(label);
        final Button buttonClose = new Button("OK",new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                box.hide();
                box.removeFromParent();
            }
        });
        buttonClose.setStyleName("");
        buttonClose.setWidth("90px");
        panel.add(buttonClose);
        panel.setCellHorizontalAlignment(buttonClose, HasAlignment.ALIGN_RIGHT);
        box.add(panel);
        box.show();
        box.center();
    }
	
	public static void show(final String header, final String content, final ClickHandler handler) {
        final DialogBox box = new DialogBox();
        final VerticalPanel panel = new VerticalPanel();
        box.setText(header);
        Label label = new Label(content);
        label.setStyleName("message-label");
        panel.add(label);
        final Button buttonClose = new Button("OK",new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                box.hide();
                box.removeFromParent();
                handler.onClick(event);
            }
        });
        buttonClose.setStyleName("");
        buttonClose.setWidth("90px");
        panel.add(buttonClose);
        panel.setCellHorizontalAlignment(buttonClose, HasAlignment.ALIGN_RIGHT);
        box.add(panel);
        box.show();
        box.center();
    }
}
