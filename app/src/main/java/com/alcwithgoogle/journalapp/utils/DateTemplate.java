package com.alcwithgoogle.journalapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Locale;

public class DateTemplate {

	// supported Placeholder
	enum Placeholder {
		yy, y,
		MMM, MM, M, mm, m,
		DDD, DD, D,
		dd, d
	}

	/**
	 * Processed given formatting string replacing all known Placeholder with its values.
	 *
	 * @param cal    Calendar object to use as date/time source
	 * @param format Formatting string
	 *
	 * @return processed date/time string
	 */
	public static String format(Calendar cal, String format) {
		return format(cal, format, false);
	}

	/**
	 * Processed given formatting string replacing all known Placeholder with its values.
	 *
	 * @param cal          Calendar object to use as date/time source
	 * @param format       Formatting string
	 * @param forceEnglish enforces use of English language instead of default locale (whenever applicable)
	 *
	 * @return processed date/time string
	 */
	@SuppressWarnings ("WeakerAccess")
	public static String format(Calendar cal, String format, @SuppressWarnings ("SameParameterValue") boolean forceEnglish) {

		Locale locale = Locale.ENGLISH;

		// checks if formatter supports current system locale or we need to fallback to English
		if (!forceEnglish) {
			String currentLanguage = Locale.getDefault().getLanguage();

			Locale[] availableLocales = SimpleDateFormat.getAvailableLocales();
			for (Locale availableLocale : availableLocales) {
				if (availableLocale.getLanguage().equals(currentLanguage)) {
					locale = availableLocale;
					break;
				}
			}
		}

		return format(cal, format, locale);
	}

	/**
	 * Processed given formatting string replacing all known Placeholder with its values.
	 *
	 * @param cal    Calendar object to use as date/time source
	 * @param format Formatting string
	 * @param locale Locale to use for month, day names
	 *
	 * @return processed date/time string
	 */
	@SuppressWarnings ("WeakerAccess")
	public static String format(Calendar cal, String format, Locale locale) {

		EnumMap<Placeholder, String> map = new EnumMap<>(Placeholder.class);

		Date date = new Date(cal.getTimeInMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("", locale);
		formatter.setTimeZone(cal.getTimeZone());

		Calendar calendar = formatter.getCalendar();
		calendar.setMinimalDaysInFirstWeek(1);
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));


		// %yy% - long year (2010)
		// %y% - short year (10)
		map.put(Placeholder.yy, Integer.toString(cal.get(Calendar.YEAR)));
		map.put(Placeholder.y, map.get(Placeholder.yy).substring(2));

		// %MMM% - long month name (January)
		// %MM%	 - abbreviated month name (Jan)
		// %M%	 - first letter of month name (J)
		formatter.applyLocalizedPattern("MMMM");
		map.put(Placeholder.MMM, formatter.format(date));

		formatter.applyLocalizedPattern("MMM");
		map.put(Placeholder.MM, formatter.format(date));
		map.put(Placeholder.M, map.get(Placeholder.MM).substring(0, 1));

		// %mm%	- zero prefixed 2 digit month number (02 for Feb, 12 for Dec)
		// %m%	- month number as is (2 for Feb, 12 for Dec)
		formatter.applyLocalizedPattern("MM");
		map.put(Placeholder.mm, formatter.format(date));

		formatter.applyLocalizedPattern("M");
		map.put(Placeholder.m, formatter.format(date));


		// %DDD%	- full day name (Saturday, Monday etc)
		// %DD%		- abbreviated day name (Sat, Mon)
		// %D%		- one letter, abbreviated day name (S, M)
		formatter.applyLocalizedPattern("EEEE");
		map.put(Placeholder.DDD, formatter.format(date));

		formatter.applyLocalizedPattern("EEE");
		String abrvDayName = formatter.format(date);
		map.put(Placeholder.DD, abrvDayName);
		map.put(Placeholder.D, abrvDayName.substring(0, 1).toUpperCase());

		// %dd%		- zero prefixed 2 digit day number (01 for 1st, 27 for 27th)
		// %d%		- day number as is (1 for 1st, 27 for 27th)
		formatter.applyLocalizedPattern("dd");
		map.put(Placeholder.dd, formatter.format(date));

		formatter.applyLocalizedPattern("d");
		map.put(Placeholder.d, formatter.format(date));

		// placeholder substitution...
		String pattern;
		String result = format;
		for (Placeholder placeholderKey : Placeholder.values()) {
			pattern = "%" + placeholderKey.name() + "%";
			result = result.replaceAll(pattern, map.get(placeholderKey));
		}

		// done
		return result;
	}
}
