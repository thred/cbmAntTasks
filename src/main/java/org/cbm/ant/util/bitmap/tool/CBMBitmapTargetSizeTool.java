package org.cbm.ant.util.bitmap.tool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.cbm.ant.util.Util;
import org.cbm.ant.util.bitmap.CBMBitmapProjectController;
import org.cbm.ant.util.bitmap.CBMBitmapProjectModel;
import org.cbm.ant.util.bitmap.util.CBMBitmapUtils;

public class CBMBitmapTargetSizeTool extends AbstractCBMBitmapTool implements ActionListener
{

    private static final long serialVersionUID = -3018765182933957393L;

    private final JTextField widthField = new JTextField(4);
    private final JTextField heightField = new JTextField(4);
    private final JCheckBox respectAspectRatioBox = CBMBitmapUtils.createCheckBox("Respect Aspect Ratio", true);
    private final JButton clearButton = CBMBitmapUtils.createButton("Clear", this);
    private final JButton setButton = CBMBitmapUtils.createButton("Set Size", this);

    public CBMBitmapTargetSizeTool()
    {
        super("target-size.png", "Target Size", "Sets the size of the target image.");

        widthField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                onWidthChange();
            }
        });
        widthField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                onWidthChange();
            }
        });

        heightField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                onHeightChange();
            }
        });
        heightField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                onHeightChange();
            }
        });

        add(CBMBitmapUtils.createLabeledPanel("Width:", widthField));
        add(CBMBitmapUtils.createLabeledPanel("Height:", heightField));
        add(respectAspectRatioBox);
        add(clearButton);
        add(setButton);
    }

    @Override
    public void onCBMBitmapProjectUpdate(CBMBitmapProjectController controller, CBMBitmapProjectModel model,
        PropertyChangeEvent event)
    {
        BufferedImage sourceImage = model != null ? model.getSourceImage() : null;

        if (sourceImage == null)
        {
            widthField.setText("");
            heightField.setText("");

            return;
        }

        if (event != null && "targetWidth".equals(event.getPropertyName()))
        {
            Integer targetWidth = model.getTargetWidth();

            if (targetWidth == null)
            {
                targetWidth = sourceImage.getWidth();
            }

            widthField.setText(String.valueOf(targetWidth));
        }
        else if (event != null && "targetHeight".equals(event.getPropertyName()))
        {
            Integer targetHeight = model.getTargetHeight();

            if (targetHeight == null)
            {
                targetHeight = sourceImage.getHeight();
            }

            heightField.setText(String.valueOf(targetHeight));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        CBMBitmapProjectController controller = getActiveController();

        if (controller != null)
        {
            if (e.getSource() == clearButton)
            {
                controller.clearTargetSize();
            }
            else if (e.getSource() == setButton)
            {
                controller.targetSize(getWidthValue(), getHeightValue());
            }
        }
    }

    public int getWidthValue()
    {
        try
        {
            return Util.parseHex(widthField.getText().trim());
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }

    public int getHeightValue()
    {
        try
        {
            return Util.parseHex(heightField.getText().trim());
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }

    public double getAspectRatio()
    {
        CBMBitmapProjectModel model = getActiveModel();

        if (model == null)
        {
            return 1;
        }

        BufferedImage sourceImage = model.getSourceImage();

        if (sourceImage == null)
        {
            return 1;
        }

        return (double) sourceImage.getWidth() / sourceImage.getHeight();
    }

    protected void onWidthChange()
    {
        final int width = getWidthValue();
        int height = getHeightValue();

        setButton.setEnabled(width > 0 && height > 0);

        if (width > 0 && respectAspectRatioBox.isSelected())
        {
            SwingUtilities.invokeLater(() -> heightField.setText(String.valueOf((int) (width / getAspectRatio()))));
        }
    }

    protected void onHeightChange()
    {
        int width = getWidthValue();
        final int height = getHeightValue();

        setButton.setEnabled(width > 0 && height > 0);

        if (height > 0 && respectAspectRatioBox.isSelected())
        {
            SwingUtilities.invokeLater(() -> widthField.setText(String.valueOf((int) (getAspectRatio() * height))));
        }
    }

}
