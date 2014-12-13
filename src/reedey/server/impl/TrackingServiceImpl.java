package reedey.server.impl;

import static reedey.server.impl.DatabaseConstants.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import reedey.client.service.TrackingService;
import reedey.server.tracking.EMSAdapter;
import reedey.server.tracking.Mailer;
import reedey.server.tracking.Messages;
import reedey.server.tracking.StatusHandler;
import reedey.server.tracking.Track17Adapter;
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

	private final EMSAdapter adapter = new EMSAdapter();
	private final Track17Adapter track17 = new Track17Adapter();
	
	
	@Override
	public TrackingItem[] getItems() {
		long userId = getUserId();
		log("Requesting items for user " + userId); //$NON-NLS-1$
		
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
	public TrackingItem addItem(String barcode, String name) {
		long userId = getUserId();
		log("Add item " + barcode + " for user " + userId); //$NON-NLS-1$ //$NON-NLS-2$
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
	public void removeItem(String barcode) {
		long userId = getUserId();
		log("Remove item " + barcode + " for user " + userId); //$NON-NLS-1$ //$NON-NLS-2$
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
			throw new ServiceException("Cannot find item " + barcode + " for user " + userId); //$NON-NLS-1$ //$NON-NLS-2$
		datastore.delete(result.get(0).getKey());
	}
	
	private HistoryItem[] getHistoryItems(String barcode) {
		log("Get history items for " + barcode); //$NON-NLS-1$
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
		Arrays.sort(items, new Comparator<HistoryItem>() {
            @Override
            public int compare(HistoryItem item1, HistoryItem item2) {
                if (StatusHandler.isOrigin(item1.getText()) ^ StatusHandler.isOrigin(item2.getText())) {
                    if (StatusHandler.isOrigin(item1.getText()))
                        return 1;
                    else 
                        return -1;
                }
                return -item1.getDate().compareTo(item2.getDate());
            }
		});
		return items;
	}
	
	private HistoryItem getNewHistoryItem(String barcode) {
		String message = null;
		try {
			message = adapter.getMessage(barcode);
			if (message == null || message.isEmpty()) {
			    throw new Exception("Empty message for " + barcode);
			}
		} catch (Exception e) {
			log("Error while requesting url", e); //$NON-NLS-1$
			return null;
		}
		return findOrCreateHistoryItem(barcode, message);
	}
	
	private HistoryItem findOrCreateHistoryItem(String barcode, String message) {
		log("Looking item: " + barcode + " with message: \n" + message); //$NON-NLS-1$ //$NON-NLS-2$

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
		
		log("Creating new history item for " + barcode); //$NON-NLS-1$
		HistoryItem item = new HistoryItem(new Date(), message, StatusHandler.getTrackingStatus(message));
		Entity entity = new Entity(HISTORY_ITEM_TABLE);
		entity.setProperty(BARCODE, barcode);
		entity.setProperty(DATE, item.getDate());
		entity.setProperty(MESSAGE, item.getText());
		datastore.put(entity);
		
		checkForNotification(item, barcode);
		if (item.getStatus() == TrackingStatus.DELIVERED)
            setItemDisabled(barcode);
		
		return item;
	}
	
	private void setItemDisabled(String barcode) {
        log("Disable item " + barcode);
        barcode = barcode.toUpperCase();
        
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        
        Query query = new Query(USER_ITEM_TABLE).setFilter(new FilterPredicate(
                                BARCODE, FilterOperator.EQUAL, barcode));
        List<Entity> result = datastore.prepare(query)
                .asList(FetchOptions.Builder.withDefaults());
        if (result.isEmpty())
            throw new ServiceException("Cannot find item " + barcode);
        for (Entity item : result) {
            item.setProperty(DISABLED, true);
            datastore.put(item);
        }
    }

	private HistoryItem extractHistoryItem(Entity entity) {
		return new HistoryItem((Date)entity.getProperty(DATE),
				(String)entity.getProperty(MESSAGE),
				StatusHandler.getTrackingStatus((String)entity.getProperty(MESSAGE)));
	}
	
	protected void processOldItems() {
        log("Processing old items ");
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        
        Query query = new Query(USER_ITEM_TABLE);
        
        List<Entity> result = datastore.prepare(query)
                .asList(FetchOptions.Builder.withDefaults());
        if (result.isEmpty())
            return;
        
        Set<String> barcodes = new HashSet<String>(result.size());
        for (Entity code : result) {
            if (code.getProperty(DISABLED) == null || (boolean)code.getProperty(DISABLED) == false)
                barcodes.add((String)code.getProperty(BARCODE));
        }
        log("Processing old items " + barcodes.size());
        query = new Query(HISTORY_ITEM_TABLE).setFilter(new FilterPredicate(
                BARCODE, FilterOperator.IN, barcodes));
        result = datastore.prepare(query)
                .asList(FetchOptions.Builder.withDefaults());
        if (result.isEmpty())
            return;
        
        Set<Entity> itemsForUpdate = new HashSet<Entity>(result.size());
        for (Entity item : result) {
            if (StatusHandler.getTrackingStatus((String)item.getProperty(MESSAGE)) == TrackingStatus.DELIVERED) {
                itemsForUpdate.add(item);
            }
        }
        
        for (Entity item : itemsForUpdate) {
            setItemDisabled((String)item.getProperty(BARCODE));
        }
            
    }

	public void updateItems() {
		log("Updating items "); //$NON-NLS-1$
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		Query query = new Query(USER_ITEM_TABLE);
		
		List<Entity> result = datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults());
		if (result.isEmpty())
			return;
		
		Set<String> barcodes = new HashSet<String>(result.size());
		for (Entity code : result)
		    if (code.getProperty(DISABLED) == null || (boolean)code.getProperty(DISABLED) == false)
                barcodes.add((String)code.getProperty(BARCODE));
		log("Processing items " + barcodes.size()); //$NON-NLS-1$
		for (String code : barcodes) {
			HistoryItem item = getNewHistoryItem(code);
			if (item != null && item.getStatus() == TrackingStatus.NONE)
			    checkOriginItems(code);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log("Interrupted",e); //$NON-NLS-1$
			}
		}
	}
	
	private HistoryItem[] checkOriginItems(String barcode) {
	    try {
	        Map<Date, String> items = track17.getMessages(barcode);
	        log("Got " + items.size() + " track17 items");
	        if (items.size() == 0)
	            return null;
	        DatastoreService datastore = DatastoreServiceFactory
	                .getDatastoreService();
	        
	        Query query = new Query(HISTORY_ITEM_TABLE).setFilter(new Query.FilterPredicate(DATE, FilterOperator.IN, items.keySet()));
	        List<Entity> result = datastore.prepare(query)
	                .asList(FetchOptions.Builder.withDefaults());
	        if (result.size() == items.size()) {
	            return null;
	        } 
	        for (Entity entity : result)
	            items.remove(entity.getProperty(DATE));
	        log("Items after removing:" + items.size());
	        for (Entry<Date, String> entry : items.entrySet()) {
	            try {
	                String translated = track17.translateMessage(entry.getValue());
	                Entity entity = new Entity(HISTORY_ITEM_TABLE);
	                entity.setProperty(BARCODE, barcode);
	                entity.setProperty(DATE, entry.getKey());
	                entity.setProperty(MESSAGE, translated);
	                datastore.put(entity);
	            } catch (Exception e) {
	                log("Cannot translate " + entry.getValue(), e);
	            }
	        }
	    } catch (Exception e) {
	        log("Error with 17track", e);
	        return null;
	    }
	    return null;
	}

	private void checkForNotification(HistoryItem item, String barcode) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(USER_ITEM_TABLE).setFilter(
				new Query.FilterPredicate(BARCODE, FilterOperator.EQUAL, barcode));
		List<Entity> result = datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults());
		if (result.isEmpty())
			throw new ServiceException("Invalid barcode " + barcode); //$NON-NLS-1$
		Map<Long, String> users = new HashMap<>(result.size());
		for (Entity user : result) {
			Long userId = (Long) user.getProperty(USER_ID);
			String itemName = (String) user.getProperty(NAME);
			if (itemName == null)
				itemName = barcode;
			users.put(userId, itemName);
		}
		query = new Query(USER_TABLE).setFilter(
				new Query.FilterPredicate(USER_ID2, FilterOperator.IN, users.keySet()));
		result = datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults());
		if (result.isEmpty())
			throw new ServiceException("No users found for ids: " + users.keySet()); //$NON-NLS-1$
		for (Entity user : result) {
			String login = (String)user.getProperty(USER_LOGIN);
			String mail = (String)user.getProperty(EMAIL);
			if (mail == null) {
				log("Skip notification for user " + login + " due to empty e-mail"); //$NON-NLS-1$ //$NON-NLS-2$
				continue;
			}
			Long flags = (Long)user.getProperty(EMAIL_FLAGS);
			if (flags == null) {
				log("Skip notification for user " + login + " due to empty flags"); //$NON-NLS-1$ //$NON-NLS-2$
				continue;
			}
			log("Check item for flags(" + flags + "): " + item); //$NON-NLS-1$ //$NON-NLS-2$
			if (((1 << item.getStatus().ordinal()) & flags.intValue()) > 0) {
				Long userId = (Long) user.getProperty(USER_ID2);
				String name = users.get(userId);
				log("Sending email to " + login); //$NON-NLS-1$
				try {
					new Mailer().sendMail(mail, "", Messages.getString("track.status.subject1") + name //$NON-NLS-1$ //$NON-NLS-2$
					        + Messages.getString("track.status.subject2") + StatusHandler.getTrackingMessage(item.getStatus()),   //$NON-NLS-1$
							Messages.getString("track.status.new") + item.getText());   //$NON-NLS-1$
				} catch (AddressException e) {
					// throw new ServiceException(e);
					log("Wrong adress", e); //$NON-NLS-1$
				} catch (MessagingException e) {
					log("Error occured while sending email", e); //$NON-NLS-1$
				} catch (Exception e) {
					log("Error occured while sending email", e); //$NON-NLS-1$
					throw new ServiceException(e);
				}
			}
		}
	}
	
	private long getUserId() {
		User user = (User) getThreadLocalRequest().getSession().getAttribute(USER_ATTR);
		if (user != null)
			return user.getId();
		return 0;
	}

	@Override
	public void updateNotificationFlags(int flags) {
		long userId = getUserId();
		log("Updating flags for user=" + userId + ", flags=" + flags); //$NON-NLS-1$ //$NON-NLS-2$
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		Query query = new Query(USER_TABLE).setFilter(
				new Query.FilterPredicate(USER_ID2, FilterOperator.EQUAL, userId));
		List<Entity> result = datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults());
		if (result.isEmpty())
			throw new ServiceException("Invalid user " + userId); //$NON-NLS-1$
		Entity user = result.get(0);
		user.setProperty(EMAIL_FLAGS, flags);
		datastore.put(user);
	}
}
