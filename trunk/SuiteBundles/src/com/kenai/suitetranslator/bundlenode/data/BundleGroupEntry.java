package com.kenai.suitetranslator.bundlenode.data;

import java.util.Locale;
import java.util.Set;

import org.openide.filesystems.FileObject;

/**
 * Eine neue Schnittstelle von hof. Erstellt Aug 21, 2013, 11:41:51 AM.
 *
 * @todo Hier fehlt die Beschreibung der Schnittstelle.
 *
 * @author hof
 */
public interface BundleGroupEntry
{
  public FileObject getFile();

  public Locale getLocale();

  public BundleGroup getParent();

  public Set<String> getKeys();

}