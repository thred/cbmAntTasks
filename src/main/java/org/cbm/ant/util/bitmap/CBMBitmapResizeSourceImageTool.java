package org.cbm.ant.util.bitmap;

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

public class CBMBitmapResizeSourceImageTool extends AbstractCBMBitmapTool implements ActionListener
{

    private static final long serialVersionUID = -3018765182933957393L;

    private final JTextField widthField = new JTextField(4);
    private final JTextField heightField = new JTextField(4);
    private final JCheckBox respectAspectRatioBox = CBMBitmapUtils.createCheckBox("Respect Aspect Ratio", true);
    private final JButton executeButton = CBMBitmapUtils.createButton("Resize", this);

    private double aspectRatio = 0;

    public CBMBitmapResizeSourceImageTool()
    {
        super("resize-source-image.png", "Resize Source Image", "Resizes the source image.");

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
        add(executeButton);
    }

    @Override
    public void onCBMBitmapProjectUpdate(CBMBitmapProjectController controller, CBMBitmapProjectModel model,
        PropertyChangeEvent event)
    {
        BufferedImage sourceImage = (model != null) ? model.getSourceImage() : null;

        if (sourceImage == null)
        {
            widthField.setText("");
            heightField.setText("");
            aspectRatio = 1;
        }
        else
        {
            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();

            widthField.setText(String.valueOf(width));
            heightField.setText(String.valueOf(height));
            aspectRatio = (double) width / height;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        CBMBitmapProjectController controller = getActiveController();

        if (controller != null)
        {
            controller.resize(getWidthValue(), getHeightValue());
        }
    }

    public int getWidthValue()
    {
        try
        {
            return Integer.decode(widthField.getText().trim());
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
            return Integer.decode(heightField.getText().trim());
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }

    protected void onWidthChange()
    {
        final int width = getWidthValue();
        int height = getHeightValue();

        if ((width < 0) || (height < 0))
        {
            executeButton.setEnabled(false);
            return;
        }

        executeButton.setEnabled(true);

        if (respectAspectRatioBox.isSelected())
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    heightField.setText(String.valueOf((int) (width / aspectRatio)));
                }
            });
        }
    }

    protected void onHeightChange()
    {
        int width = getWidthValue();
        final int height = getHeightValue();

        if ((width < 0) || (height < 0))
        {
            executeButton.setEnabled(false);
            return;
        }

        executeButton.setEnabled(true);

        if (respectAspectRatioBox.isSelected())
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    widthField.setText(String.valueOf((int) (aspectRatio * height)));
                }
            });
        }
    }

}
