package com.pgbezerra.bezerras.entities.enums;

public enum DayOfWeek {
	SUNDAY(0b000_0001),
	MONDAY(2),
	TUESDAY(3),
	WEDNESDAY(4),
	THURSDAY(5),
	FRIDAY(6),
	SATURDAY(7);
	
	private Integer dayCode;
	
	private DayOfWeek(Integer i) {
		this.dayCode = i;
	}
	
	public Integer getDayCode() {
		return dayCode;
	}
	
	public static DayOfWeek getByDayCode(Integer dayCode) {
		if (dayCode <= 0)
			throw new IllegalArgumentException(String.format("Invalid status code[%s], status must be greater than 0", dayCode));
		for(DayOfWeek status: DayOfWeek.values())
			if(status.getDayCode().equals(dayCode))
				return status;
		throw new IllegalArgumentException(String.format("Invalid status code[%s], status not found", dayCode));
	}
}
