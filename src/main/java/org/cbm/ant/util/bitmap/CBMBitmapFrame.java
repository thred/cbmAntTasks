package org.cbm.ant.util.bitmap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cbm.ant.util.bitmap.util.CBMBitmapToolPanel;
import org.cbm.ant.util.bitmap.util.CBMBitmapUtils;

public class CBMBitmapFrame extends JFrame
{
    private static final long serialVersionUID = 5112467837914889513L;

    private final JMenuBar menuBar = new JMenuBar();
    private final CBMBitmapToolPanel toolPanel = new CBMBitmapToolPanel();
    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final Collection<CBMBitmapProjectController> controllers = new ArrayList<CBMBitmapProjectController>();

    public CBMBitmapFrame(GraphicsConfiguration gc)
    {
        super(gc);

        setTitle("CBM Bitmap Tool");
        setLayout(new BorderLayout());

        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");

        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(new JMenuItem(CBMBitmapUtils.get(CBMBitmapNewProjectAction.class)));
        fileMenu.add(new JMenuItem(CBMBitmapUtils.get(CBMBitmapCloseProjectAction.class)));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(CBMBitmapUtils.get(CBMBitmapExitAction.class)));

        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");

        editMenu.setMnemonic(KeyEvent.VK_E);

        menuBar.add(editMenu);

        JMenu viewMenu = new JMenu("View");

        viewMenu.setMnemonic(KeyEvent.VK_V);

        viewMenu.add(new JMenuItem(CBMBitmapUtils.get(CBMBitmapToggleSplitAction.class)));
        viewMenu.addSeparator();
        viewMenu.add(new JMenuItem(CBMBitmapUtils.get(CBMBitmapZoomInSourceAction.class)));
        viewMenu.add(new JMenuItem(CBMBitmapUtils.get(CBMBitmapZoomOutSourceAction.class)));
        viewMenu.add(new JMenuItem(CBMBitmapUtils.get(CBMBitmapZoomFitSourceAction.class)));
        viewMenu.add(new JMenuItem(CBMBitmapUtils.get(CBMBitmapZoom100SourceAction.class)));
        viewMenu.addSeparator();
        viewMenu.add(new JMenuItem(CBMBitmapUtils.get(CBMBitmapZoomInTargetAction.class)));
        viewMenu.add(new JMenuItem(CBMBitmapUtils.get(CBMBitmapZoomOutTargetAction.class)));
        viewMenu.add(new JMenuItem(CBMBitmapUtils.get(CBMBitmapZoomFitTargetAction.class)));
        viewMenu.add(new JMenuItem(CBMBitmapUtils.get(CBMBitmapZoom100TargetAction.class)));

        menuBar.add(viewMenu);

        JMenu sourceMenu = new JMenu("Source");

        sourceMenu.setMnemonic(KeyEvent.VK_S);

        sourceMenu.add(new JMenuItem(CBMBitmapUtils.get(CBMBitmapOpenSourceImageAction.class)));
        sourceMenu.addSeparator();
        sourceMenu.add(new JCheckBoxMenuItem(CBMBitmapUtils.get(CBMBitmapResizeSourceImageToolAction.class)));

        menuBar.add(sourceMenu);

        JMenu targetMenu = new JMenu("Target");

        targetMenu.setMnemonic(KeyEvent.VK_T);

        targetMenu.add(new JCheckBoxMenuItem(CBMBitmapUtils.get(CBMBitmapTargetSizeToolAction.class)));
        targetMenu.add(new JCheckBoxMenuItem(CBMBitmapUtils.get(CBMBitmapDitherModeToolAction.class)));
        targetMenu.add(new JCheckBoxMenuItem(CBMBitmapUtils.get(CBMBitmapEmbossToolAction.class)));
        targetMenu.add(new JCheckBoxMenuItem(CBMBitmapUtils.get(CBMBitmapContrastAndBrightnessToolAction.class)));
        targetMenu.add(new JCheckBoxMenuItem(CBMBitmapUtils.get(CBMBitmapPaletteToolAction.class)));

        menuBar.add(targetMenu);

        toolPanel.addTool(CBMBitmapUtils.get(CBMBitmapNewProjectAction.class));
        toolPanel.addTool(CBMBitmapUtils.get(CBMBitmapCloseProjectAction.class));
        toolPanel.addSeparator();
        toolPanel.addTool(CBMBitmapUtils.get(CBMBitmapOpenSourceImageAction.class));
        toolPanel.addSeparator();
        toolPanel.addTool(CBMBitmapUtils.get(CBMBitmapResizeSourceImageToolAction.class));
        toolPanel.addSeparator();
        toolPanel.addTool(CBMBitmapUtils.get(CBMBitmapTargetSizeToolAction.class));
        toolPanel.addTool(CBMBitmapUtils.get(CBMBitmapDitherModeToolAction.class));
        toolPanel.addTool(CBMBitmapUtils.get(CBMBitmapEmbossToolAction.class));
        toolPanel.addTool(CBMBitmapUtils.get(CBMBitmapContrastAndBrightnessToolAction.class));
        toolPanel.addTool(CBMBitmapUtils.get(CBMBitmapPaletteToolAction.class));

        tabbedPane.setVisible(false);
        tabbedPane.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                CBMBitmapProjectController controller = getController(tabbedPane.getSelectedComponent());

                toolPanel.onCBMBitmapProjectUpdate(controller, (controller != null) ? controller.getModel() : null,
                    null);
            }
        });

        JPanel panel = CBMBitmapUtils.createBorderPanel(tabbedPane);

        add(toolPanel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    public CBMBitmapToolPanel getToolPanel()
    {
        return toolPanel;
    }

    public CBMBitmapProjectController createController()
    {
        final CBMBitmapProjectModel model = new CBMBitmapProjectModel();
        final CBMBitmapProjectController controller = new CBMBitmapProjectController(model);

        model.addPropertyChangeListener(new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent event)
            {
                toolPanel.onCBMBitmapProjectUpdate(controller, model, event);
            }
        });

        controllers.add(controller);
        tabbedPane.addTab(model.getName(), controller.getView());
        tabbedPane.setVisible(tabbedPane.getTabCount() > 0);

        return controller;
    }

    private CBMBitmapProjectController getController(Component component)
    {
        if (component == null)
        {
            return null;
        }

        for (CBMBitmapProjectController controller : controllers)
        {
            if (controller.getView() == component)
            {
                return controller;
            }
        }

        return null;
    }

    public CBMBitmapProjectController getActiveController()
    {
        return getController(tabbedPane.getSelectedComponent());
    }

    public void removeController(CBMBitmapProjectController controller)
    {
        controllers.remove(controller);
        tabbedPane.remove(controller.getView());
        tabbedPane.setVisible(tabbedPane.getTabCount() > 0);
    }

}
