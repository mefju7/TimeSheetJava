package no.marintek.matthias.TimeSheet.ui;

import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

public class NonZeroConverter extends DisplayConverter {

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		// TODO Auto-generated method stub
		if(canonicalValue instanceof Double)
		{
			Double D = (Double) canonicalValue;
			if(D==0)
				return "";
			return String.format("%1.1f",D);
		}
		return null;
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		// TODO Auto-generated method stub
		return null;
	}

}
