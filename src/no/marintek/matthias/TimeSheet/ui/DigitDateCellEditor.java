/**
 * 
 */
package no.marintek.matthias.TimeSheet.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.nebula.widgets.nattable.edit.editor.AbstractCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author mnow
 *
 */
public class DigitDateCellEditor extends AbstractCellEditor {

	private int[] digits;
	private Text text;
	private Date curDate;
	private int curPos;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor#getEditorValue
	 * ()
	 */
	@Override
	public Object getEditorValue() {
		// TODO Auto-generated method stub
		return curDate;
		//
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor#setEditorValue
	 * (java.lang.Object)
	 */
	@Override
	public void setEditorValue(Object value) {
		// TODO Auto-generated method stub
		digits = new int[14];
		if (value instanceof Date) {
			curDate = (Date) value;
		} else {
			if (curDate == null)
				curDate = new Date();
		}
		// setting initial value
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String f = sdf.format(curDate);
		if (f.length() > 14) {
			System.out.println("problem 2015-01-26/1");
		}
		char[] cs = f.toCharArray();
		for (int i = 0; i < 14; ++i)
			digits[i] = cs[i] - '0';
		this.curPos = 14;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor#getEditorControl
	 * ()
	 */
	@Override
	public Control getEditorControl() {
		// TODO Auto-generated method stub
		return this.text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor#createEditorControl
	 * (org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Text createEditorControl(Composite parent) {
		int style = HorizontalAlignmentEnum.getSWTStyle(this.cellStyle);
		final Text textControl = new Text(parent, style);
		// set style information configured in the associated cell style
		textControl.setBackground(this.cellStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR));
		textControl.setForeground(this.cellStyle.getAttributeValue(CellStyleAttributes.FOREGROUND_COLOR));
		textControl.setFont(this.cellStyle.getAttributeValue(CellStyleAttributes.FONT));
		textControl.setCursor(null);
		// add a key listener that will commit or close the editor for special key
		// strokes
		// and executes conversion/validation on input to the editor
		textControl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				event.doit = false;
				if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR || event.keyCode == SWT.TAB) {
					Calendar cal;
					cal = Calendar.getInstance();
					cal.set(((digits[0] * 10 + digits[1]) * 10 + digits[2]) * 10 + digits[3], // years
					    digits[4] * 10 + digits[5] - 1, // month -1 pecularity
					    digits[6] * 10 + digits[7], // day
					    digits[8] * 10 + digits[9], // hour
					    digits[10] * 10 + digits[11], digits[12] * 10 + digits[13]);
					curDate = cal.getTime();
					commit(MoveDirectionEnum.RIGHT);
					close();
				}
				if (event.keyCode == SWT.ESC) {
					close();
				}
				if (Character.isDigit(event.character)) {
					if (curPos > 0)
						--curPos;
					for (int i = curPos; i < 13; ++i)
						digits[i] = digits[i + 1];
					digits[13] = event.character - '0';
					text.setText(getString());
				}
			}

		});

		return textControl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractCellEditor#activateCell
	 * (org.eclipse.swt.widgets.Composite, java.lang.Object)
	 */
	@Override
	protected Control activateCell(Composite parent, Object originalCanonicalValue) {
		text = createEditorControl(parent);

		setEditorValue(originalCanonicalValue);
		this.text.forceFocus();
//		this.text.setText(getString());
		this.text.setText("enter digits only");
		return text;
	}

	private String getString() {
		String s = String.format("%d%d%d%d-%d%d-%d%d %d%d:%d%d:%d%d", digits[0], digits[1], digits[2], digits[3], digits[4], digits[5], digits[6], digits[7],
		    digits[8], digits[9], digits[10], digits[11], digits[12], digits[13]);
		return s;
	}

}
