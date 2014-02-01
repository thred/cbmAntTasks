package org.cbm.ant.util.bitmap;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import org.cbm.ant.util.bitmap.util.CBMBitmapCanvas;
import org.cbm.ant.util.bitmap.util.CBMBitmapUtils;

public class CBMBitmapProjectView extends JPanel
{

	private static final long serialVersionUID = 2081467348467784213L;

	private final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private final CBMBitmapCanvas sourceCanvas = new CBMBitmapCanvas();
	private final CBMBitmapCanvas targetCanvas = new CBMBitmapCanvas();
	private final JScrollPane sourceScrollPane = new JScrollPane(sourceCanvas);
	private final JScrollPane targetScrollPane = new JScrollPane(targetCanvas);

	private final CBMBitmapProjectModel model;

	public CBMBitmapProjectView(CBMBitmapProjectModel model)
	{
		super(new BorderLayout());

		this.model = model;

		JPanel sourcePanel = new JPanel(new BorderLayout());

		sourcePanel.add(sourceScrollPane, BorderLayout.CENTER);

		JToolBar sourceToolBar = new JToolBar();

		sourceToolBar.add(CBMBitmapUtils.get(CBMBitmapZoomInSourceAction.class));
		sourceToolBar.add(CBMBitmapUtils.get(CBMBitmapZoomOutSourceAction.class));
		sourceToolBar.add(CBMBitmapUtils.get(CBMBitmapZoomFitSourceAction.class));
		sourceToolBar.add(CBMBitmapUtils.get(CBMBitmapZoom100SourceAction.class));
		sourceToolBar.add(CBMBitmapUtils.get(CBMBitmapZoom200SourceAction.class));
		sourceToolBar.add(CBMBitmapUtils.get(CBMBitmapZoom300SourceAction.class));
		sourceToolBar.add(CBMBitmapUtils.get(CBMBitmapZoom400SourceAction.class));
		sourceToolBar.setFloatable(false);
		//        sourceToolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

		sourcePanel.add(sourceToolBar, BorderLayout.SOUTH);

		JPanel targetPanel = new JPanel(new BorderLayout());

		targetPanel.add(targetScrollPane, BorderLayout.CENTER);

		JToolBar targetToolBar = new JToolBar();

		targetToolBar.add(CBMBitmapUtils.get(CBMBitmapZoomInTargetAction.class));
		targetToolBar.add(CBMBitmapUtils.get(CBMBitmapZoomOutTargetAction.class));
		targetToolBar.add(CBMBitmapUtils.get(CBMBitmapZoomFitTargetAction.class));
		targetToolBar.add(CBMBitmapUtils.get(CBMBitmapZoom100TargetAction.class));
		targetToolBar.add(CBMBitmapUtils.get(CBMBitmapZoom200TargetAction.class));
		targetToolBar.add(CBMBitmapUtils.get(CBMBitmapZoom300TargetAction.class));
		targetToolBar.add(CBMBitmapUtils.get(CBMBitmapZoom400TargetAction.class));
		targetToolBar.setFloatable(false);
		//        targetToolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

		targetPanel.add(targetToolBar, BorderLayout.SOUTH);

		splitPane.setLeftComponent(sourcePanel);
		splitPane.setRightComponent(targetPanel);
		splitPane.setDividerLocation(0.5);

		JPanel panel = CBMBitmapUtils.createBorderPanel(splitPane);

		panel.setBackground(Color.WHITE);

		add(panel, BorderLayout.CENTER);
	}

	public CBMBitmapProjectModel getModel()
	{
		return model;
	}

	public JSplitPane getSplitPane()
	{
		return splitPane;
	}

	public CBMBitmapCanvas getSourceCanvas()
	{
		return sourceCanvas;
	}

	public CBMBitmapCanvas getTargetCanvas()
	{
		return targetCanvas;
	}

	public JScrollPane getSourceScrollPane()
	{
		return sourceScrollPane;
	}

	public JScrollPane getTargetScrollPane()
	{
		return targetScrollPane;
	}

}
