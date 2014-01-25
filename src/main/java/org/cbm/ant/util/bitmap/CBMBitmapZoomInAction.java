package org.cbm.ant.util.bitmap;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

public class CBMBitmapZoomInAction extends AbstractCBMBitmapAction
{

    private static final long serialVersionUID = 6114587570625893678L;

    public CBMBitmapZoomInAction()
    {
        super("zoom-in", "Zoom In");

        putValue(MNEMONIC_KEY, KeyEvent.VK_I);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("PLUS"));
    }

    @Override
    protected boolean computeEnabled()
    {
        return getActiveController() != null;
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        CBMBitmapProjectController controller = getActiveController();

        if (controller == null)
        {
            return;
        }

        controller.zoom(2);
    }

}
