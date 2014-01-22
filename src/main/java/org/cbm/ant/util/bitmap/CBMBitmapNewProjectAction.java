package org.cbm.ant.util.bitmap;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

public class CBMBitmapNewProjectAction extends AbstractCBMBitmapAction
{

    private static final long serialVersionUID = 6114587570625893678L;

    public CBMBitmapNewProjectAction()
    {
        super("New Project");

        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("new-project.png")));
        putValue(MNEMONIC_KEY, KeyEvent.VK_P);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl P"));
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

        frame.createController();
    }

}
