package reedey.server.impl;

import static reedey.server.impl.DatabaseConstants.BARCODE;
import static reedey.server.impl.DatabaseConstants.DATE;
import static reedey.server.impl.DatabaseConstants.EMAIL;
import static reedey.server.impl.DatabaseConstants.EMAIL_FLAGS;
import static reedey.server.impl.DatabaseConstants.HISTORY_ITEM_TABLE;
import static reedey.server.impl.DatabaseConstants.MESSAGE;
import static reedey.server.impl.DatabaseConstants.NAME;
import static reedey.server.impl.DatabaseConstants.USER_ATTR;
import static reedey.server.impl.DatabaseConstants.USER_ID;
import static reedey.server.impl.DatabaseConstants.USER_ID2;
import static reedey.server.impl.DatabaseConstants.USER_ITEM_TABLE;
import static reedey.server.impl.DatabaseConstants.USER_TABLE;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpSession;

import reedey.client.service.TrackingService;
import reedey.server.tracking.EMSAdapter;
import reedey.server.tracking.Mailer;
import reedey.shared.exceptions.ServiceException;
import reedey.shared.tracking.entity.HistoryItem;
import reedey.shared.tracking.entity.TrackingItem;
import reedey.shared.tracking.entity.TrackingStatus;
import reedey.shared.tracking.entity.User;

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
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TrackingServiceImpl extends RemoteServiceServlet implements TrackingService {

	private static final long serialVersionUID = -8002177620880133394L;

	private static final EMSAdapter adapter = new EMSAdapter();
	
	@Override
	public TrackingItem[] getItems(long userId) {
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
		Arrays.sort(result, new Comparator<TrackingItem>() {
			@Override
			public int compare(TrackingItem o1, TrackingItem o2) {
				Date d1, d2;
				if (o1.getItems().length == 0)
					d1 = new Date(0);
				else d1 = o1.getItems()[0].getDate();
				if (o2.getItems().length == 0)
					d2 = new Date(0);
				else d2 = o2.getItems()[0].getDate();
				return -d1.compareTo(d2);
			}
		});
		//log(Arrays.toString(result));
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
	
	@Override
	public void removeItem(long userId, String barcode) {
		log("Remove item " + barcode + " for user " + userId);
		barcode = barcode.toUpperCase();
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		Query query = new Query(USER_ITEM_TABLE).setFilter(new CompositeFilter(
				CompositeFilterOperator.AND, Arrays.<Filter> asList(
						new FilterPredicate(USER_ID, FilterOperator.EQUAL,
								userId), new FilterPredicate(
								BARCODE, FilterOperator.EQUAL, barcode))));
		List<Entity> result = datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults());
		if (result.isEmpty())
			throw new ServiceException("Cannot find item " + barcode + " for user " + userId);
		datastore.delete(result.get(0).getKey());
	}
	
	private HistoryItem[] getHistoryItems(String barcode) {
		log("Get history items for " + barcode);
		barcode = barcode.toUpperCase();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(HISTORY_ITEM_TABLE).setFilter(
				new Query.FilterPredicate(BARCODE, FilterOperator.EQUAL, barcode));
		List<Entity> result = datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults());
		if (result.isEmpty())
			return new HistoryItem[0];
		HistoryItem[] items = new HistoryItem[result.size()];
		for (int i = 0; i < result.size(); i++)
			items[i] = extractHistoryItem(result.get(i));
		Arrays.sort(items);
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
		log("Creating new history item for " + barcode);
		HistoryItem item = new HistoryItem(new Date(), message, getTrackingStatus(message));
		Entity entity = new Entity(HISTORY_ITEM_TABLE);
		entity.setProperty(BARCODE, barcode);
		entity.setProperty(DATE, item.getDate());
		entity.setProperty(MESSAGE, item.getText());
		datastore.put(entity);
		
		checkForNotification(item, barcode);
		
		return item;
	}

	private HistoryItem extractHistoryItem(Entity entity) {
		return new HistoryItem((Date)entity.getProperty(DATE),
				(String)entity.getProperty(MESSAGE),
				getTrackingStatus((String)entity.getProperty(MESSAGE)));
	}
	
	private TrackingStatus getTrackingStatus(String message) {
		if (message.contains("тому що не зареєстровані в системі"))
			return TrackingStatus.NONE;
		if (message.contains("вручене адресату (одержувачу) особисто"))
			return TrackingStatus.DELIVERED;
		//if (message.contains(""))
		//	return TrackingStatus.DELIVERING;
		return TrackingStatus.PROCESSING;
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

	private void checkForNotification(HistoryItem item, String barcode) {
		HttpSession session = getThreadLocalRequest().getSession();
		User user = (User) session.getAttribute(USER_ATTR);
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(USER_TABLE).setFilter(
				new Query.FilterPredicate(USER_ID2, FilterOperator.EQUAL, user.getId()));
		List<Entity> result = datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults());
		if (result.isEmpty())
			throw new ServiceException("Invalid user " + user);
		String mail = (String)result.get(0).getProperty(EMAIL);
		if (mail == null)
			return;
		Integer flags = (Integer)result.get(0).getProperty(EMAIL_FLAGS);
		if (flags == null)
			return;
		log("Check item for flags(" + flags + "): " + item);
		if (((1 << item.getStatus().ordinal()) & flags) > 0) {
			log("Sending email");
			Query nameQuery = new Query(USER_ITEM_TABLE).setFilter(new CompositeFilter(
					CompositeFilterOperator.AND, Arrays.<Filter> asList(
							new FilterPredicate(BARCODE, FilterOperator.EQUAL,
									barcode.toUpperCase()), new FilterPredicate(
									USER_ID, FilterOperator.EQUAL, user.getId()))));
			List<Entity> items = datastore.prepare(nameQuery)
					.asList(FetchOptions.Builder.withDefaults());
			if (items.isEmpty())
				throw new ServiceException("Invalid barcode " + barcode + " for user " + user);
			String name = (String)items.get(0).getProperty(NAME);
			if (name == null)
				name = barcode;
			try {
				new Mailer().sendMail(mail, user.getName(), "Состояние заказа " + name + " изменилось!", 
						"Текущее состояние заказа :<br/>" + item.getText());
			} catch (AddressException e) {
				// throw new ServiceException(e);
				log("Wrong adress", e);
			} catch (MessagingException e) {
				log("Error occured while sending email", e);
			} catch (Exception e) {
				log("Error occured while sending email", e);
				throw new ServiceException(e);
			}
		}
	}

}
