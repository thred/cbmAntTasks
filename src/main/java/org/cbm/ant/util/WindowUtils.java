package org.cbm.ant.util;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class WindowUtils
{

    private WindowUtils()
    {
        super();
    }

    public static <TYPE extends Frame> TYPE terminateOnClose(final TYPE frame)
    {
        if (frame instanceof JFrame)
        {
            ((JFrame) frame).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }
        else
        {
            frame.addWindowListener(new WindowAdapter()
            {

                @Override
                public void windowClosing(WindowEvent e)
                {
                    frame.setVisible(false);
                    frame.dispose();
                }

            });
        }

        return frame;
    }

    public static GraphicsConfiguration getGraphicsConfiguration(Window window)
    {
        GraphicsConfiguration configuration = window.getGraphicsConfiguration();

        if (configuration != null)
        {
            return configuration;
        }

        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }

    public static GraphicsConfiguration getGraphicsConfiguration(int index)
    {
        return getGraphicsDevice(index).getDefaultConfiguration();
    }

    public static GraphicsDevice getGraphicsDevice(Window window)
    {
        return getGraphicsConfiguration(window).getDevice();
    }

    public static GraphicsDevice getGraphicsDevice(int index)
    {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = environment.getScreenDevices();

        if (index >= 0 && index <= devices.length)
        {
            return devices[index];
        }

        return environment.getDefaultScreenDevice();
    }

    public static int getGraphicsDeviceIndex(GraphicsDevice device)
    {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = environment.getScreenDevices();

        for (int i = 0; i < devices.length; i += 1)
        {
            if (devices[i].equals(device))
            {
                return i;
            }
        }

        return -1;
    }

    public static Rectangle getMaximumWindowBounds(GraphicsDevice device)
    {
        GraphicsConfiguration configuration = device.getDefaultConfiguration();
        Rectangle bounds = (Rectangle) configuration.getBounds().clone();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(device.getDefaultConfiguration());

        bounds.x += screenInsets.left;
        bounds.y += screenInsets.top;
        bounds.width -= screenInsets.left + screenInsets.right;
        bounds.height -= screenInsets.top + screenInsets.bottom;

        return bounds;
    }

    public static boolean enterFullscreen(Window window)
    {
        GraphicsDevice device = getGraphicsDevice(window);

        if (device == null)
        {
            return false;
        }

        device.setFullScreenWindow(window);

        return true;
    }

    public static boolean exitFullscreen(Window window)
    {
        GraphicsDevice device = getGraphicsDevice(window);

        if (device == null)
        {
            return false;
        }

        device.setFullScreenWindow(null);

        return true;
    }

    public static Rectangle getCenterBounds(Window window)
    {
        GraphicsDevice device = getGraphicsDevice(window);
        Rectangle deviceBounds = getMaximumWindowBounds(device);
        Dimension windowSize = window.getSize();

        int width = Math.min(deviceBounds.width, windowSize.width);
        int height = Math.min(deviceBounds.height, windowSize.height);
        int x = (deviceBounds.width - width) / 2;
        int y = (deviceBounds.height - height) / 2;

        return new Rectangle(x, y, width, height);
    }

    public static <TYPE extends Window> TYPE packAndCenter(TYPE window)
    {
        window.pack();

        return center(window);
    }

    public static <TYPE extends Window> TYPE center(TYPE window)
    {
        window.setBounds(getCenterBounds(window));

        return window;
    }

    public static Rectangle getFitBounds(Window window)
    {
        GraphicsDevice device = getGraphicsDevice(window);
        Rectangle deviceBounds = getMaximumWindowBounds(device);
        Rectangle windowBounds = (Rectangle) window.getBounds().clone();

        windowBounds.width = Math.min(windowBounds.width, deviceBounds.width);
        windowBounds.height = Math.min(windowBounds.height, deviceBounds.height);

        if (windowBounds.x < deviceBounds.x)
        {
            windowBounds.x = deviceBounds.x;
        }

        if (windowBounds.y < deviceBounds.y)
        {
            windowBounds.y = deviceBounds.y;
        }

        if (windowBounds.x + windowBounds.width > deviceBounds.x + deviceBounds.width)
        {
            windowBounds.x = deviceBounds.x + deviceBounds.width - windowBounds.width;
        }

        if (windowBounds.y + windowBounds.height > deviceBounds.y + deviceBounds.height)
        {
            windowBounds.y = deviceBounds.y + deviceBounds.height - windowBounds.height;
        }

        return windowBounds;
    }

    public static <TYPE extends Window> TYPE fit(TYPE window)
    {
        window.setBounds(getFitBounds(window));

        return window;
    }

    /**
     * A typical usage would look like this:
     *
     * <pre>
     * ShowcaseFrame frame = WindowUtils
     *     .setAndRecordState(&quot;Showcase&quot;, WindowUtils
     *         .packAndCenter(
     *             new ShowcaseFrame(WindowUtils.getRecordedGraphicsConfiguration(&quot;Showcase&quot;, ShowcaseFrame.class))));
     * </pre>
     *
     * @param name the unique name of the window
     * @param windowType the type of the window
     * @return the graphics configuration
     */
    public static GraphicsConfiguration getRecordedGraphicsConfiguration(String name,
        Class<? extends Window> windowType)
    {
        return getRecordedGraphicsConfiguration(Prefs.get(windowType), name, windowType);
    }

    /**
     * A typical usage would look like this:
     *
     * <pre>
     * ShowcaseFrame frame = WindowUtils
     *     .setAndRecordState(&quot;Showcase&quot;, WindowUtils
     *         .packAndCenter(
     *             new ShowcaseFrame(WindowUtils.getRecordedGraphicsConfiguration(&quot;Showcase&quot;, ShowcaseFrame.class))));
     * </pre>
     *
     * @param prefs some {@link Prefs}
     * @param name the unique name of the window
     * @param windowType the type of the window
     * @return the graphics configuration
     */
    public static GraphicsConfiguration getRecordedGraphicsConfiguration(Prefs prefs, String name,
        Class<? extends Window> windowType)
    {
        String prefix = determineStateRecordPrefix(windowType, name);

        return getGraphicsConfiguration(prefs.get(prefix + "-device", -1));
    }

    /**
     * A typical usage would look like this:
     *
     * <pre>
     * ShowcaseFrame frame = WindowUtils
     *     .setAndRecordState(&quot;Showcase&quot;, WindowUtils
     *         .packAndCenter(
     *             new ShowcaseFrame(WindowUtils.getRecordedGraphicsConfiguration(&quot;Showcase&quot;, ShowcaseFrame.class))));
     * </pre>
     *
     * @param name the unique name of the window
     * @param window the window
     * @return the window itself
     */
    public static <TYPE extends Window> TYPE setAndRecordState(String name, TYPE window)
    {
        return setAndRecordState(Prefs.get(window.getClass()), name, window);
    }

    /**
     * A typical usage would look like this:
     *
     * <pre>
     * ShowcaseFrame frame = WindowUtils
     *     .setAndRecordState(&quot;Showcase&quot;, WindowUtils
     *         .packAndCenter(
     *             new ShowcaseFrame(WindowUtils.getRecordedGraphicsConfiguration(&quot;Showcase&quot;, ShowcaseFrame.class))));
     * </pre>
     *
     * @param prefs some {@link Prefs}
     * @param name the unique name of the window
     * @param window the window
     * @return the window
     */
    public static <TYPE extends Window> TYPE setAndRecordState(Prefs prefs, String name, TYPE window)
    {
        String prefix = determineStateRecordPrefix(window.getClass(), name);
        Rectangle windowBounds = window.getBounds();
        Rectangle bounds =
            new Rectangle(prefs.get(prefix + "-x", windowBounds.x), prefs.get(prefix + "-y", windowBounds.y),
                prefs.get(prefix + "-width", windowBounds.width), prefs.get(prefix + "-height", windowBounds.height));

        int state = prefs.get(prefix + "-state", Frame.NORMAL);

        window.setBounds(bounds);

        if (window instanceof Frame)
        {
            ((Frame) window).setExtendedState(state);
        }

        fit(window);

        WindowStateRecorder worker = new WindowStateRecorder(window, prefs, prefix);

        window.addComponentListener(worker);
        window.addWindowStateListener(worker);

        return window;
    }

    private static String determineStateRecordPrefix(Class<? extends Window> windowType, String name)
    {
        String prefix = windowType.getName();

        if (name != null && !name.isEmpty())
        {
            prefix += "#" + name;
        }
        return prefix;
    }

    private static class WindowStateRecorder implements ComponentListener, WindowStateListener
    {

        private final Window window;
        private final Prefs prefs;
        private final String prefix;

        public WindowStateRecorder(Window window, Prefs prefs, String prefix)
        {
            super();

            this.window = window;
            this.prefs = prefs;
            this.prefix = prefix;
        }

        @Override
        public void componentResized(ComponentEvent e)
        {
            if (e.getSource() == window)
            {
                storeSize();
            }
        }

        @Override
        public void componentMoved(ComponentEvent e)
        {
            if (e.getSource() == window)
            {
                storeLocation();
                storeDevice();
            }
        }

        @Override
        public void componentShown(ComponentEvent e)
        {
            // intentionally left blank
        }

        @Override
        public void componentHidden(ComponentEvent e)
        {
            // intentionally left blank
        }

        @Override
        public void windowStateChanged(WindowEvent e)
        {
            if (e.getSource() == window)
            {
                storeExtendedState();
            }
        }

        protected void storeLocation()
        {
            if (window instanceof Frame && ((Frame) window).getExtendedState() != Frame.NORMAL)
            {
                return;
            }

            Point location = window.getLocation();

            prefs.set(prefix + "-x", location.x);
            prefs.set(prefix + "-y", location.y);
        }

        protected void storeSize()
        {
            if (window instanceof Frame && ((Frame) window).getExtendedState() != Frame.NORMAL)
            {
                return;
            }

            Dimension size = window.getSize();

            prefs.set(prefix + "-width", size.width);
            prefs.set(prefix + "-height", size.height);
        }

        protected void storeExtendedState()
        {
            if (window instanceof Frame)
            {
                int extendedState = ((Frame) window).getExtendedState();

                prefs.set(prefix + "-state", extendedState);
            }
        }

        protected void storeDevice()
        {
            int index = getGraphicsDeviceIndex(getGraphicsDevice(window));

            prefs.set(prefix + "-device", index);
        }
    }
}
