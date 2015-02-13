package no.marintek.matthias.TimeSheet.ui;

import java.util.List;

import no.marintek.matthias.data.TimeSheetEntry;

import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary.IGroupBySummaryProvider;

class TimeSheetSumBilled implements IGroupBySummaryProvider<TimeSheetEntry> {

	@Override
	public Object summarize(int columnIndex, List<TimeSheetEntry> children) {
		// TODO Auto-generated method stub
		double billed=0;
		for( TimeSheetEntry ch : children)
		{
			billed+= ch.billed;
		}
		return billed;
	}

}