package org.cbm.ant.util.bitmap;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;

public class CBMBitmapCloseProjectAction extends AbstractCBMBitmapAction
{

    private static final long serialVersionUID = 6114587570625893678L;

    public CBMBitmapCloseProjectAction()
    {
        super("Close Project");

        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("close-project.png")));
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
    }

    @Override
    protected boolean computeEnabled()
    {
        return getActiveController() != null;
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        CBMBitmapFrame frame = CBMBitmapUtility.getFrame();
        CBMBitmapProjectController controller = frame.getActiveController();

        frame.removeController(controller);
    }

}
