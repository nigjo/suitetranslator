package com.kenai.suitetranslator.bundlenode.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.util.EditableProperties;

/**
 * Eine einzelne Propierties Datei als Teil eines Bundle;
 *
 * @author hof
 */
public class BundleFile
{
  private final FileObject file;
  private final BundleGroup parent;
  private final Locale locale;
  private EditableProperties data;
  private Date lastModified;
  //private IOException lastException;

  BundleFile(BundleGroup bundle, Locale locale) throws IOException
  {
    this.parent = bundle;
    this.locale = locale;

    BundleFile baseFile = bundle.getFile(null);
    if(baseFile == null)
      throw new IllegalArgumentException("unable to create new bundles");
    FileObject bundleDir = baseFile.file.getParent();
    String bundleFileName = bundle.getBasename();
    bundleFileName =
        bundleFileName.substring(bundleFileName.lastIndexOf('.') + 1);
    bundleFileName += '_' + locale.toString();
    FileObject fo = bundleDir.getFileObject(bundleFileName, "properties");
    if(fo == null)
      fo = bundleDir.createData(bundleFileName, "properties");
    this.file = fo;
    this.lastModified = this.file.lastModified();
    // Die "Datei" existiert nicht und wueder nur Fehlermeldungen
    // "produzieren", deswegen hier die Datenstrukturen anlegen.
    //this.dataObject = DataObject.find(fo);
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

  public Locale getLocale()
  {
    return locale;
  }

  public BundleGroup getParent()
  {
    return parent;
  }

  // <editor-fold defaultstate="collapsed" desc="readfile">
//  private synchronized EditableProperties readFile()
//  {
//    try
//    {
//      return readFileImpl();
//    }
//    catch(IOException ioe)
//    {
//      lastException = ioe;
//      return null;
//    }
//  }
//
//  private EditableProperties readFileImpl() throws IOException
//  {
//    InputStream in = file.getInputStream();
//    try
//    {
//      EditableProperties p = new EditableProperties(true);
//      p.load(in);
//      return p;
//    }
//    finally
//    {
//      in.close();
//    }
//  }
  // </editor-fold>
  public Set<String> getKeys()
  {
    return getData().keySet();
  }

  private synchronized EditableProperties getData()
  {
    Date now = file.lastModified();
    if(data == null || now.after(lastModified))
    {
      updateProperties();
      lastModified = now;
    }
    return data;
  }

  protected void updateProperties()
  {
    try
    {
      InputStream in = file.getInputStream();
      try
      {
        data = new EditableProperties(true);
        data.load(in);
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

  public FileObject getFile()
  {
    return file;
  }

}
