package no.marintek.matthias.TimeSheet.ui;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.CellPainterWrapper;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class LeftBarDecorator extends CellPainterWrapper {

	BackgroundPainter bgP= new BackgroundPainter();

	/**
	 * Instantiates a new left bar decorator.
	 *
	 * @param interiorPainter the interior painter
	 */
	public LeftBarDecorator(ICellPainter interiorPainter) {
		super(interiorPainter);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.painter.cell.CellPainterWrapper#paintCell(org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell, org.eclipse.swt.graphics.GC, org.eclipse.swt.graphics.Rectangle, org.eclipse.nebula.widgets.nattable.config.IConfigRegistry)
	 */
	@Override
	public void paintCell(ILayerCell cell, GC gc, Rectangle rectangle, IConfigRegistry configRegistry) {
		
		Pattern originalBackgroundPattern = gc.getBackgroundPattern();
		bgP.paintCell(cell, gc, rectangle, configRegistry);
		double d = ((Double) cell.getDataValue()).doubleValue();
		if (d < 0) {

			Rectangle bar = new Rectangle(rectangle.x, rectangle.y, (int) (rectangle.width * (-d)*2 ), rectangle.height);
			Color bgColor = new Color(Display.getCurrent(),new RGB(183,232,216));
			gc.setBackground(bgColor);
			gc.fillRectangle(bar);
			bgColor.dispose();
			gc.setBackgroundPattern(originalBackgroundPattern);
		}

		super.paintCell(cell, gc, rectangle, configRegistry);
	}

}
