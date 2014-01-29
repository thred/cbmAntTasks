package org.cbm.ant.util.bitmap.tool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cbm.ant.util.CBMBitmapEmboss;
import org.cbm.ant.util.bitmap.CBMBitmapProjectController;
import org.cbm.ant.util.bitmap.CBMBitmapProjectModel;
import org.cbm.ant.util.bitmap.util.CBMBitmapSlider;
import org.cbm.ant.util.bitmap.util.CBMBitmapUtils;

public class CBMBitmapEmbossTool extends AbstractCBMBitmapTool implements ActionListener, ChangeListener
{

	private static final long serialVersionUID = -3018765182933957393L;

	private final JComboBox embossBox = new JComboBox(CBMBitmapEmboss.values());
	private final CBMBitmapSlider embossStrengthSlider = CBMBitmapUtils.createSlider(100, 0, 500, 10, 50, this);

	public CBMBitmapEmbossTool()
	{
		super("emboss.png", "Dither Mode", "Sets the dithering mode.");

		embossBox.addActionListener(this);

		add(CBMBitmapUtils.createLabeledPanel("Emboss Bright Spot:", embossBox));
		add(CBMBitmapUtils.createLabeledPanel("Strength:", embossStrengthSlider));
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
			controller.getModel().setEmboss((CBMBitmapEmboss) embossBox.getSelectedItem());
			controller.recalculate();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		CBMBitmapProjectController controller = getActiveController();

		if (controller != null)
		{
			controller.getModel().setEmbossStrength(embossStrengthSlider.getValue() / 100f);
			controller.recalculate();
		}
	}

}
