package org.cbm.ant.util.bitmap;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CBMBitmapZoomFitAction extends AbstractCBMBitmapAction
{

    private static final long serialVersionUID = 6114587570625893678L;

    public CBMBitmapZoomFitAction()
    {
        super("Zoom Fit");

        putValue(MNEMONIC_KEY, KeyEvent.VK_F);
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

        controller.zoomFit();
    }
}
