package org.cbm.ant.util.bitmap;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

public class CBMBitmapZoomOutSourceAction extends AbstractCBMBitmapAction
{

	private static final long serialVersionUID = 6114587570625893678L;

	public CBMBitmapZoomOutSourceAction()
	{
		super("zoom-out", "Source Image Zoom Out");

		putValue(MNEMONIC_KEY, KeyEvent.VK_O);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("MINUS"));
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

		controller.sourceZoom(0.5);
	}

}
