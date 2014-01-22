package org.cbm.ant.util.bitmap;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.cbm.ant.util.Prefs;
import org.cbm.ant.util.WindowUtils;

public class CBMBitmapUtility
{

    private static CBMBitmapFrame frame;

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
        IllegalAccessException, UnsupportedLookAndFeelException
    {
        Prefs.setDefault(CBMBitmapUtility.class);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        frame =
            WindowUtils.setAndRecordState(Prefs.getDefault(), "CBMBitmapTool", WindowUtils.packAndCenter(new CBMBitmapFrame(WindowUtils
                .getRecordedGraphicsConfiguration(Prefs.getDefault(), "CBMBitmapTool", CBMBitmapFrame.class))));

        frame.addWindowListener(new WindowAdapter()
        {

            @Override
            public void windowClosing(WindowEvent e)
            {
                exit();
            }

        });
        frame.setVisible(true);
    }

    public static CBMBitmapFrame getFrame()
    {
        return frame;
    }

    public static void exit()
    {
        frame.setVisible(false);
        frame.dispose();
    }

}
