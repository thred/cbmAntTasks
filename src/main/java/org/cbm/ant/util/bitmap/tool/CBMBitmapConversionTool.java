package org.cbm.ant.util.bitmap.tool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JComboBox;

import org.cbm.ant.cbm.bitmap.CBMColorSpace;
import org.cbm.ant.cbm.bitmap.GraphicsMode;
import org.cbm.ant.util.bitmap.CBMBitmapProjectController;
import org.cbm.ant.util.bitmap.CBMBitmapProjectModel;
import org.cbm.ant.util.bitmap.util.CBMBitmapUtils;

public class CBMBitmapConversionTool extends AbstractCBMBitmapTool implements ActionListener
{

    private static final long serialVersionUID = -3018765182933957393L;

    private final JComboBox<CBMColorSpace> colorSpaceBox = new JComboBox<>(CBMColorSpace.values());
    private final JComboBox<GraphicsMode> graphicsModeBox = new JComboBox<>(GraphicsMode.values());

    public CBMBitmapConversionTool()
    {
        super("conversion.png", "Conversion", "Set additional conversion parameters.");

        colorSpaceBox.addActionListener(this);
        graphicsModeBox.addActionListener(this);

        add(CBMBitmapUtils.createLabeledPanel("Color Space:", colorSpaceBox));
        add(CBMBitmapUtils.createLabeledPanel("Graphics Mode:", graphicsModeBox));
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
            controller.getModel().setColorSpace((CBMColorSpace) colorSpaceBox.getSelectedItem());
            controller.getModel().setGraphicsMode((GraphicsMode) graphicsModeBox.getSelectedItem());
            controller.recalculate();
        }
    }

}
