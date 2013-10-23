package reedey.server.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import static reedey.server.impl.DatabaseConstants.*;

import reedey.client.service.LoginService;
import reedey.shared.exceptions.ServiceException;
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

public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {

	private static final long serialVersionUID = 7487435292987824639L;

	

	@Override
	public User loginFromSession() {
		HttpSession session = getThreadLocalRequest().getSession();
		User user = (User) session.getAttribute(USER_ATTR);
		if (user != null)
			return getUserById(user.getId());
		return null;
	}

	@Override
	public User login(String login, String password) {
		log("Login user: " + login);

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(USER_TABLE).setFilter(new CompositeFilter(
				CompositeFilterOperator.AND, Arrays.<Filter> asList(
						new FilterPredicate(USER_LOGIN, FilterOperator.EQUAL,
								login.toLowerCase()), new FilterPredicate(
								USER_PWD, FilterOperator.EQUAL, getPasswordHash(password)))));
		List<Entity> result = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		if (!result.isEmpty()) {
			Entity entity = result.get(0);
			User user = new User((String) entity.getProperty(USER_LOGIN),
					(long) entity.getProperty(USER_ID2), (String) entity.getProperty(EMAIL));
			Integer flags = (Integer) entity.getProperty(EMAIL_FLAGS);
			user.setFlags(flags != null ? flags : 0);
			getThreadLocalRequest().getSession().setAttribute(USER_ATTR, user);
			return user;
		}
		return null;
	}
	
	private User getUserById(long userId) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(USER_TABLE).setFilter(new Query.FilterPredicate(
				USER_ID2, FilterOperator.EQUAL, userId));
		List<Entity> result = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		if (!result.isEmpty()) {
			Entity entity = result.get(0);
			User user = new User((String) entity.getProperty(USER_LOGIN),
					(long) entity.getProperty(USER_ID2), (String) entity.getProperty(EMAIL));
			Integer flags = (Integer) entity.getProperty(EMAIL_FLAGS);
			user.setFlags(flags != null ? flags : 0);
			getThreadLocalRequest().getSession().setAttribute(USER_ATTR, user);
			return user;
		}
		return null;
	}

	@Override
	public User createUser(String login, String password) {
		log("Register user: " + login);

		// MessageDigest md = MessageDigest.getInstance("MD5");
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(USER_TABLE)
				.setFilter(new Query.FilterPredicate(USER_LOGIN,
						FilterOperator.EQUAL, login.toLowerCase()));
		if (datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults()).isEmpty()) {
			Entity user = new Entity(USER_TABLE);
			long id = UUID.randomUUID().getLeastSignificantBits();
			user.setProperty(USER_LOGIN, login.toLowerCase());
			user.setProperty(USER_PWD, getPasswordHash(password));
			user.setProperty(USER_ID2, id);
			datastore.put(user);
			User created = new User(login, id);
			getThreadLocalRequest().getSession().setAttribute(USER_ATTR, created);
			return created;
		}
		return null;
	}

	@Override
	public void logout() {
		log("Logout user");
		HttpSession session = getThreadLocalRequest().getSession();
		session.removeAttribute(USER_ATTR);
	}

	private String getPasswordHash(String password) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			log("Hash error", e);
			throw new ServiceException("Login failed", e);
		}
		return new String(md.digest(password.getBytes()));
	}
}
