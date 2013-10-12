package reedey.client.service;

import reedey.shared.tracking.entity.TrackingItem;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TrackingServiceAsync {
	
	void getItems(long userId, AsyncCallback<TrackingItem[]> callback);

	void addItem(long userId, String barcode, String name, AsyncCallback<TrackingItem> callback);
}
