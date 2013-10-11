package reedey.server.impl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import reedey.client.service.LoginService;
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

	private static final String USER_TABLE = "users";
	private static final String USER_ATTR = "username";
	private static final String USER_ATTR_ID = "userid";
	private static final String USER_LOGIN = "login";
	private static final String USER_PWD = "password";
	private static final String USER_ID = "id";

	@Override
	public User loginFromSession() {
		HttpSession session = getThreadLocalRequest().getSession();
		String userName = (String)session.getAttribute(USER_ATTR);
		if (userName == null)
			return null;
		return new User(userName, (long)session.getAttribute(USER_ATTR_ID));
	}

	@Override
	public User login(String login, String password) {
		log("Login user: " + login);

		// MessageDigest md = MessageDigest.getInstance("MD5");
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(USER_TABLE).setFilter(new CompositeFilter(
				CompositeFilterOperator.AND, Arrays.<Filter> asList(
						new FilterPredicate(USER_LOGIN, FilterOperator.EQUAL,
								login.toLowerCase()), new FilterPredicate(
								USER_PWD, FilterOperator.EQUAL, password))));
		List<Entity> result = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		if (!result.isEmpty()) {
			Entity entity = result.get(0);
			User user = new User((String) entity.getProperty(USER_LOGIN),
					(long) entity.getProperty(USER_ID));
			getThreadLocalRequest().getSession().setAttribute(USER_ATTR, user.getName());
			getThreadLocalRequest().getSession().setAttribute(USER_ATTR_ID, user.getId());
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
			user.setProperty(USER_PWD, password);
			user.setProperty(USER_ID, id);
			datastore.put(user);
			User created = new User(login, id);
			getThreadLocalRequest().getSession().setAttribute(USER_ATTR, created.getName());
			getThreadLocalRequest().getSession().setAttribute(USER_ATTR_ID, created.getId());
			return created;
		}
		return null;
	}

	@Override
	public void logout() {
		log("Logout user");
		HttpSession session = getThreadLocalRequest().getSession();
		session.removeAttribute(USER_ATTR);
		session.removeAttribute(USER_ATTR_ID);
	}

}
