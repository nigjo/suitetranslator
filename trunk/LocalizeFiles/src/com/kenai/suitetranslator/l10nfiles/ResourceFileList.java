package com.kenai.suitetranslator.l10nfiles;

import javax.swing.JList;


/**
 * Eine neue Klasse von hof. Erstellt Nov 29, 2012, 3:31:27 PM.
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
public class ResourceFileList extends JList
{
  private static final long serialVersionUID = -7580845568026616123L;

  public ResourceFileList()
  {
    super();
    setCellRenderer(new ResourceFileRenderer());
  }

}