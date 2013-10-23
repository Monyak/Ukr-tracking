package reedey.client.service;

import reedey.shared.tracking.entity.TrackingItem;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TrackingServiceAsync {
	
	void getItems(AsyncCallback<TrackingItem[]> callback);

	void addItem(String barcode, String name, AsyncCallback<TrackingItem> callback);
	
	void removeItem(String barcode, AsyncCallback<Void> callback);
	
	void updateNotificationFlags(int flags, AsyncCallback<Void> callback);
}
