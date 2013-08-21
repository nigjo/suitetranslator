package com.kenai.suitetranslator.bundlenode.data;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;

import com.kenai.suitetranslator.bundlenode.BundleGroupNode;

/**
 * Daten eines Bundles.
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author nigjo
 */
public class BundleGroup implements
    Iterable<BundleGroupEntry>, Comparable<BundleGroup>
{
  public static final String PROP_BUNDLE_DIR = "BundleGroup.bundleDir";
  public static final String PROP_LOCALE_COUNT = "BundleGroup.localeCount";
  public static final String PROP_LAST_CHANGED = "BundleGroup.lastChanged";
  protected BundleGroupNode node;
  List<BundleGroupEntry> files;
  private final String basename;
  private BundleGroupObserver changeObserver;
  private long lastChanged;

  public BundleGroup(String basename)
  {
    this.basename = basename;
    changeObserver = new BundleGroupObserver(this);
  }

  public String getBasename()
  {
    return basename;
  }

  public void add(FileObject subfile)
  {
    if(files == null)
      files = new ArrayList<BundleGroupEntry>();
    BundleFile bundleFile = new BundleFile(this, subfile);
    files.add(bundleFile);
    changeObserver.bundleLocaleAdded(bundleFile);
  }

  public void remove(FileObject deletedFile)
  {
    if(files == null)
      return;
    remove(getFile(deletedFile));
  }

  void remove(BundleGroupEntry bundleFile)
  {
    if(bundleFile == null)
      return;
    int index = files.indexOf(bundleFile);
    files.remove(index);
    changeObserver.bundleLocaleRemoved(bundleFile);
  }

  @Override
  public Iterator<BundleGroupEntry> iterator()
  {
    return files.iterator();
  }

  @Override
  public int compareTo(BundleGroup o)
  {
    return toString().compareTo(o.toString());
  }

  @Override
  public String toString()
  {
    StringBuilder b = new StringBuilder(basename);
    if(files.size() > 1)
    {
      b.append(" (");
      boolean first = true;
      for(BundleGroupEntry f : this)
      {
        if(first)
          first = false;
        else
          b.append(", ");
        Locale l = f.getLocale();
        if(l == null)
          b.append("<default>");
        else
          b.append(f.getLocale().toString());
      }
      b.append(")");
    }
    return b.toString();
  }

  private BundleGroupEntry getFile(FileObject fo)
  {
    for(BundleGroupEntry file : this)
    {
      if(file.getFile().equals(fo))
        return file;
    }
    return null;
  }

  public BundleGroupEntry getFile(Locale locale)
  {
    for(BundleGroupEntry file : this)
    {
      if(locale == null)
      {
        if(file.getLocale() == null)
          return file;
      }
      else
      {
        if(locale.equals(file.getLocale()))
          return file;
      }
    }
    return null;
  }

  public BundleFile createFile(Locale locale) throws IOException
  {
    BundleFile bundleFile;
    BundleGroupEntry bundleEntry = getFile(locale);
    if(bundleEntry instanceof BundleFile)
    {
      bundleFile = (BundleFile)bundleEntry;
    }
    else
    {
      if(files == null)
        files = new ArrayList<BundleGroupEntry>();
      bundleFile = new BundleFile(this, locale);
      files.add(bundleFile);
    }
    return bundleFile;
  }

  public int getLocaleCount()
  {
    if(files == null)
      return 0;
    return files.size();
  }

  // <editor-fold defaultstate="collapsed" desc="Dummy group/WaitNode">
  public boolean isDummyGroup()
  {
    return this instanceof DummyBundleGroup;
  }

  public static BundleGroup createDummyGroup()
  {
    return new DummyBundleGroup();
  }

  void setLastChange(long time)
  {
    long lastChange = this.lastChanged;
    if(time < lastChange)
      return;
    this.lastChanged = time;
    changeObserver.bundleChanged(lastChange, time);
  }

  public Node getNodeDelegate()
  {
    if(node == null)
      node = new BundleGroupNode(this);
    return node;
  }

  private static class DummyBundleGroup extends BundleGroup
  {
    public DummyBundleGroup()
    {
      super(DummyBundleGroup.class.getName());
    }

    @Override
    public void add(FileObject subfile)
    {
      throw new UnsupportedOperationException("only dummy"); //NOI18N
    }

  }// </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="propertyChangeSupport">
  public synchronized void removePropertyChangeListener(String propertyName,
      PropertyChangeListener listener)
  {
    changeObserver.removePropertyChangeListener(propertyName, listener);
  }

  public synchronized boolean hasListeners(String propertyName)
  {
    return changeObserver.hasListeners(propertyName);
  }

  public synchronized PropertyChangeListener[] getPropertyChangeListeners(
      String propertyName)
  {
    return changeObserver.getPropertyChangeListeners(propertyName);
  }

  public synchronized void addPropertyChangeListener(String propertyName,
      PropertyChangeListener listener)
  {
    changeObserver.addPropertyChangeListener(propertyName, listener);
  }
  // </editor-fold>
}
