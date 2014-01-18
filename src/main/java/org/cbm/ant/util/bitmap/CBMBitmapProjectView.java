package org.cbm.ant.util.bitmap;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

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

        splitPane.setLeftComponent(sourceScrollPane);
        splitPane.setRightComponent(targetScrollPane);
        splitPane.setDividerLocation(0.5);

        JPanel panel = CBMBitmapToolUtils.createBorderPanel(splitPane);

        panel.setBackground(Color.WHITE);

        add(panel, BorderLayout.CENTER);
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
