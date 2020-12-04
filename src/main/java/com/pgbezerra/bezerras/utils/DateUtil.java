package com.pgbezerra.bezerras.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateUtil {
	
	private DateUtil() {}

	public static DayOfWeek currentDayOfWeek() {
		return LocalDate.now().getDayOfWeek();
	}
	
}
