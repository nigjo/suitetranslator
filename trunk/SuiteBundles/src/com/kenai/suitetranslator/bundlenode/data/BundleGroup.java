package com.kenai.suitetranslator.bundlenode.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.openide.filesystems.FileObject;

/**
 * Daten eines Bundles
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
public class BundleGroup implements Iterable<BundleFile>
{
  List<BundleFile> files;
  private final String basename;

  public BundleGroup(String basename)
  {
    this.basename = basename;
  }

  public String getBasename()
  {
    return basename;
  }

  public void add(FileObject subfile)
  {
    if(files == null)
      files = new ArrayList<BundleFile>();
    files.add(new BundleFile(this, subfile));
  }

  @Override
  public Iterator<BundleFile> iterator()
  {
    return files.iterator();
  }

  @Override
  public String toString()
  {
    StringBuilder b = new StringBuilder(basename);
    if(files.size() > 1)
    {
      b.append(" (");
      boolean first = true;
      for(BundleFile f : this)
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

  public BundleFile getFile(Locale locale)
  {
    for(BundleFile file : this)
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
    bundleFile = getFile(locale);
    if(bundleFile == null)
    {
      if(files == null)
        files = new ArrayList<BundleFile>();
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

  public boolean isDummyGroup()
  {
    return this instanceof DummyBundleGroup;
  }

  public static BundleGroup createDummyGroup()
  {
    return new DummyBundleGroup();
  }

  private static class DummyBundleGroup extends BundleGroup
  {
    public DummyBundleGroup()
    {
      super(DummyBundleGroup.class.getName());
    }

  }
}
