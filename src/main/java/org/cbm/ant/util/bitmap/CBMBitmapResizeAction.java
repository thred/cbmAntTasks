package org.cbm.ant.util.bitmap;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CBMBitmapResizeAction extends AbstractCBMBitmapAction
{

    private static final long serialVersionUID = 6114587570625893678L;

    public CBMBitmapResizeAction()
    {
        super("Resize");

        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
    }

    @Override
    protected boolean computeEnabled()
    {
        CBMBitmapProjectController controller = getActiveController();

        return (controller != null) && (controller.getModel().getSourceImage() != null);
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        CBMBitmapProjectController controller = getActiveController();
        CBMBitmapResizeDialog dialog = new CBMBitmapResizeDialog();
        
		if (dialog.consume()) {
        	controller.resize(dialog.getWidthValue(), dialog.getHeightValue());
        }
    }
}
