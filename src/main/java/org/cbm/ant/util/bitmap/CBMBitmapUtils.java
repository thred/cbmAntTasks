package org.cbm.ant.util.bitmap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CBMBitmapUtils
{

    private static final Map<Class<?>, Object> REGISTRY = new HashMap<Class<?>, Object>();

    public static boolean equals(final Object obj0, final Object obj1)
    {
        return ((obj0 == null) && (obj1 == null)) || ((obj0 != null) && (obj0.equals(obj1)));
    }

    @SuppressWarnings("unchecked")
    public static <TYPE> TYPE get(Class<TYPE> type)
    {
        Object object = REGISTRY.get(type);

        if (object == null)
        {
            try
            {
                object = type.newInstance();
            }
            catch (InstantiationException e)
            {
                throw new IllegalArgumentException("Failed to create instance of " + type, e);
            }
            catch (IllegalAccessException e)
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
        JLabel label = new JLabel(text);

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

}
