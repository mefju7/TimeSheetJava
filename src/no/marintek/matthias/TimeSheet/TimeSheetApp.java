/**
 * 
 */
package no.marintek.matthias.TimeSheet;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import no.marintek.matthias.TimeSheet.ui.TimeSheetWindow;
import no.marintek.matthias.data.TimeSheetData;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class TimeSheetApp.
 *
 * @author mnow
 */
public class TimeSheetApp {

	/** The Constant dataFn. */
	final static String dataFn = "dataFile";

	/** The timedata. */
	private static TimeSheetData timedata;

	/**
	 * The main method.
	 *
	 * @param args
	 *          the arguments
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Display display = Display.getDefault();
		// Log log = LogFactory.getLog(TimeSheetApp.class);
		// log.info("starting here");
		timedata = initializeData();
		// TimeSheetEntry e = new
		// TimeSheetEntry().setStartTime(null).setProject("xx").setTask("yy");
		// timedata.entries.add(e);
		TimeSheetWindow tsw = new TimeSheetWindow(display);
		tsw.setTimeData(timedata);
		tsw.open();
		tsw.layout();
		tsw.mntmopen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Preferences prefs = Preferences.userNodeForPackage(TimeSheetApp.class);
				FileDialog fd = new FileDialog(tsw);
				fd.setFileName(prefs.get(dataFn, null));
				String fn = fd.open();
				if (!fn.isEmpty()) {
					prefs.put(dataFn, fn);
					System.out.printf("got file %s\n", fn);
					timedata = TimeSheetData.loadData(fn);
					tsw.setTimeData(timedata);
				}
				System.out.println("file dialog done");
			}
		});
		// message loop
		while (display.getShells().length > 0) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// done with message loop
		if (timedata != null) {
			Preferences prefs = Preferences.userNodeForPackage(TimeSheetApp.class);
			try {
				String dfn = prefs.get(dataFn, null);
				if ((dfn == null) || dfn.isEmpty()) {
					FileDialog fd = new FileDialog(tsw);
					fd.setFileName(prefs.get(dataFn, null));
					String fn = fd.open();
					if (!fn.isEmpty()) {
						prefs.put(dataFn, fn);
						System.out.printf("got file %s\n", fn);
					}
				}
				prefs.flush();

				if ((dfn != null) && !dfn.isEmpty()) {
					timedata.saveData(dfn);
				}

			} catch (BackingStoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	/**
	 * Initialize data.
	 *
	 * @return the time sheet data
	 */
	private static TimeSheetData initializeData() {
		Preferences prefs = Preferences.userNodeForPackage(TimeSheetApp.class);
		String dfn = prefs.get(dataFn, null);
		if ((dfn == null) || dfn.isEmpty()) {
			return new TimeSheetData();
		}
		return TimeSheetData.loadData(dfn);

	}

}
