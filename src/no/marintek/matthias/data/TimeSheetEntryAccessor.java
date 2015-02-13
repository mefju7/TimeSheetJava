package no.marintek.matthias.data;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

public class TimeSheetEntryAccessor implements IColumnPropertyAccessor<TimeSheetEntry> {

	static final int yearPos = 0;
	static final String yearLabel = "Year";
	static final int weekPos = yearPos + 1;
	static final String weekLabel = "Week";

	public static final int timePos = weekPos + 1;
	public static final String startTimeLabel = "Start";
	public static final int projPos = timePos + 1;
	public static final String projLabel = "Project";
	static final int taskPos = projPos + 1;
	public static final String taskLabel = "Task";
	static final int prevPos= taskPos+1;
	public static final String prevLabel="previous";
	static final int workedPos=prevPos+1;
	public static final String workedLabel="worked";	
	static final int billedPos=workedPos+1;
	public static final String billedLabel="billed";
	static final int remPos=billedPos+1;
	public static final String remLabel="remaining";
	// last one
	static final int colCount = remPos + 1;

	// public static final HashMap<String, String> property2Labels;
	// public static final String[] propertyNames ;
	public static final String[] columnLabels = { yearLabel, weekLabel, startTimeLabel, projLabel, taskLabel, prevLabel, workedLabel,billedLabel,remLabel};
	private TimeSheetData dataSource;

	
	
	public TimeSheetEntryAccessor(TimeSheetData data) {
		// TODO Auto-generated constructor stub
		this.dataSource=data;
	}

	@Override
	public Object getDataValue(TimeSheetEntry rowObject, int columnIndex) {
		switch (columnIndex) {
		case yearPos: {
			Calendar cal;
			cal = Calendar.getInstance();
			cal.setTime(rowObject.getStartTime());
			return cal.get(Calendar.YEAR);
		}
		case weekPos: {
			Calendar cal = Calendar.getInstance();
			cal.setFirstDayOfWeek(Calendar.MONDAY);
			cal.setMinimalDaysInFirstWeek(4);
			cal.setTime(rowObject.getStartTime());
			return cal.get(Calendar.WEEK_OF_YEAR);
		}
		case timePos: {
			return rowObject.getStartTime();
		}
		case projPos:
			return rowObject.getProject();
		case taskPos:
			return rowObject.getTask();
		
		case prevPos:
			return rowObject.previousLeft;
		case workedPos:
			return rowObject.worked;
		case billedPos:
			return rowObject.billed;
		case remPos:
			return rowObject.remaining;
		}
		return null;
	}

	@Override
	public void setDataValue(TimeSheetEntry rowObject, int columnIndex, Object newValue) {
		switch (columnIndex) {
		case timePos:
			if (newValue instanceof Date) {
				rowObject.setStartTime((Date) newValue);
				dataSource.reSort();
				dataSource.refresh();
			}
			break;
		case projPos:
			rowObject.setProject((String) newValue);
			rowObject.previousLeft=0;
			dataSource.refresh();
			break;
		case taskPos:
			rowObject.setTask((String) newValue);
			rowObject.previousLeft=0;
			dataSource.refresh();
			break;
		}
	}

	@Override
	public int getColumnCount() {
		return colCount;
	}

	@Override
	public String getColumnProperty(int columnIndex) {
		// TODO Auto-generated method stub
		return columnLabels[columnIndex];
	}

	@Override
	public int getColumnIndex(String propertyName) {
		// TODO Auto-generated method stub
		return 0;
	}
}