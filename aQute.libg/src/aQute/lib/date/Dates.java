package aQute.lib.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Dates {
	public final static ZoneId					UTC_ZONE_ID				= ZoneId.of("UTC");
	public static final TimeZone				UTC_TIME_ZONE			= TimeZone.getTimeZone("UTC");
	private static final Pattern				IS_NUMERIC_P			= Pattern.compile("[+-]?\\d+");
	private static final DateTimeFormatter[]	DATE_TIME_FORMATTERS	= new DateTimeFormatter[] {
		// @formatter:off
		DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSSZ", Locale.ROOT),
		DateTimeFormatter.ISO_OFFSET_DATE.withLocale(Locale.ROOT),
		DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(Locale.ROOT),
		DateTimeFormatter.ISO_LOCAL_DATE.withLocale(Locale.ROOT),
		DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(Locale.ROOT),
		DateTimeFormatter.ISO_ORDINAL_DATE.withLocale(Locale.ROOT),
		DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(Locale.ROOT),
		DateTimeFormatter.ISO_WEEK_DATE.withLocale(Locale.ROOT),
		DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.ROOT),

		// old Date toString format
		DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ROOT),

		// old Date toString format in current Locale
		DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy"),

		DateTimeFormatter.ofPattern("yyyy[-][/][ ]MM[-][/][ ]dd[ ][HH[:]mm[[:]ss][.SSS]][X]", Locale.ROOT),

		DateTimeFormatter.ofPattern("dd[-][/][ ]MM[-][/][ ]yyyy[ HH[:]mm[[:]ss[.SSS]][X]", Locale.ROOT),
		DateTimeFormatter.ofPattern("dd[-][/][ ]MMM[-][/][ ]yyyy[ HH[:]mm[[:]ss[.SSS]][X]", Locale.ROOT),
		DateTimeFormatter.ofPattern("dd[-][/][ ]MMMM[-][/][ ]yyyy[ HH[:]mm[[:]ss[.SSS]][X]", Locale.US),

		// Dont ask why these are needed, seems the DTF has a problem with a long
		// row of digits. The optional characters [] dont work it seems
		DateTimeFormatter.ofPattern("yyyyMMdd[X]", Locale.ROOT),
		DateTimeFormatter.ofPattern("yyyyMMddHHmm[X]", Locale.ROOT),
		DateTimeFormatter.ofPattern("yyyyMMdd[ ][/][-]HHmm[X]", Locale.ROOT),
		DateTimeFormatter.ofPattern("yyyyMMddHHmmss[.SSS][X]", Locale.ROOT),
		DateTimeFormatter.ofPattern("yyyyMMdd[ ][/][-]HHmmss[.SSS][X]", Locale.ROOT),



		// @formatter:on
	};

	/**
	 * Return a ZonedDateTime that is set to the given datestring. This will try
	 * all standard DateTimeFormatter formats and a bunch more. It does not
	 * support formats where the day and month are ambiguous. It is either
	 * year-month-day or day-month-year.
	 *
	 * @param dateString a date formatted string
	 * @return a ZonedDateTime or null if the string cannot be interpreted as a
	 *         date
	 */
	public static ZonedDateTime parse(String dateString) {

		for (DateTimeFormatter df : DATE_TIME_FORMATTERS) {
			try {
				return toZonedDateTime(df.parse(dateString));
			} catch (DateTimeParseException dte) {
				// we ignore wrong formats
				continue;
			}

		}

		if (IS_NUMERIC_P.matcher(dateString)
			.matches()) {
			long ldate = Long.parseLong(dateString);
			return Instant.ofEpochMilli(ldate)
				.atZone(UTC_ZONE_ID);
		}

		return null;
	}

	/**
	 * Turn a TemporalAccessor into a ZonedDateTime using defaults for missing
	 * fields. See {@link #toZonedDateTime(TemporalAccessor)} for defaults.
	 *
	 * @param temporal the temporal to turn into {@link ZonedDateTime}
	 * @return a {@link ZonedDateTime}
	 */
	public static ZonedDateTime toZonedDateTime(TemporalAccessor temporal) {

		if (temporal instanceof ZonedDateTime)
			return (ZonedDateTime) temporal;

		LocalDate date = temporal.query(TemporalQueries.localDate());
		LocalTime time = temporal.query(TemporalQueries.localTime());
		ZoneId zone = temporal.query(TemporalQueries.zone());

		return toZonedDateTime(date, time, zone);
	}

	/**
	 * Return a new ZonedDateTime based on a local date, time, and zone. Each
	 * can be null.
	 *
	 * @param date the localdate, when null, the current date is used
	 * @param time the time, when null, 00:00:00.000 is used
	 * @param zone the time zone, when null, UTC is used
	 * @return a {@link ZonedDateTime}
	 */
	public static ZonedDateTime toZonedDateTime(LocalDate date, LocalTime time, ZoneId zone) {

		if (date == null)
			date = LocalDate.now();

		if (time == null)
			time = LocalTime.MIN;

		if (zone == null)
			zone = UTC_ZONE_ID;

		return ZonedDateTime.of(date, time, zone);
	}
}