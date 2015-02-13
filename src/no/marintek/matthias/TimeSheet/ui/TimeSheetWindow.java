package no.marintek.matthias.TimeSheet.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

import no.marintek.matthias.data.TimeSheetData;
import no.marintek.matthias.data.TimeSheetEntry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class TimeSheetWindow extends Shell {

	public static String startLabel = "StartLabel";

	public MenuItem mntmopen;
	private TimeSheetData timeData;

	private TimeSheetTable nattab;

	private Runnable updater = null;
	private int updateInterval = 500;

	protected Date lastTurnOver = new Date();

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public TimeSheetWindow(Display display) {
		super(display, SWT.SHELL_TRIM);
		setImage(SWTResourceManager.getImage(TimeSheetWindow.class, "/no/marintek/matthias/TimeSheet/ui/Sandwatch.png"));
		createContents();

		addShellListener(new ShellAdapter() {
			@Override
			public void shellActivated(ShellEvent e) {
				updater = createUpdater(display);
				updater.run();
			}

			@Override
			public void shellDeactivated(ShellEvent e) {
				updater = null;
			}
		});

	}

	protected Runnable createUpdater(Display display) {
		return new Runnable() {

			@Override
			public void run() {
				if (timeData == null)
					return;
				timeData.updateLast();
				if (timeData.nextTurnOver != null) {
					if (Math.abs(timeData.nextTurnOver.getTime() - lastTurnOver.getTime()) > 2000) {
						// 5 seconds different to before
						lastTurnOver = timeData.nextTurnOver;
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
					  setText("next turn over at " + sdf.format(lastTurnOver));
					}
				}
				// nattab.refresh();
				if (updater != null)
					display.timerExec(updateInterval, updater);
			}
		};

	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Time sheet register");
		setSize(1200, 800);
		setLayout(new FillLayout(SWT.VERTICAL));
		Menu menu = new Menu(this, SWT.BAR);
		setMenuBar(menu);

		MenuItem mntmfile = new MenuItem(menu, SWT.CASCADE);
		mntmfile.setText("&File");

		Menu menu_1 = new Menu(mntmfile);
		mntmfile.setMenu(menu_1);
		mntmopen = new MenuItem(menu_1, SWT.NONE);
		mntmopen.setText("&Open persistent file");

		MenuItem mntmBilled = new MenuItem(menu_1, SWT.CHECK);
		mntmBilled.setText("show only billed entries");
		mntmBilled.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if (nattab == null)
					return;
				nattab.ShowBilled(mntmBilled.getSelection());
			}
		});

		new MenuItem(menu_1, SWT.SEPARATOR);
		TimeSheetWindow shell = this;
		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		mntmExit.setText("E&xit");
		mntmExit.setAccelerator(SWT.ALT | SWT.F4);

		MenuItem mntmNew = new MenuItem(menu, SWT.CASCADE);
		Menu newSubMenu = new Menu(shell, SWT.DROP_DOWN);
		mntmNew.setMenu(newSubMenu);
		mntmNew.setText("&New");

		newSubMenu.addListener(SWT.Show, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// System.out.println("making submenu");
				MenuItem[] its = newSubMenu.getItems();
				// System.out.printf("removing %d items\n", its.length);
				for (MenuItem menuItem : its) {
					menuItem.dispose();
				}

				MenuItem mit1 = new MenuItem(newSubMenu, SWT.NONE);
				mit1.setText("empty row");
				mit1.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						timeData.entries.add(new TimeSheetEntry().setStartTime(null));
						timeData.reSort().refresh();
						nattab.showLast();
					};
				});

				for (Entry<String, TreeMap<String, TimeSheetEntry>> prj : timeData.projTask.entrySet()) {
					String prjTxt = prj.getKey();
					if ((prjTxt == null) || prjTxt.isEmpty())
						continue;
					MenuItem miPrj = new MenuItem(newSubMenu, SWT.CASCADE);
					miPrj.setText(prjTxt);
					Menu prjSubMenu = new Menu(shell, SWT.DROP_DOWN);
					miPrj.setMenu(prjSubMenu);
					for (Entry<String, TimeSheetEntry> task : prj.getValue().entrySet()) {
						MenuItem miTask = new MenuItem(prjSubMenu, SWT.NONE);
						String taskTxt = task.getKey();
						if ((taskTxt == null) || taskTxt.isEmpty())
							taskTxt = "---";
						miTask.setText(taskTxt);
						miTask.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								timeData.entries.add(new TimeSheetEntry(task.getValue()));
								timeData.reSort().refresh();
								nattab.showLast();
							};
						});
					}
				}
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	// datagrid needs to be created anew, not working with changing "datasource"
	// something

	public void setTimeData(TimeSheetData timedata) {
		// remembering this
		this.timeData = timedata;
		timeData.reSort();
		timeData.refresh();
		if ((nattab != null) && !nattab.isDisposed())
			nattab.dispose();
		nattab = TimeSheetTable.create(this, timeData);
		nattab.showLast();
		nattab.refresh();
	}

}
