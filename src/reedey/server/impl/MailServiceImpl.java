package reedey.server.impl;

import static reedey.server.impl.DatabaseConstants.EMAIL;
import static reedey.server.impl.DatabaseConstants.TOKENS;
import static reedey.server.impl.DatabaseConstants.TOKEN_DATE;
import static reedey.server.impl.DatabaseConstants.TOKEN_ID;
import static reedey.server.impl.DatabaseConstants.USER_ATTR;
import static reedey.server.impl.DatabaseConstants.USER_ID;
import static reedey.server.impl.DatabaseConstants.USER_ID2;
import static reedey.server.impl.DatabaseConstants.USER_TABLE;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import reedey.client.service.MailService;
import reedey.server.tracking.Mailer;
import reedey.server.tracking.Messages;
import reedey.shared.exceptions.ServiceException;
import reedey.shared.exceptions.SessionExpiredException;
import reedey.shared.tracking.entity.User;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MailServiceImpl extends RemoteServiceServlet implements
		MailService {

	private static final long serialVersionUID = -256250255315902020L;

	@Override
	public void activateEmail(String email) {
		HttpSession session = getThreadLocalRequest().getSession();
		User user = (User) session.getAttribute(USER_ATTR);
		if (user == null)
			throw new SessionExpiredException();
		long userId = user.getId();
		long token = UUID.randomUUID().getLeastSignificantBits();
		saveTempToken(userId, email, token);
		sendTokenMail(user.getName(), email, token);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		clearOldTokens();
		String tokenStr = req.getParameter("token"); //$NON-NLS-1$
		long token;
		try {
			token = Long.valueOf(tokenStr);
		} catch (NumberFormatException e) {
			log("Error while parsing token", e); //$NON-NLS-1$
			resp.getWriter().write("Invalid token"); //$NON-NLS-1$
			resp.getWriter().close();
			return;
		}
		try {
			verifyMailByToken(token);
		} catch (IllegalArgumentException e) {
			log("Expired token", e); //$NON-NLS-1$
			resp.getWriter().write("Invalid token"); //$NON-NLS-1$
			resp.getWriter().close();
			return;
		}
		resp.getWriter().write("E-mail activated!"); //$NON-NLS-1$
		resp.getWriter().close();
		return;
	}

	private void saveTempToken(long userId, String email, long token) {
		log("Add token " + token + " for user " + userId); //$NON-NLS-1$ //$NON-NLS-2$

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Entity tokenObject = new Entity(TOKENS);
		tokenObject.setProperty(USER_ID, userId);
		tokenObject.setProperty(TOKEN_ID, token);
		tokenObject.setProperty(EMAIL, email);
		tokenObject.setProperty(TOKEN_DATE, Calendar.getInstance().getTime());
		datastore.put(tokenObject);
	}

	private void clearOldTokens() {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(TOKENS).setFilter(new Query.FilterPredicate(
				TOKEN_DATE, FilterOperator.LESS_THAN, new Date(new Date()
						.getTime() - 1000 * 60 * 60 * 24)));
		List<Entity> result = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		Key[] keys = new Key[result.size()];
		for (int i = 0; i < result.size(); i++)
			keys[i] = result.get(i).getKey();
		datastore.delete(keys);
	}

	private void verifyMailByToken(long token) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = new Query(TOKENS).setFilter(new Query.FilterPredicate(
				TOKEN_ID, FilterOperator.EQUAL, token));
		List<Entity> result = datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		if (result.isEmpty())
			throw new IllegalArgumentException();
		Entity last = result.get(0);
		long userId = (long) last.getProperty(USER_ID);
		String mail = (String) last.getProperty(EMAIL);
		datastore.delete(result.get(0).getKey());

		Query query2 = new Query(USER_TABLE)
				.setFilter(new Query.FilterPredicate(USER_ID2,
						FilterOperator.EQUAL, userId));
		result = datastore.prepare(query2).asList(
				FetchOptions.Builder.withDefaults());
		if (!result.isEmpty()) {
			Entity res = result.get(0);
			res.setProperty(EMAIL, mail);
			datastore.put(res);
		} else {
			throw new IllegalArgumentException("User not found"); //$NON-NLS-1$
		}
	}

	private void sendTokenMail(String userName, String email, long token) {
		String msgBody = Messages.getString("mail.activation.body") //$NON-NLS-1$
				+ userName + "): "+ generateLink(token); //$NON-NLS-1$
		try {
			new Mailer().sendMail(email, userName, Messages.getString("mail.activation.subject"), msgBody); //$NON-NLS-1$
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

	private String generateLink(long token) {
		return Properties.getString("mail.activate.url") + token; //$NON-NLS-1$
	}
}
