package no.marintek.matthias.TimeSheet.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

public class DigitDateDisplayConverter extends DisplayConverter {
	
	private SimpleDateFormat dateFormat=new SimpleDateFormat("EEE yyyy-MM-dd HH:mm:ss");
	
	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		if(canonicalValue instanceof String)
			return canonicalValue;
		if(canonicalValue instanceof Date)
			return dateFormat.format(canonicalValue);
		return null;
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		if(displayValue instanceof Date)
			return displayValue;
		if(displayValue instanceof String)
			try {
				return dateFormat.parse(displayValue.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}

}
