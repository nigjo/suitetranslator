package com.kenai.suitetranslator.bundlenode.data;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.filesystems.FileObject;

/**
 * Eine einzelne Propierties Datei als Teil eines Bundle.
 *
 * <p><strong>no longer public since 0.9.3!</strong>
 *
 * @author hof
 */
public class BundleFile implements BundleGroupEntry
{
  private final FileObject file;
  private final BundleGroup parent;
  private final Locale locale;
  private SoftReference<Properties> dataRef;
  private Date lastModified;

  BundleFile(BundleGroup bundle, Locale locale) throws IOException
  {
    this.parent = bundle;
    this.locale = locale;

    BundleGroupEntry baseFile = bundle.getFile(null);
    if(baseFile == null)
      throw new IllegalArgumentException("unable to create new bundles");
    FileObject bundleDir = baseFile.getFile().getParent();
    String bundleFileName = bundle.getBasename();
    bundleFileName =
        bundleFileName.substring(bundleFileName.lastIndexOf('.') + 1);
    bundleFileName += '_' + locale.toString();
    FileObject fo = bundleDir.getFileObject(bundleFileName, "properties");
    if(fo == null)
      fo = bundleDir.createData(bundleFileName, "properties");
    this.file = fo;
    this.lastModified = this.file.lastModified();
  }

  BundleFile(BundleGroup bundle, FileObject subfile)
  {
    this.parent = bundle;
    this.file = subfile;
    this.lastModified = this.file.lastModified();

    String base = bundle.getBasename();
    base = base.substring(base.lastIndexOf('.') + 1);

    String filename = subfile.getName();
    String appendix = filename.substring(base.length());
    if(!appendix.isEmpty())
    {
      String[] parts = appendix.split("_");
      switch(parts.length)
      {
        case 2:
          locale = new Locale(parts[1]);
          break;
        case 3:
          locale = new Locale(parts[1], parts[2]);
          break;
        case 4:
          locale = new Locale(parts[1], parts[2], parts[3]);
          break;
        default:
          locale = null;
          break;
      }
    }
    else
      locale = null;
  }

  @Override
  public Locale getLocale()
  {
    return locale;
  }

  @Override
  public BundleGroup getParent()
  {
    return parent;
  }

  public Set<String> getKeys()
  {
    return getData().stringPropertyNames();
  }

  private synchronized Properties getData()
  {
    Properties data = null;
    Date now = file.lastModified();
    if(dataRef == null || dataRef.get() == null || now.after(lastModified))
    {
      data = updateProperties();
      dataRef = new SoftReference<Properties>(data);
      lastModified = now;
    }
    return dataRef.get();
  }

  protected Properties updateProperties()
  {
    try
    {
      file.refresh();
      InputStream in = file.getInputStream();
      try
      {
        Properties data = new Properties();
        data.load(in);
        return data;
      }
      finally
      {
        in.close();
      }
    }
    catch(IOException e)
    {
      Logger.getLogger(BundleFile.class.getName()).
          log(Level.FINE, e.toString(), e);
      return null;
    }
  }

  public String getValue(String key)
  {
    return getData().getProperty(key);
  }

  public void setValue(String key, String value)
  {
    getData().setProperty(key, value);
  }

  @Override
  public FileObject getFile()
  {
    return file;
  }

}
