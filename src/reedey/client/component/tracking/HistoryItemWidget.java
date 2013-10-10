package reedey.client.component.tracking;

import reedey.shared.tracking.entity.HistoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class HistoryItemWidget extends Composite {

    private static HistoryItemWidgetUiBinder uiBinder = GWT.create(HistoryItemWidgetUiBinder.class);

    interface HistoryItemWidgetUiBinder extends UiBinder<Widget, HistoryItemWidget> {
    }
    
    private static final DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT);
    
    @UiField
    HTML label;

    public HistoryItemWidget(HistoryItem item) {
        initWidget(uiBinder.createAndBindUi(this));
        this.addStyleName(item.getStatus().getStyle(false));
        label.setHTML("<b>" + format.format(item.getDate()) + "</b>: " + item.getText());
        label.setTitle(format.format(item.getDate()) + ": " + item.getText());
        label.setWidth(Window.getClientWidth() / 3 + "px");
    }

}
