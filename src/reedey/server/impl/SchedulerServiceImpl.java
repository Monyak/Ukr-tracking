package reedey.server.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SchedulerServiceImpl extends TrackingServiceImpl {

	private static final long serialVersionUID = -1290583620502335408L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String type = request.getParameter("type");
		log("Got type=" + type);
		if (type == null || !type.equals("day") && !type.equals("night")) {
		    processOldItems();
			return;
		}
		log("Starting cron task: " + type);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		log("hour is " + hour);
		if (type.equals("day") && (hour < 9 || hour > 18)
				|| type.equals("night") && hour >= 9 && hour <= 18) {
			log("Skipping cron task: " + type);
			return;
		}
		log("Cron job started!");
		updateItems();
	}
}
