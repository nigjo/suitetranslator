package com.kenai.suitetranslator.l10nfiles;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.openide.explorer.view.Visualizer;
import org.openide.nodes.Node;

/**
 * Eine neue Klasse von hof. Erstellt Nov 29, 2012, 1:55:16 PM.
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
public class ResourceFileRenderer extends JCheckBox
    implements ListCellRenderer
{
  private static final long serialVersionUID = -2071168897495116487L;

  @Override
  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus)
  {
    setFocusPainted(cellHasFocus);
    ResourceFileEntry entry;
    if(value instanceof ResourceFileEntry)
      entry = (ResourceFileEntry)value;
    else
    {
      Node n = Visualizer.findNode(value);
      entry = n.getLookup().lookup(ResourceFileEntry.class);
    }
    if(entry != null)
    {
      setText(entry.getDisplayName());
      setSelected(entry.isActive());
    }
    else
    {
      setText("---");
    }

    if(!list.isEnabled())
      setForeground(UIManager.getColor("textInactiveText"));
    else if(isSelected)
      setBackground(UIManager.getColor("textHighlightText"));
    else
      setForeground(UIManager.getColor("textText"));
    if(isSelected)
      setBackground(UIManager.getColor("textHighlight"));
    else
      setBackground(UIManager.getColor("text"));

    return this;
  }

}
