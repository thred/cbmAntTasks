package org.cbm.ant.util.bitmap.util;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CBMBitmapSlider extends JComponent
{

	private static final long serialVersionUID = -7870045130854379821L;

	private final JSlider slider = new JSlider(SwingConstants.HORIZONTAL);
	private final JSpinner spinner = new JSpinner();

	private final SpinnerNumberModel spinnerModel;

	private int value;
	private boolean adjusting = false;

	public CBMBitmapSlider(int value, int minimum, int maximum, int stepSize, int largeStepSize)
	{
		super();

		this.value = value;

		setLayout(new BorderLayout(4, 0));

		slider.setMinimum(minimum);
		slider.setMaximum(maximum);
		slider.setValue(value);
		slider.setSnapToTicks(true);
		slider.setMinorTickSpacing(stepSize);
		slider.setMajorTickSpacing(largeStepSize);
		slider.setPaintTicks(true);
		slider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				setValue(slider.getValue());
			}
		});

		spinnerModel = new SpinnerNumberModel(value, minimum, maximum, stepSize);

		spinner.setModel(spinnerModel);
		spinner.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				setValue(((Number) spinner.getValue()).intValue());
			}
		});

		add(slider, BorderLayout.CENTER);
		add(spinner, BorderLayout.EAST);
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		if (adjusting)
		{
			return;
		}

		adjusting = true;

		try
		{
			if (this.value != value)
			{
				this.value = value;

				slider.setValue(value);
				spinner.setValue(value);

				fireChangeEvent();
			}
		}
		finally
		{
			adjusting = false;
		}
	}

	public void addChangeListener(ChangeListener listener)
	{
		listenerList.add(ChangeListener.class, listener);
	}

	public void removeChangeListener(ChangeListener listener)
	{
		listenerList.remove(ChangeListener.class, listener);
	}

	protected void fireChangeEvent()
	{
		ChangeEvent event = new ChangeEvent(this);

		for (ChangeListener listener : listenerList.getListeners(ChangeListener.class))
		{
			listener.stateChanged(event);
		}
	}
}
