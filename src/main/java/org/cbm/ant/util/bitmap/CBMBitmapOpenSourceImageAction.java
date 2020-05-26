package org.cbm.ant.util.bitmap;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.cbm.ant.util.Prefs;

public class CBMBitmapOpenSourceImageAction extends AbstractCBMBitmapAction
{

    private static final long serialVersionUID = 6114587570625893678L;

    public CBMBitmapOpenSourceImageAction()
    {
        super("open-source-image", "Open Source Image...");

        putValue(MNEMONIC_KEY, KeyEvent.VK_O);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl O"));
    }

    @Override
    protected boolean computeEnabled()
    {
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        CBMBitmapFrame frame = CBMBitmapUtility.getFrame();
        CBMBitmapProjectController controller = frame.getActiveController();

        if (controller == null)
        {
            controller = frame.createController();
        }

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "png", "gif");

        chooser.setFileFilter(filter);

        File selected = new File(Prefs.getDefault().get("open", System.getProperty("user.home")));

        if (selected.isDirectory())
        {
            chooser.setCurrentDirectory(selected);
        }
        else if (selected.isFile())
        {
            chooser.setSelectedFile(selected);
        }

        int returnVal = chooser.showOpenDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = chooser.getSelectedFile();

            try
            {
                Prefs.getDefault().set("open", file.getAbsolutePath());

                BufferedImage image = ImageIO.read(file);

                controller.setSourceImage(file, image);
            }
            catch (IOException e)
            {
                e.printStackTrace(System.err);

                JOptionPane.showMessageDialog(frame,
                    "Failed to load image from \"" + file + "\".\nSee log for more information.",
                    "Failed to load image", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
