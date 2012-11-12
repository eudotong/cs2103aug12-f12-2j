package utilities;

import org.joda.time.DateTime;

public class DateComparator {
	private static final int POSITIVE_NUMBER = 1;
	private static final int SAME_TIME = 0;
	private static final int NEGATIVE_NUMBER = -1;
	private static final int MILLISEC_DIFF_ALLOWANCE = 120;

	/**
	 * Returns true if the datetime specified is the current date and time.
	 * Returns false otherwise. Has some allowance for a small difference in
	 * time.
	 * 
	 * @param dateTimeToCheck
	 * @return boolean
	 */
	public static boolean isNow(DateTime dateTimeToCheck) {
		assert (dateTimeToCheck != null) : "Null DateTime.";
		long timeNow = new DateTime().getMillis();
		long timeSpecified = dateTimeToCheck.getMillis();
		if (Math.abs(timeNow - timeSpecified) <= MILLISEC_DIFF_ALLOWANCE) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the time specified is the same time of the day as the
	 * current time. Returns false otherwise. Has some allowance for a small
	 * difference in time.
	 * 
	 * @param dateTimeToCheck
	 * @return boolean
	 */
	public static boolean isSameTimeOfDayAsNow(DateTime dateTimeToCheck) {
		assert (dateTimeToCheck != null) : "Null DateTime.";
		int timeNow = new DateTime().getMillisOfDay();
		int timeSpecified = dateTimeToCheck.getMillisOfDay();
		if (Math.abs(timeNow - timeSpecified) <= MILLISEC_DIFF_ALLOWANCE) {
			return false;
		}
		return true;
	}

	/**
	 * A null date is always bigger than any other date. Returns positive number
	 * if firstDate is greater than secondDate, returns negative number if
	 * firstDate is smaller than secondDate and returns zero if they are the
	 * same.
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return int
	 */
	public static int compareNullDatesLast(DateTime firstDate,
			DateTime secondDate) {
		if (firstDate == null && secondDate == null) {
			return SAME_TIME;
		}
		if (firstDate == null && secondDate != null) {
			return POSITIVE_NUMBER;
		}
		if (firstDate != null && secondDate == null) {
			return NEGATIVE_NUMBER;
		}
		return firstDate.compareTo(secondDate);
	}

	/**
	 * Returns true if firstDate and secondDate fall on the same day. Returns
	 * false otherwise.
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return boolean
	 */
	public static boolean isSameDay(DateTime firstDate, DateTime secondDate) {
		if (firstDate == secondDate) {
			return true;
		}
		if (firstDate == null && secondDate != null || firstDate != null
				&& secondDate == null) {
			return false;
		}
		firstDate = firstDate.withTimeAtStartOfDay();
		secondDate = secondDate.withTimeAtStartOfDay();
		if (firstDate.compareTo(secondDate) == SAME_TIME) {
			return true;
		}
		return false;
	}

}
