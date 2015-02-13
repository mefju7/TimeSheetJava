package no.marintek.matthias.TimeSheet.ui;

import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

/**
 * The Class DoubleHoursConverter.
 */
public class DoubleHoursConverter extends DisplayConverter {

	@Override
	public Object canonicalToDisplayValue(Object canonVal) {
		if (canonVal instanceof String)
			return canonVal;
		if (canonVal instanceof Double) {
			Double tval = (Double) canonVal;
			if(tval==0.0)
				return "";
			String rv = "";
			if (tval < 0) {
				rv = "-";
				tval = -tval;
			}
			int h = (int) Math.floor(tval);
			tval -= h;
			tval *= 60;
			int m = (int) Math.floor(tval);
			tval -= m;
			tval *= 60;
			int s = (int) Math.floor(tval);
			rv = String.format("%s%d:%02d:%02d", rv, h, m, s);
			return rv;
		}
		return null;
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		// TODO Auto-generated method stub
		return null;
	}

}
