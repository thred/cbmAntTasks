package org.cbm.ant.util.bitmap.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.ChangeListener;

import org.cbm.ant.util.bitmap.CBMBitmapUtility;

public class CBMBitmapUtils
{

    private static final Map<Class<?>, Object> REGISTRY = new HashMap<>();

    public static boolean equals(final Object obj0, final Object obj1)
    {
        return obj0 == null && obj1 == null || obj0 != null && obj0.equals(obj1);
    }

    @SuppressWarnings("unchecked")
    public static <TYPE> TYPE get(Class<TYPE> type)
    {
        Object object = REGISTRY.get(type);

        if (object == null)
        {
            try
            {
                object = type.getConstructor().newInstance();
            }
            catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e)
            {
                throw new IllegalArgumentException("Failed to create instance of " + type, e);
            }

            REGISTRY.put(type, object);
        }

        return (TYPE) object;
    }

    public static JPanel createBorderPanel(Component component)
    {
        JPanel panel = new JPanel(new BorderLayout());

        panel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        panel.add(component, BorderLayout.CENTER);

        return panel;
    }

    public static JPanel createLabeledPanel(String label, Component component)
    {
        JPanel panel = new JPanel(new BorderLayout(8, 4));

        panel.setOpaque(false);
        panel.add(createLabel(label, component), BorderLayout.WEST);
        panel.add(component);

        return panel;
    }

    public static JPanel createButtonPanel(JButton... buttons)
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));

        for (JButton button : buttons)
        {
            panel.add(button);
        }

        return panel;
    }

    public static JLabel createLabel(String text, Component labelFor)
    {
        return createLabel(null, text, labelFor);
    }

    public static JLabel createIcon(String iconResourceName, Component labelFor)
    {
        return createLabel(iconResourceName, null, labelFor);
    }

    public static JLabel createLabel(String iconResourceName, String text, Component labelFor)
    {
        JLabel label = new JLabel();

        if (iconResourceName != null)
        {
            label.setIcon(new ImageIcon(CBMBitmapUtility.class.getResource(iconResourceName)));
        }

        if (text != null)
        {
            label.setText(text);
        }

        if (labelFor != null)
        {
            label.setLabelFor(labelFor);
        }

        return label;
    }

    public static JButton createButton(String text, String actionCommand, ActionListener listener)
    {
        JButton button = new JButton(text);

        button.setActionCommand(actionCommand);
        button.addActionListener(listener);

        return button;
    }

    public static JButton createButton(String text, ActionListener listener)
    {
        return createButton(text, null, listener);
    }

    public static JCheckBox createCheckBox(String text, boolean selected)
    {
        JCheckBox checkBox = new JCheckBox(text);

        checkBox.setOpaque(false);
        checkBox.setSelected(selected);

        return checkBox;
    }

    public static CBMBitmapSlider createSlider(int value, int minimum, int maximum, int stepSize, int largeStepSize,
        ChangeListener listener)
    {
        CBMBitmapSlider slider = new CBMBitmapSlider(value, minimum, maximum, stepSize, largeStepSize);

        if (listener != null)
        {
            slider.addChangeListener(listener);
        }

        return slider;
    }

    public static JMenuItem createMenuItem(String text, ActionListener listener)
    {
        JMenuItem item = new JMenuItem(text);

        item.addActionListener(listener);

        return item;
    }

    public static JCheckBoxMenuItem createCheckBoxMenuItem(String text, ActionListener listener)
    {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(text);

        item.addActionListener(listener);

        return item;
    }

    public static JRadioButtonMenuItem createRadioButtonMenuItem(String text, ActionListener listener)
    {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(text);

        item.addActionListener(listener);

        return item;
    }

}
