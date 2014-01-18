package org.cbm.ant.util.bitmap;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class CBMBitmapResizeDialog extends JDialog implements ActionListener
{

    private static final long serialVersionUID = 7638777880972957808L;

    private final JTextField widthField = new JTextField(4);
    private final JTextField heightField = new JTextField(4);
    private final JCheckBox respectAspectRatioBox = new JCheckBox("Respect Aspect Ratio", true);
    private final JButton okButton = CBMBitmapToolUtils.createButton("Ok", this);
    private final JButton cancelButton = CBMBitmapToolUtils.createButton("Cancel", this);

    private boolean ok = false;
    private double aspectRatio = 0;

    public CBMBitmapResizeDialog()
    {
        super(CBMBitmapTool.getFrame(), "Resize", true);

        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        CBMBitmapProjectController controller = CBMBitmapTool.getFrame().getActiveController();
        BufferedImage sourceImage = controller.getModel().getSourceImage();

        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();

        aspectRatio = (double) width / height;

        g.gridx = 1;
        g.gridy = 1;
        g.gridwidth = 1;
        g.gridheight = 1;
        g.insets = new Insets(2, 4, 2, 4);
        g.anchor = GridBagConstraints.CENTER;
        g.fill = GridBagConstraints.HORIZONTAL;

        panel.add(CBMBitmapToolUtils.createLabel("Size:", widthField), g);

        g.weightx = 1;
        g.gridx += 1;

        widthField.setText(String.valueOf(width));
        widthField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                onWidthChange();
            }
        });

        panel.add(widthField, g);

        g.weightx = 0;
        g.gridx += 1;

        panel.add(new JLabel("x"), g);

        g.weightx = 1;
        g.gridx += 1;

        heightField.setText(String.valueOf(height));
        heightField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                onHeightChange();
            }
        });

        panel.add(heightField, g);

        g.gridx = 2;
        g.gridy += 1;
        g.gridwidth = 3;

        panel.add(respectAspectRatioBox, g);

        add(CBMBitmapToolUtils.createBorderPanel(panel), BorderLayout.CENTER);
        add(CBMBitmapToolUtils.createBorderPanel(CBMBitmapToolUtils.createButtonPanel(okButton, cancelButton)),
            BorderLayout.SOUTH);
    }

    public boolean consume()
    {
        pack();
        setLocationRelativeTo(CBMBitmapTool.getFrame());

        setVisible(true);

        return ok;
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
            return Integer.decode(widthField.getText().trim());
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == okButton)
        {
            ok = true;
            setVisible(false);
        }
        else if (e.getSource() == cancelButton)
        {
            ok = false;
            setVisible(false);
        }
    }

    protected void onWidthChange()
    {
        final int width = getWidthValue();
        int height = getHeightValue();

        if ((width < 0) || (height < 0))
        {
            okButton.setEnabled(false);
            return;
        }

        okButton.setEnabled(true);

        if (respectAspectRatioBox.isSelected())
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    heightField.setText(String.valueOf((int) (width * aspectRatio)));
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
            okButton.setEnabled(false);
            return;
        }

        okButton.setEnabled(true);

        if (respectAspectRatioBox.isSelected())
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    widthField.setText(String.valueOf((int) (height / aspectRatio)));
                }
            });
        }
    }

}
