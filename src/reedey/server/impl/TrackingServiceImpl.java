package reedey.server.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import reedey.client.service.TrackingService;
import reedey.server.tracking.EMSAdapter;
import reedey.shared.tracking.entity.HistoryItem;
import reedey.shared.tracking.entity.TrackingItem;
import reedey.shared.tracking.entity.TrackingStatus;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TrackingServiceImpl extends RemoteServiceServlet implements TrackingService {

	private static final long serialVersionUID = -8002177620880133394L;

	private static final String USER_ITEM_TABLE = "UserTracking";
	private static final String HISTORY_ITEM_TABLE = "Tracking";

	private static final String USER_ID = "user_id";
	private static final String BARCODE = "barcode";
	private static final String NAME = "name";
	private static final String MESSAGE = "message";
	private static final String DATE = "tracking_date";
	
	private static final EMSAdapter adapter = new EMSAdapter();
	
	@Override
	public TrackingItem[] getItems(long userId) {
		/*TrackingItem[] items = new TrackingItem[] {
				new TrackingItem("RC12312314CH", "myItem", new HistoryItem[]{
					new HistoryItem(new Date(), "Delivered very long message Delivered " +
								"very long message Delivered very long message Delivered very " +
								"long message Delivered very long message", TrackingStatus.DELIVERING),
					new HistoryItem(new Date(), "Bla BlaDelivered very long message Delivered " +
										"very long message Delivered very long message Delivered very " +
										"long message Delivered very long message", TrackingStatus.PROCESSING),
					new HistoryItem(new Date(new Date().getTime() - 24*60*60*1000*5), "Processing", TrackingStatus.PROCESSING),
					new HistoryItem(new Date(new Date().getTime() - 24*60*60*1000*10), "Got", TrackingStatus.NONE)
				}),
				new TrackingItem("RC12312314CH", null, new HistoryItem[]{
						new HistoryItem(new Date(), "Processing", TrackingStatus.PROCESSING),
						new HistoryItem(new Date(new Date().getTime() - 24*60*60*1000*7), "Got", TrackingStatus.NONE)
				})
			};
		return items;*/
		log("Requesting items for user " + userId);
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		Query query = new Query(USER_ITEM_TABLE).setFilter(
				new Query.FilterPredicate(USER_ID, FilterOperator.EQUAL,userId));
		
		List<Entity> barcodes = datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults());
		if (barcodes.isEmpty())
			return new TrackingItem[0];
		
		TrackingItem[] result = new TrackingItem[barcodes.size()];
		for (int i = 0; i < barcodes.size(); i++) {
			TrackingItem item = new TrackingItem();
			item.setBarCode((String)barcodes.get(i).getProperty(BARCODE));
			item.setName((String)barcodes.get(i).getProperty(NAME));
			item.setItems(getHistoryItems(item.getBarCode()));
			result[i] = item;
		}
		return result;
	}

	@Override
	public TrackingItem addItem(long userId, String barcode, String name) {
		log("Add item " + barcode + " for user " + userId);
		barcode = barcode.toUpperCase();
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(USER_ITEM_TABLE).setFilter(new CompositeFilter(
				CompositeFilterOperator.AND, Arrays.<Filter> asList(
						new FilterPredicate(USER_ID, FilterOperator.EQUAL,
								userId), new FilterPredicate(
								BARCODE, FilterOperator.EQUAL, barcode))));
		if (!datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults()).isEmpty())
			return null;
		
		Entity userItem = new Entity(USER_ITEM_TABLE);
		userItem.setProperty(USER_ID, userId);
		userItem.setProperty(BARCODE, barcode);
		userItem.setProperty(NAME, name);
		datastore.put(userItem);
		TrackingItem item = new TrackingItem();
		item.setBarCode(barcode);
		item.setName(name);
		item.setItems(new HistoryItem[] { getNewHistoryItem(barcode)});
		return item;
	}
	
	private HistoryItem[] getHistoryItems(String barcode) {
		log("Get history items for " + barcode);
		barcode = barcode.toUpperCase();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(USER_ITEM_TABLE).setFilter(
				new Query.FilterPredicate(BARCODE, FilterOperator.EQUAL, barcode))
				.addSort(DATE, SortDirection.DESCENDING);
		List<Entity> result = datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults());
		if (result.isEmpty())
			return new HistoryItem[0];
		HistoryItem[] items = new HistoryItem[result.size()];
		for (int i = 0; i < result.size(); i++)
			items[i] = extractHistoryItem(result.get(i));
		return items;
	}
	
	private HistoryItem getNewHistoryItem(String barcode) {
		String message = null;
		try {
			message = adapter.getMessage(barcode);
		} catch (IOException e) {
			log("Error while requesting url", e);
			return null;
		}
		return findOrCreateHistoryItem(barcode, message);
	}
	
	private HistoryItem findOrCreateHistoryItem(String barcode, String message) {
		log("Looking item: " + barcode + " with message: \n" + message);

		// MessageDigest md = MessageDigest.getInstance("MD5");
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(HISTORY_ITEM_TABLE).setFilter(new CompositeFilter(
				CompositeFilterOperator.AND, Arrays.<Filter> asList(
						new FilterPredicate(BARCODE, FilterOperator.EQUAL,
								barcode.toUpperCase()), new FilterPredicate(
								MESSAGE, FilterOperator.EQUAL, message))));
		List<Entity> result = datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults());
		if (!result.isEmpty())
			return extractHistoryItem(result.get(0));
		HistoryItem item = new HistoryItem(new Date(), message, getTrackingStatus(message));
		Entity entity = new Entity(HISTORY_ITEM_TABLE);
		entity.setProperty(DATE, item.getDate());
		entity.setProperty(MESSAGE, item.getText());
		datastore.put(entity);
		
		
		return item;
	}
	
	private HistoryItem extractHistoryItem(Entity entity) {
		return new HistoryItem((Date)entity.getProperty(DATE),
				(String)entity.getProperty(MESSAGE),
				getTrackingStatus((String)entity.getProperty(MESSAGE)));
	}
	
	private TrackingStatus getTrackingStatus(String message) {
		return TrackingStatus.NONE;
	}

	public void updateItems() {
		log("Updating items ");
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		Query query = new Query(USER_ITEM_TABLE);
		
		List<Entity> result = datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults());
		if (result.isEmpty())
			return;
		
		Set<String> barcodes = new HashSet<String>(result.size());
		for (Entity code : result)
			barcodes.add((String)code.getProperty(BARCODE));
		log("Processing items " + barcodes.size());
		for (String code : barcodes) {
			getNewHistoryItem(code);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				log("Interrupted",e);
			}
		}
	}
	

}
