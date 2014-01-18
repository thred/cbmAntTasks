package org.cbm.ant.util.bitmap;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.cbm.ant.util.Prefs;
import org.cbm.ant.util.WindowUtils;

public class CBMBitmapTool
{

    private static CBMBitmapToolFrame frame;

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
        IllegalAccessException, UnsupportedLookAndFeelException
    {
        Prefs.setDefault(CBMBitmapTool.class);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        frame =
            WindowUtils.setAndRecordState(Prefs.getDefault(), "CBMBitmapTool", WindowUtils.packAndCenter(new CBMBitmapToolFrame(WindowUtils
                .getRecordedGraphicsConfiguration(Prefs.getDefault(), "CBMBitmapTool", CBMBitmapToolFrame.class))));

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

    public static CBMBitmapToolFrame getFrame()
    {
        return frame;
    }

    public static void exit()
    {
        frame.setVisible(false);
        frame.dispose();
    }

}
