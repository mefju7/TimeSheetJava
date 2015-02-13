package no.marintek.matthias.TimeSheet.ui;

import java.text.SimpleDateFormat;

import no.marintek.matthias.data.TimeSheetData;
import no.marintek.matthias.data.TimeSheetEntry;
import no.marintek.matthias.data.TimeSheetEntryAccessor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByConfigAttributes;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByDataLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.config.DefaultGridLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeCollapseAllCommand;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeExpandAllCommand;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.MenuItemProviders;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.nebula.widgets.nattable.viewport.command.ShowRowInViewportCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.matchers.Matcher;

public class TimeSheetTable extends NatTable {

	private static EventList<TimeSheetEntry> entries;
	private ViewportLayer viewPortLayer;
	private SelectionLayer selectionLayer;
	private FilterList<TimeSheetEntry> filterList;

	private TimeSheetTable(Composite comp, ILayer gridLayer) {
		// TODO Auto-generated constructor stub
		super(comp, gridLayer, false);
	}

	public static TimeSheetTable create(Composite comp, TimeSheetData data) {

		// column titles
		//
		//
		ConfigRegistry configRegistry;
		configRegistry = new ConfigRegistry();

		// -----------------------------------------------------------------------------------
		// all layers need a data provider, making one for each part body,
		// column, row, corner
		// creating data providers: body column row corner
		TimeSheetEntryAccessor tsea;
		tsea = new TimeSheetEntryAccessor(data);
		if (!(data.entries instanceof EventList<?>)) {
			data.entries = GlazedLists.eventList(data.entries); // just replace
			// it with an
			// encapsulated
		}
//		EventList<TimeSheetEntry> entries;
		entries = (EventList<TimeSheetEntry>) data.entries;
		entries = new ObservableElementList<TimeSheetEntry>(entries, GlazedLists.beanConnector(TimeSheetEntry.class));

		TransformedList<TimeSheetEntry, TimeSheetEntry> rowObjectsGlazedList; // needed
																																					// for
																																					// summary
																																					// rows
																																					// with
																																					// details
		rowObjectsGlazedList = GlazedLists.threadSafeList(entries);

		SortedList<TimeSheetEntry> sortedEntries;
		entries = sortedEntries = new SortedList<TimeSheetEntry>(rowObjectsGlazedList, null);
		FilterList<TimeSheetEntry> filterList;
		entries = filterList = new FilterList<TimeSheetEntry>(entries);
		// ====================================================================================
		// layers for each part
		// https://eclipse.org/nattable/documentation.php?page=layer

		IUniqueIndexLayer stackLayer;
		IDataProvider bodyDataProvider;

		// starting with body due to dependencies
		// ----- this is the default one with provider below first layer,
		// created seperately
		// bodyDataProvider = new ListDataProvider<TimeSheetEntry>(data.entries,
		// tsea);
		// DataLayer bodyDataLayer;
		// stacklayer = bodyDataLayer = new DataLayer(bodyDataProvider);
		// ---- end first example
		// ---- this one will group
		GroupByModel groupByModel = new GroupByModel();
		GroupByDataLayer<TimeSheetEntry> bodyDataLayer;
		bodyDataLayer = new GroupByDataLayer<TimeSheetEntry>(groupByModel, entries, tsea, configRegistry);
		bodyDataProvider = bodyDataLayer.getDataProvider();

		DefaultColumnHeaderDataProvider colHeaderDataProvider;
		// colHeaderDataProvider = new
		// DefaultColumnHeaderDataProvider(TimeSheetEntryAccessor.propertyNames,
		// TimeSheetEntryAccessor.property2Labels);
		colHeaderDataProvider = new DefaultColumnHeaderDataProvider(TimeSheetEntryAccessor.columnLabels);
		DefaultRowHeaderDataProvider rowHeaderDataProvider;
		rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);
		DefaultCornerDataProvider cornerDataProvider;
		cornerDataProvider = new DefaultCornerDataProvider(colHeaderDataProvider, rowHeaderDataProvider);

		// ****************************************
		// body stack layers

		stackLayer = bodyDataLayer;
		bodyDataLayer.setDefaultColumnWidthByPosition(TimeSheetEntryAccessor.timePos, 200);
		stackLayer = new GlazedListsEventLayer<TimeSheetEntry>(stackLayer, entries);
		// optional layers
		stackLayer = new ColumnReorderLayer(stackLayer);
		// stackLayer = new RowReorderLayer(stackLayer); // probably not working
		// with grouping
		stackLayer = new ColumnHideShowLayer(stackLayer);
		// stackLayer = new RowHideShowLayer(stackLayer); // not working with
		// grouping
		// stacklayer= new HoverLayer(stacklayer);

		// mandantory layers
		@SuppressWarnings("unused")
		TreeLayer treeStackLayer;
		stackLayer = treeStackLayer = new TreeLayer(stackLayer, bodyDataLayer.getTreeRowModel());

		SelectionLayer bodySelectionLayer;
		stackLayer = bodySelectionLayer = new SelectionLayer(stackLayer);

		ViewportLayer bodyViewportLayer;
		bodyViewportLayer = new ViewportLayer(stackLayer);
		DefaultColumnHeaderDataLayer columnHeaderDataLayer;
		// example had another layer on top of this
		// ****************************************
		// column
		stackLayer = columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(colHeaderDataProvider);
		ColumnHeaderLayer colHeaderLayer;
		colHeaderLayer = new ColumnHeaderLayer(stackLayer, bodyViewportLayer, bodySelectionLayer);

		SortHeaderLayer<TimeSheetEntry> sortHeaderLayer;
		sortHeaderLayer = new SortHeaderLayer<TimeSheetEntry>(colHeaderLayer, new GlazedListsSortModel<TimeSheetEntry>(sortedEntries, tsea, configRegistry,
		    columnHeaderDataLayer), false);
		bodyDataLayer.setSortModel(sortHeaderLayer.getSortModel());
		// ****************************************
		// row
		DefaultRowHeaderDataLayer rowDataLayer;
		rowDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
		RowHeaderLayer rowHeaderLayer;
		rowHeaderLayer = new RowHeaderLayer(rowDataLayer, bodyViewportLayer, bodySelectionLayer);
		// ****************************************
		// corner // why we are needing this?
		CornerLayer cornerLayer;
		DataLayer cornerDataLayer;
		cornerDataLayer = new DataLayer(cornerDataProvider);
		// cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer,
		// colHeaderLayer);
		cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, sortHeaderLayer);
		//
		ColumnOverrideLabelAccumulator columnLabelAccumulator;
		columnLabelAccumulator = new ColumnOverrideLabelAccumulator(bodyDataLayer);
		bodyDataLayer.setConfigLabelAccumulator(columnLabelAccumulator);
		// example registerColumnLabels(columnLabelAccumulator);
		String[] pn = TimeSheetEntryAccessor.columnLabels;
		for (int i = 0; i < pn.length; ++i) {
			columnLabelAccumulator.registerColumnOverrides(i, pn[i]);
		}

		// ****************************************
		// combining
		// don't configure due to group header

		GridLayer gridLayer = new GridLayer(bodyViewportLayer, sortHeaderLayer, rowHeaderLayer, cornerLayer, false);
		// setting group-by-header on top of grid
		CompositeLayer compositeGridLayer;
		compositeGridLayer = new CompositeLayer(1, 2);
		GroupByHeaderLayer groupByHeaderLayer;
		groupByHeaderLayer = new GroupByHeaderLayer(groupByModel, gridLayer, colHeaderDataProvider);
		compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, groupByHeaderLayer, 0, 0);
		compositeGridLayer.setChildLayer("Grid", gridLayer, 0, 1);
		// add configuration on top
		compositeGridLayer.addConfiguration(new DefaultGridLayerConfiguration(compositeGridLayer));

		// TimeSheetTable nattab = new TimeSheetTable(comp, gridLayer); // was
		// for normal grid
		TimeSheetTable nattab = new TimeSheetTable(comp, compositeGridLayer);

		nattab.viewPortLayer = bodyViewportLayer;
		nattab.selectionLayer = bodySelectionLayer;
		nattab.filterList = filterList;
		nattab.setConfigRegistry(configRegistry);
		// first default style configuration
		nattab.addConfiguration(new DefaultNatTableStyleConfiguration());
		nattab.addConfiguration(new GroupByHeaderMenuConfiguration(nattab, groupByHeaderLayer));

		// add the header menu configuration for adding the column header menu
		// with hide/show actions
		nattab.addConfiguration(new HeaderMenuConfiguration(nattab) {
			// @Override
			// protected PopupMenuBuilder createColumnHeaderMenu(NatTable
			// natTable) {
			// return
			// super.createColumnHeaderMenu(natTable).withHideColumnMenuItem()
			// .withShowAllColumnsMenuItem()
			// //.withAutoResizeSelectedColumnsMenuItem()
			// ;
			// }

			@Override
			protected PopupMenuBuilder createRowHeaderMenu(NatTable natTable) {
				return new PopupMenuBuilder(natTable)
				// .withHideRowMenuItem().withShowAllRowsMenuItem().withAutoResizeSelectedRowsMenuItem();
				    .withMenuItemProvider(new IMenuItemProvider() {

					    @Override
					    public void addMenuItem(final NatTable nt2, Menu popupMenu) {
						    MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
						    menuItem.setText("Remove all previous"); //$NON-NLS-1$
						    menuItem.setEnabled(true);

						    menuItem.addSelectionListener(new SelectionAdapter() {
							    @Override
							    public void widgetSelected(SelectionEvent event) {
								    int rowPosition = MenuItemProviders.getNatEventData(event).getRowPosition();
								    nattab.removePrevious(rowPosition);
							    }
						    });
					    }
				    });

			}

			@Override
			protected PopupMenuBuilder createCornerMenu(NatTable natTable) {
				return super.createCornerMenu(natTable).withShowAllRowsMenuItem().withShowAllColumnsMenuItem().withStateManagerMenuItemProvider()
				// .withMenuItemProvider(new IMenuItemProvider() {
				//
				// @Override
				// public void addMenuItem(NatTable natTable, Menu
				// popupMenu) {
				// MenuItem menuItem = new MenuItem(popupMenu,
				// SWT.PUSH);
				//								menuItem.setText("Toggle Group By Header"); //$NON-NLS-1$
				// menuItem.setEnabled(true);
				//
				// menuItem.addSelectionListener(new SelectionAdapter()
				// {
				// @Override
				// public void widgetSelected(SelectionEvent event) {
				// groupByHeaderLayer.setVisible(!groupByHeaderLayer.isVisible());
				// }
				// });
				//
				// }
				// })
				    .withMenuItemProvider(new IMenuItemProvider() {

					    @Override
					    public void addMenuItem(final NatTable natTable, Menu popupMenu) {
						    MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
						    menuItem.setText("Collapse All"); //$NON-NLS-1$
						    menuItem.setEnabled(true);

						    menuItem.addSelectionListener(new SelectionAdapter() {
							    @Override
							    public void widgetSelected(SelectionEvent event) {
								    natTable.doCommand(new TreeCollapseAllCommand());
							    }
						    });
					    }
				    }).withMenuItemProvider(new IMenuItemProvider() {

					    @Override
					    public void addMenuItem(final NatTable natTable, Menu popupMenu) {
						    MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
						    menuItem.setText("Expand All"); //$NON-NLS-1$
						    menuItem.setEnabled(true);

						    menuItem.addSelectionListener(new SelectionAdapter() {
							    @Override
							    public void widgetSelected(SelectionEvent event) {
								    natTable.doCommand(new TreeExpandAllCommand());
							    }
						    });
					    }
				    })

				;
			}

		});
		// add sorting configuration
		nattab.addConfiguration(new SingleClickSortConfiguration());
		//
		nattab.addConfiguration(new AbstractRegistryConfiguration() {
			@Override
			public void configureRegistry(IConfigRegistry cr) {
				// TODO Auto-generated method stub
				cr.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE, DisplayMode.NORMAL, TimeSheetEntryAccessor.projLabel);
				cr.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE, DisplayMode.NORMAL, TimeSheetEntryAccessor.taskLabel);
				cr.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE, DisplayMode.NORMAL,
				    TimeSheetEntryAccessor.startTimeLabel);
				cr.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DigitDateDisplayConverter(), DisplayMode.NORMAL,
				    TimeSheetEntryAccessor.startTimeLabel);
				cr.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new DigitDateCellEditor(), DisplayMode.NORMAL, TimeSheetEntryAccessor.startTimeLabel);
				cr.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DoubleHoursConverter(), DisplayMode.NORMAL, TimeSheetEntryAccessor.prevLabel);
				cr.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DoubleHoursConverter(), DisplayMode.NORMAL, TimeSheetEntryAccessor.workedLabel);
				cr.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new NonZeroConverter(), DisplayMode.NORMAL, TimeSheetEntryAccessor.billedLabel);
				cr.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DoubleHoursConverter(), DisplayMode.NORMAL, TimeSheetEntryAccessor.remLabel);
				cr.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new LeftBarDecorator(new TextPainter(false, false)), DisplayMode.NORMAL,
				    TimeSheetEntryAccessor.remLabel);
			}

		});
		TimeSheetSumBilled sumBilledProvider;
		sumBilledProvider = new TimeSheetSumBilled();
		nattab.addConfiguration(new AbstractRegistryConfiguration() {
			@Override
			public void configureRegistry(IConfigRegistry cr) {
				cr.registerConfigAttribute(GroupByConfigAttributes.GROUP_BY_SUMMARY_PROVIDER, sumBilledProvider, DisplayMode.NORMAL,
				    GroupByDataLayer.GROUP_BY_COLUMN_PREFIX + 7);
				cr.registerConfigAttribute(GroupByConfigAttributes.GROUP_BY_CHILD_COUNT_PATTERN, "[{0}] - ({1})");
			}
		});
		// add group by header configuration
		nattab.addConfiguration(new GroupByHeaderMenuConfiguration(nattab, groupByHeaderLayer));
		nattab.configure();
		nattab.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(nattab));
		return nattab;
	}

	protected void removePrevious(int rowPosition) {
	  // TODO Auto-generated method stub
	  int idx = this.getRowIndexByPosition(rowPosition);
	  TimeSheetEntry timeSheetEntry = entries.get(idx);
	  Shell shell = this.getShell();
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  String str = "remove all entries before "+ sdf.format(timeSheetEntry.getStartTime()); 
	  boolean openConfirm = MessageDialog.openConfirm(shell, "Removing data", str );
	  if(openConfirm)
	  {
	  	for(int i=0;i<=idx;++i)
	  		entries.remove(0);
	  }
  }

	public void showLast() {
		// this.refresh(); taken over by glazedlist
		// selection layer knows about the number of rows potentially visible
		// viewportlayer must scroll
		Display.getCurrent().timerExec(500, new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ShowRowInViewportCommand srivc = new ShowRowInViewportCommand(viewPortLayer, selectionLayer.getRowCount() - 1);
				doCommand(srivc);
			}
		});
	}

	public void ShowBilled(boolean selection) {
		if (selection)
			filterList.setMatcher(new Matcher<TimeSheetEntry>() {
				@Override
				public boolean matches(TimeSheetEntry item) {
					if (item.billed <= 0)
						return false;
					return true;
				}
			});
		else
			filterList.setMatcher(new Matcher<TimeSheetEntry>() {
				@Override
				public boolean matches(TimeSheetEntry item) {
					// TODO Auto-generated method stub
					return true;
				}
			});

	}

}
