package org.cbm.ant.util.bitmap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JComboBox;

import org.cbm.ant.util.CBMBitmapDither;

public class CBMBitmapDitherModeTool extends AbstractCBMBitmapTool implements ActionListener
{

	private static final long serialVersionUID = -3018765182933957393L;

	private final JComboBox ditherBox = new JComboBox(CBMBitmapDither.values());

	public CBMBitmapDitherModeTool()
	{
		super("dither-mode.png", "Dither Mode", "Sets the dithering mode.");

		ditherBox.addActionListener(this);

		add(CBMBitmapUtils.createLabeledPanel("Dither Mode:", ditherBox));
	}

	@Override
	public void onCBMBitmapProjectUpdate(CBMBitmapProjectController controller, CBMBitmapProjectModel model,
			PropertyChangeEvent event)
	{
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		CBMBitmapProjectController controller = getActiveController();

		if (controller != null)
		{
			controller.setDither((CBMBitmapDither) ditherBox.getSelectedItem());
			controller.recalculate();
		}
	}

}
