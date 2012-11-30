package com.kenai.suitetranslator.l10nfiles;

import org.openide.filesystems.FileObject;

/**
 * Eine neue Klasse von hof. Erstellt Nov 29, 2012, 1:55:42 PM.
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
public class ResourceFileEntry
{
  private final FileObject root;
  private final FileObject key;
  private boolean active;

  public ResourceFileEntry(FileObject root, FileObject key)
  {
    this.root = root;
    this.key = key;
  }

  public String getDisplayName()
  {
    return getSourcePath();
  }

  public String getSourcePath()
  {
    String base = root.getPath();
    String full = key.getPath();
    return full.substring(base.length() + 1);
  }

  boolean isActive()
  {
    return active;
  }

  public void setActive(boolean active)
  {
    this.active = active;
  }

  @Override
  public String toString()
  {
    return (active ? "(x) " : "( ) ") + key.getPath();
  }


}