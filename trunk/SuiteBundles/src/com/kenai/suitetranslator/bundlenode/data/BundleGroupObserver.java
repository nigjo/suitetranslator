package com.kenai.suitetranslator.bundlenode.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Locale;

import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;

/**
 * Helperclass for BundleGroup to manage property change and file change events.
 *
 * @author nigjo
 */
class BundleGroupObserver extends FileChangeAdapter
{
  private final BundleGroup group;
  private FileObject bundleDir;
  private PropertyChangeSupport changeSupport;

  public BundleGroupObserver(BundleGroup group)
  {
    this.group = group;
    changeSupport = new PropertyChangeSupport(group);
  }

  public void bundleLocaleAdded(BundleFile bundleFile)
  {
    int localeCount = group.getLocaleCount();
    FileObject file = bundleFile.getFile();
    if(bundleDir == null)
    {
      bundleDir = file.getParent();
      bundleDir.addFileChangeListener(this);
      changeSupport.firePropertyChange(BundleGroup.PROP_BUNDLE_DIR,
          null, bundleDir);
    }
    file.addFileChangeListener(this);
    changeSupport.firePropertyChange(BundleGroup.PROP_LOCALE_COUNT,
        localeCount - 1, localeCount);
  }

  public void bundleLocaleRemoved(BundleFile bundleFile)
  {
    int localeCount = group.getLocaleCount();
    FileObject file = bundleFile.getFile();
    file.removeFileChangeListener(this);
    changeSupport.firePropertyChange(BundleGroup.PROP_LOCALE_COUNT,
        localeCount + 1, localeCount);
  }

  private boolean isBundleFile(FileObject file)
  {
    String name = file.getName();
    String basename = group.getBasename();
    String expected = basename.substring(basename.lastIndexOf('.') + 1);
    return name.equals(expected) || name.startsWith(expected + '_');
  }

  void bundleChanged(long lastChange, long time)
  {
    changeSupport.firePropertyChange(
        BundleGroup.PROP_LAST_CHANGED, lastChange, time);
  }

  // <editor-fold defaultstate="collapsed" desc="external file events">
  @Override
  public void fileDataCreated(FileEvent fe)
  {
    // a new bundle?
    FileObject createdFile = fe.getFile();
    if(isBundleFile(createdFile))
    {
      // a new bundle file
      group.add(createdFile);
    }
  }

  @Override
  public void fileDeleted(FileEvent fe)
  {
    FileObject deletedFile = fe.getFile();
    if(isBundleFile(deletedFile))
    {
      group.remove(deletedFile);
    }
  }

  @Override
  public void fileRenamed(FileRenameEvent fe)
  {
    FileObject renamedFile = fe.getFile();
    if(renamedFile.isFolder())
      return;
    BundleFile bundleFile;
    String oldFileName = fe.getName();
    String[] parts = oldFileName.split("_");
    switch(parts.length)
    {
      default:
        // no locale
        bundleFile = group.getFile(null);
        break;
      case 2:
        bundleFile = group.getFile(new Locale(parts[1]));
        break;
      case 3:
        bundleFile = group.getFile(new Locale(parts[1], parts[2]));
        break;
      case 4:
        bundleFile = group.getFile(new Locale(parts[1], parts[2], parts[3]));
        break;
    }
    group.remove(bundleFile);
    group.add(renamedFile);
  }

  @Override
  public void fileChanged(FileEvent fe)
  {
    group.setLastChange(fe.getTime());
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="propertyChangeSupport">
  public synchronized void removePropertyChangeListener(String propertyName,
      PropertyChangeListener listener)
  {
    changeSupport.removePropertyChangeListener(propertyName, listener);
  }

  public synchronized void removePropertyChangeListener(
      PropertyChangeListener listener)
  {
    changeSupport.removePropertyChangeListener(listener);
  }

  public synchronized boolean hasListeners(String propertyName)
  {
    return changeSupport.hasListeners(propertyName);
  }

  public synchronized PropertyChangeListener[] getPropertyChangeListeners(
      String propertyName)
  {
    return changeSupport.getPropertyChangeListeners(propertyName);
  }

  public synchronized PropertyChangeListener[] getPropertyChangeListeners()
  {
    return changeSupport.getPropertyChangeListeners();
  }

  public synchronized void addPropertyChangeListener(String propertyName,
      PropertyChangeListener listener)
  {
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public synchronized void addPropertyChangeListener(
      PropertyChangeListener listener)
  {
    changeSupport.addPropertyChangeListener(listener);
  }// </editor-fold>

}
