package no.marintek.matthias.data;

import java.util.Comparator;

public class TimeSheetEntryComparer implements Comparator<TimeSheetEntry> {

	@Override
	public int compare(TimeSheetEntry o1, TimeSheetEntry o2) {
		return  o1.startTime.compareTo(o2.startTime);
	}

}
