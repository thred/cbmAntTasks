package org.cbm.ant.util.bitmap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class CBMBitmapToolFrame extends JFrame
{
    private static final long serialVersionUID = 5112467837914889513L;

    private final JMenuBar menuBar = new JMenuBar();
    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final Collection<CBMBitmapProjectController> controllers = new ArrayList<CBMBitmapProjectController>();

    public CBMBitmapToolFrame(GraphicsConfiguration gc)
    {
        super(gc);

        setTitle("CBM Bitmap Tool");
        setLayout(new BorderLayout());

        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");

        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(new JMenuItem(CBMBitmapToolUtils.get(CBMBitmapNewProjectAction.class)));
        fileMenu.add(new JMenuItem(CBMBitmapToolUtils.get(CBMBitmapCloseProjectAction.class)));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(CBMBitmapToolUtils.get(CBMBitmapOpenSourceImageAction.class)));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(CBMBitmapToolUtils.get(CBMBitmapExitAction.class)));

        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");

        editMenu.setMnemonic(KeyEvent.VK_E);

        editMenu.add(new JMenuItem(CBMBitmapToolUtils.get(CBMBitmapResizeAction.class)));

        menuBar.add(editMenu);

        JMenu viewMenu = new JMenu("View");

        viewMenu.setMnemonic(KeyEvent.VK_V);

        viewMenu.add(new JMenuItem(CBMBitmapToolUtils.get(CBMBitmapZoomInAction.class)));
        viewMenu.add(new JMenuItem(CBMBitmapToolUtils.get(CBMBitmapZoomOutAction.class)));
        viewMenu.add(new JMenuItem(CBMBitmapToolUtils.get(CBMBitmapZoomFitAction.class)));
        viewMenu.add(new JMenuItem(CBMBitmapToolUtils.get(CBMBitmapZoom100Action.class)));

        menuBar.add(viewMenu);

        tabbedPane.setVisible(false);
        
        JPanel panel = CBMBitmapToolUtils.createBorderPanel(tabbedPane);
        
        add(panel, BorderLayout.CENTER);
    }

    public CBMBitmapProjectController createController()
    {
        CBMBitmapProjectModel model = new CBMBitmapProjectModel();
        CBMBitmapProjectController controller = new CBMBitmapProjectController(model);

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
