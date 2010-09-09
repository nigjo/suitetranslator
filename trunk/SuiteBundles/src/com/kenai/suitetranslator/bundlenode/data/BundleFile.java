package com.kenai.suitetranslator.bundlenode.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;
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
    this.file = bundleDir.getFileObject(bundleFileName, "properties");

    // Die "Datei" existiert nicht und wueder nur Fehlermeldungen
    // "produzieren", deswegen hier die Datenstrukturen anlegen.
//    this.entries = new HashMap<String, BundleEntry>();
//    this.keyOrder = new ArrayList<String>();
    this.data = new EditableProperties(true);
  }

  BundleFile(BundleGroup bundle, FileObject subfile)
  {
    this.parent = bundle;
    this.file = subfile;

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

  // <editor-fold defaultstate="collapsed" desc="readfile">
  private synchronized void readFile()
  {
    try
    {
      readFileImpl();
    }
    catch(IOException ioe)
    {
    }
  }

  private void readFileImpl() throws IOException
  {
    InputStream in = file.getInputStream();
    try
    {
      EditableProperties p = new EditableProperties(true);
      p.load(in);
    }
    finally
    {
      in.close();
    }
  }
  // </editor-fold>

  public Set<String> getKeys()
  {
    if(data == null)
      readFile();
    return data.keySet();
  }

  public String getValue(String key)
  {
    if(data == null)
      readFile();
    return data.getProperty(key);
  }

  public void setValue(String key, String value)
  {
    data.setProperty(key, value);
  }

  public static String stripPropertyExtras(String lines)
  {
    if(lines == null)
      return null;
    String singleLine = lines.trim();
    singleLine = singleLine.replaceAll("\\\\\n\\s*", "");
    int index;
    while(-1 < (index = singleLine.indexOf('\\')))
    {
      if(singleLine.length() <= index + 1)
        break;
      switch(singleLine.charAt(index + 1))
      {
        case 'u':
          String code = singleLine.substring(index + 2, index + 6);
          char c = (char)Integer.parseInt(code, 16);
          singleLine = singleLine.substring(0, index)
              + c + singleLine.substring(index + 6);
          index++;
          break;
        case 't':
          singleLine = singleLine.substring(0, index)
              + '\t' + singleLine.substring(index + 2);
          break;
        case 'r':
          singleLine = singleLine.substring(0, index)
              + '\r' + singleLine.substring(index + 2);
          break;
        case 'n':
          singleLine = singleLine.substring(0, index)
              + '\n' + singleLine.substring(index + 2);
          break;
        default:
          // delete escape
          singleLine = singleLine.substring(0, index)
              + singleLine.substring(index + 1);
          break;
      }
    }
    return singleLine;
  }

  public FileObject getFile()
  {
    return file;
  }

}
