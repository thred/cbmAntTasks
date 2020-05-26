package org.cbm.ant.util.bitmap.tool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cbm.ant.cbm.bitmap.CBMBitmapDither;
import org.cbm.ant.util.bitmap.CBMBitmapProjectController;
import org.cbm.ant.util.bitmap.CBMBitmapProjectModel;
import org.cbm.ant.util.bitmap.util.CBMBitmapSlider;
import org.cbm.ant.util.bitmap.util.CBMBitmapUtils;

public class CBMBitmapDitherModeTool extends AbstractCBMBitmapTool implements ActionListener, ChangeListener
{

    private static final long serialVersionUID = -3018765182933957393L;

    private final JComboBox ditherBox = new JComboBox(CBMBitmapDither.values());
    private final CBMBitmapSlider ditherStrengthSlider = CBMBitmapUtils.createSlider(100, 0, 100, 5, 25, this);

    public CBMBitmapDitherModeTool()
    {
        super("dither-mode.png", "Dither Mode", "Sets the dithering mode.");

        ditherBox.addActionListener(this);

        add(CBMBitmapUtils.createLabeledPanel("Dither Mode:", ditherBox));
        add(CBMBitmapUtils.createLabeledPanel("Strength:", ditherStrengthSlider));
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

    @Override
    public void stateChanged(ChangeEvent e)
    {
        CBMBitmapProjectController controller = getActiveController();

        if (controller != null)
        {
            controller.getModel().setDitherStrength(ditherStrengthSlider.getValue() / 100f);
            controller.recalculate();
        }
    }

}
