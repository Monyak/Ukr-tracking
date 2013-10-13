package reedey.client.service;

import reedey.shared.tracking.entity.TrackingItem;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("tracking")
public interface TrackingService extends RemoteService {
	
	TrackingItem[] getItems(long userId);

	TrackingItem addItem(long userId, String barcode, String name);

	void removeItem(long userId, String barcode);
}
