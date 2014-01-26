package org.cbm.ant.util.bitmap.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

public class CBMBitmapCanvas extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener
{

	private static final long serialVersionUID = 3051383348493852568L;

	private BufferedImage image = null;

	private double zoom = 1;
	private Point mouseOnScreen = null;

	public CBMBitmapCanvas()
	{
		super();

		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public void setImage(BufferedImage image)
	{
		this.image = image;

		updateLayout();
	}

	private void updateLayout()
	{
		BufferedImage image = this.image;
		Dimension dimension = new Dimension();

		if (image != null)
		{
			dimension = new Dimension((int) (image.getWidth() * zoom), (int) (image.getHeight() * zoom));
		}

		dimension.width = Math.max(dimension.width, 320);
		dimension.height = Math.max(dimension.height, 200);

		setMinimumSize(dimension);
		setPreferredSize(dimension);
		setSize(dimension);
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics.create();

		int width = getWidth();
		int height = getHeight();

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);

		BufferedImage image = this.image;

		if (image != null)
		{
			Point2D position = getImageLocationInComponent();

			if (zoom < 1)
			{
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			}

			g.drawImage(image, (int) position.getX(), (int) position.getY(), (int) (image.getWidth() * zoom),
					(int) (image.getHeight() * zoom), null);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		Rectangle2D visibleComponentRectangle = getVisibleComponentRectangle();
		Point newMouseOnScreen = e.getLocationOnScreen();

		visibleComponentRectangle.setRect(visibleComponentRectangle.getX() - (newMouseOnScreen.x - mouseOnScreen.x),
				visibleComponentRectangle.getY() - (newMouseOnScreen.y - mouseOnScreen.y),
				visibleComponentRectangle.getWidth(), visibleComponentRectangle.getHeight());

		scrollRectToVisible(visibleComponentRectangle.getBounds());

		mouseOnScreen = newMouseOnScreen;
		e.consume();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// intentionally left blank
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		mouseOnScreen = e.getLocationOnScreen();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		mouseOnScreen = null;
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// intentionally left blank
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// intentionally left blank
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		int wheelRotation = e.getWheelRotation();

		while (wheelRotation < 0)
		{
			zoomIn(e.getPoint());

			wheelRotation += 1;
		}

		while (wheelRotation > 0)
		{
			zoomOut(e.getPoint());

			wheelRotation -= 1;
		}
	}

	public double getZoom()
	{
		return zoom;
	}

	public void setZoom(double zoom)
	{
		setZoom(zoom, null);
	}

	public void setZoom(double zoom, Point zoomPointComponent)
	{
		if (this.zoom == zoom)
		{
			return;
		}

		if (zoomPointComponent == null)
		{
			zoomPointComponent = getMousePosition();
		}

		Point2D pointImage = null;
		Point2D relativePointComponent = null;

		if (zoomPointComponent != null)
		{
			Rectangle2D visibleRectangleComponent = getVisibleRectangleComponent();

			pointImage = convertFromComponentToImage(zoomPointComponent);
			relativePointComponent = new Point2D.Double(zoomPointComponent.x - visibleRectangleComponent.getX(),
					zoomPointComponent.y - visibleRectangleComponent.getY());
		}

		if (zoom > 16)
		{
			zoom = 16;
		}
		else if (zoom < 0.125)
		{
			zoom = 0.125;
		}

		this.zoom = zoom;

		updateLayout();

		if ((pointImage != null) && (relativePointComponent != null))
		{
			Point2D newPointComponent = convertFromImageToComponent(pointImage);
			Rectangle2D newVisibleRectangle = getVisibleComponentRectangle();

			newVisibleRectangle.setRect(newPointComponent.getX() - relativePointComponent.getX(),
					newPointComponent.getY() - relativePointComponent.getY(), newVisibleRectangle.getWidth(),
					newVisibleRectangle.getHeight());

			scrollRectToVisible(newVisibleRectangle.getBounds());
		}
	}

	public void zoomIn(Point zoomPointComponent)
	{
		zoom(1.25, zoomPointComponent);
	}

	public void zoomOut(Point zoomPointComponent)
	{
		zoom(1 / 1.25, zoomPointComponent);
	}

	public void zoom(double multiplier, Point zoomPointComponent)
	{
		setZoom(zoom * multiplier, zoomPointComponent);
	}

	private Point2D getImageLocationInComponent()
	{
		BufferedImage image = this.image;

		if (image == null)
		{
			return new Point(0, 0);
		}

		int componentWidth = getWidth();
		int componentHeight = getHeight();

		int imageWidth = (int) (image.getWidth() * zoom);
		int imageHeight = (int) (image.getHeight() * zoom);

		return new Point2D.Double((componentWidth - imageWidth) / 2, (componentHeight - imageHeight) / 2);
	}

	private Rectangle2D getVisibleComponentRectangle()
	{
		JViewport viewport = getViewport();

		if (viewport == null)
		{
			return null;
		}

		return viewport.getViewRect();
	}

	private Point2D convertFromComponentToImage(Point2D componentPoint)
	{
		Point2D location = getImageLocationInComponent();

		return new Point2D.Double((componentPoint.getX() - location.getX()) / zoom,
				(componentPoint.getY() - location.getY()) / zoom);
	}

	private Point2D convertFromImageToComponent(Point2D imagePoint)
	{
		Point2D location = getImageLocationInComponent();

		return new Point2D.Double((imagePoint.getX() * zoom) + location.getX(), (imagePoint.getY() * zoom)
				+ location.getY());
	}

	//    private Rectangle2D convertFromComponentToImage(Rectangle2D componentRectangle)
	//    {
	//        Point2D location =
	//            convertFromComponentToImage(new Point2D.Double(componentRectangle.getX(), componentRectangle.getY()));
	//
	//        return new Rectangle2D.Double(location.getX(), location.getY(), componentRectangle.getWidth() / zoom,
	//            componentRectangle.getHeight() / zoom);
	//    }
	//
	//    private Rectangle2D convertFromImageToComponent(Rectangle2D imageRectangle)
	//    {
	//        Point2D location =
	//            convertFromImageToComponent(new Point2D.Double(imageRectangle.getX(), imageRectangle.getY()));
	//
	//        return new Rectangle2D.Double(location.getX(), location.getY(), imageRectangle.getWidth() * zoom,
	//            imageRectangle.getHeight() * zoom);
	//    }

	private Rectangle2D getVisibleRectangleComponent()
	{
		JViewport viewport = getViewport();

		if (viewport == null)
		{
			return null;
		}

		return SwingUtilities.convertRectangle(viewport, viewport.getVisibleRect(), this);
	}

	private JViewport getViewport()
	{
		JScrollPane scrollPane = getScrollPane();

		if (scrollPane == null)
		{
			return null;
		}

		return scrollPane.getViewport();
	}

	public JScrollPane getScrollPane()
	{
		Component parent = this;

		while ((parent = parent.getParent()) != null)
		{
			if (parent instanceof JScrollPane)
			{
				return (JScrollPane) parent;
			}
		}

		return null;
	}

}
