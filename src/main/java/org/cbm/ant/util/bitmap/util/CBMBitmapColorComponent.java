package org.cbm.ant.util.bitmap.util;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import org.cbm.ant.util.bitmap.CBMBitmapFrame;
import org.cbm.ant.util.bitmap.CBMBitmapPaletteUsage;
import org.cbm.ant.util.bitmap.CBMBitmapUtility;

public class CBMBitmapColorComponent extends JComponent implements ActionListener
{

	private static interface PickerCallable
	{

		void picked(Color color);

	}

	private static class Picker implements MouseListener, KeyListener
	{
		private final PickerCallable callable;

		public Picker(PickerCallable callable)
		{
			super();

			this.callable = callable;
		}

		public void consume()
		{
			CBMBitmapFrame frame = CBMBitmapUtility.getFrame();
			Component glassPane = frame.getGlassPane();

			frame.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

			glassPane.addMouseListener(this);
			glassPane.addKeyListener(this);
			glassPane.setVisible(true);
		}

		public void release()
		{
			CBMBitmapFrame frame = CBMBitmapUtility.getFrame();
			Component glassPane = frame.getGlassPane();

			frame.setCursor(null);

			glassPane.removeMouseListener(this);
			glassPane.removeKeyListener(this);
			glassPane.setVisible(false);

		}

		@Override
		public void keyTyped(KeyEvent event)
		{
			event.consume();
		}

		@Override
		public void keyPressed(KeyEvent event)
		{
			event.consume();
		}

		@Override
		public void keyReleased(KeyEvent event)
		{
			event.consume();
		}

		@Override
		public void mouseClicked(MouseEvent event)
		{
			event.consume();

			Point location = event.getLocationOnScreen();

			pick(location);
			release();
		}

		private void pick(Point location)
		{
			Rectangle rectangle = new Rectangle(location.x, location.y, 1, 1);
			try
			{
				BufferedImage capture = new Robot().createScreenCapture(rectangle);
				Color color = new Color(capture.getRGB(0, 0), false);

				callable.picked(color);
			}
			catch (AWTException e)
			{
				e.printStackTrace(System.err);
			}
		}

		@Override
		public void mousePressed(MouseEvent event)
		{
			event.consume();
		}

		@Override
		public void mouseReleased(MouseEvent event)
		{
			event.consume();
		}

		@Override
		public void mouseEntered(MouseEvent event)
		{
			event.consume();
		}

		@Override
		public void mouseExited(MouseEvent event)
		{
			event.consume();
		}

	}

	private static final long serialVersionUID = -5411391019175789364L;

	private final JMenuItem pickHueItem = CBMBitmapUtils.createMenuItem("Pick Hue", this);
	private final JRadioButtonMenuItem doNotUseItem = CBMBitmapUtils
			.createRadioButtonMenuItem("Do Not Use Color", this);
	private final JRadioButtonMenuItem optionalItem = CBMBitmapUtils.createRadioButtonMenuItem("Optional Color", this);
	private final JRadioButtonMenuItem mandatoryItem = CBMBitmapUtils
			.createRadioButtonMenuItem("Mandatory Color", this);

	private String actionCommand;
	private Color color;
	private Color referenceColor;

	public CBMBitmapColorComponent()
	{
		super();

		Dimension dimension = new Dimension(32, 40);

		setSize(dimension);
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setPreferredSize(dimension);

		final JPopupMenu popup = new JPopupMenu();

		popup.add(pickHueItem);
		popup.addSeparator();
		popup.add(doNotUseItem);
		popup.add(optionalItem);
		popup.add(mandatoryItem);

		ButtonGroup group = new ButtonGroup();

		group.add(doNotUseItem);
		group.add(optionalItem);
		group.add(mandatoryItem);

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				maybeShowPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	public String getActionCommand()
	{
		return actionCommand;
	}

	public void setActionCommand(String actionCommand)
	{
		this.actionCommand = actionCommand;
	}

	public void addActionListener(ActionListener listener)
	{
		listenerList.add(ActionListener.class, listener);
	}

	public void removeActionListener(ActionListener listener)
	{
		listenerList.remove(ActionListener.class, listener);
	}

	protected void fireActionEvent()
	{
		ActionEvent event = new ActionEvent(this, 0, getActionCommand());

		for (ActionListener listener : listenerList.getListeners(ActionListener.class))
		{
			listener.actionPerformed(event);
		}
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		if (!CBMBitmapUtils.equals(this.color, color))
		{
			Object old = this.color;

			this.color = color;

			firePropertyChange("color", old, color);
		}
	}

	public Color getReferenceColor()
	{
		return referenceColor;
	}

	public void setReferenceColor(Color referenceColor)
	{
		if (!CBMBitmapUtils.equals(this.referenceColor, referenceColor))
		{
			Object old = this.referenceColor;

			this.referenceColor = referenceColor;

			firePropertyChange("referenceColor", old, referenceColor);
		}
	}

	public CBMBitmapPaletteUsage getUsage()
	{
		if (doNotUseItem.isSelected())
		{
			return CBMBitmapPaletteUsage.DO_NOT_USE;
		}

		if (optionalItem.isSelected())
		{
			return CBMBitmapPaletteUsage.OPTIONAL;
		}

		return CBMBitmapPaletteUsage.MANDATORY;
	}

	public void setUsage(CBMBitmapPaletteUsage usage)
	{
		doNotUseItem.setSelected(usage == CBMBitmapPaletteUsage.DO_NOT_USE);
		optionalItem.setSelected(usage == CBMBitmapPaletteUsage.OPTIONAL);
		mandatoryItem.setSelected(usage == CBMBitmapPaletteUsage.MANDATORY);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();

		if ((source == doNotUseItem) || (source == optionalItem) || (source == mandatoryItem))
		{
			fireActionEvent();

			return;
		}
		else if (source == pickHueItem)
		{
			pickHue();
		}
	}

	public void pickHue()
	{
		Picker picker = new Picker(new PickerCallable()
		{
			@Override
			public void picked(Color color)
			{
				float[] referenceHSB = new float[3];
				float[] colorHSB = new float[3];
				Color reference = getReferenceColor();

				Color.RGBtoHSB(reference.getRed(), reference.getGreen(), reference.getBlue(), referenceHSB);
				Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), colorHSB);

				colorHSB[2] = referenceHSB[2];

				setColor(Color.getHSBColor(colorHSB[0], colorHSB[1], colorHSB[2]));
				fireActionEvent();
			}
		});

		picker.consume();
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		Insets insets = getInsets();
		int x = insets.left;
		int y = insets.top;
		int width = getWidth() - insets.left - insets.right;
		int height = getHeight() - insets.top - insets.bottom;
		int sHeight = height - 8;
		Graphics2D g = (Graphics2D) graphics.create();

		Path2D path = new Path2D.Double();

		path.moveTo(x, y);
		path.lineTo(x + width, y);
		path.lineTo(x, y + sHeight);
		path.closePath();

		g.setColor(color);
		g.fill(path);

		path = new Path2D.Double();

		path.moveTo(x + width, y);
		path.lineTo(x + width, y + sHeight);
		path.lineTo(x, y + sHeight);
		path.closePath();

		g.setColor(referenceColor);
		g.fill(path);

		g.setColor(Color.WHITE);
		g.fillRect(x, y + sHeight, width, height - sHeight);
	}
}
