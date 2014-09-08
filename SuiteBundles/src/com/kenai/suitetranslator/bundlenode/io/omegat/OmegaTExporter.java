package com.kenai.suitetranslator.bundlenode.io.omegat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.api.project.Project;

import org.openide.filesystems.FileObject;
import org.openide.util.EditableProperties;

import com.kenai.suitetranslator.bundlenode.data.BundleGroup;
import com.kenai.suitetranslator.bundlenode.data.BundleGroupEntry;
import com.kenai.suitetranslator.bundlenode.data.TranslationBundle;
import com.kenai.suitetranslator.bundlenode.io.TranslationExporter;

/**
 * Neue Klasse von hof. Erstellt Oct 18, 2011, 1:18:33 PM.
 *
 * @author hof
 */
public class OmegaTExporter implements TranslationExporter
{
  private Project suiteProject;
  private FileObject suiteDir;
  private FileObject sourceBundles;
  private FileObject targetBundles;
  private Map<String, String> translationMemory;

  @Override
  public void setSuiteProject(Project suiteProject)
  {
    this.suiteProject = suiteProject;
    this.suiteDir = suiteProject.getProjectDirectory();

    try
    {
      createDefaultEntries();
    }
    catch(IOException ex)
    {
      Logger.getLogger(OmegaTExporter.class.getName()).log(
          Level.WARNING, ex.toString(), ex);
    }

    translationMemory = new LinkedHashMap<>();
  }

  @Override
  public void export(TranslationBundle projectInfo)
  {
    Locale en = new Locale("en");
    for(BundleGroup group : projectInfo.getGroups())
    {
      try
      {
        EditableProperties source = copyBundle(group, null, sourceBundles);
        if(source == null)
        {
          continue;
        }
        EditableProperties target = copyBundle(group, en, targetBundles);
        if(target == null)
        {
          continue;
        }

        Set<Entry<String, String>> entries = source.entrySet();
        for(Entry<String, String> entry : entries)
        {
          String key = entry.getKey();
          String sourceText = entry.getValue();
          String targetText = target.getProperty(key);
          if(targetText != null)
          {
            if(!translationMemory.containsKey(sourceText))
            {
              translationMemory.put(sourceText, targetText);
            }
          }
        }
      }
      catch(IOException e)
      {
        Logger.getLogger(OmegaTExporter.class.getName()).log(
            Level.WARNING, e.toString(), e);
      }
    }
  }

  @Override
  public void close() throws IOException
  {
    FileObject projectFolder = suiteDir.getFileObject("omegat");

    String version = "1.1";
    String dtdVersion = version.replaceAll("\\.", "");

    FileObject projectFile = projectFolder.getFileObject("project_save", "tmx");
    if(projectFile == null)
    {
      projectFile = projectFolder.createData("project_save", "tmx");
    }

    OutputStreamWriter sow = new OutputStreamWriter(
        projectFile.getOutputStream(), "UTF-8");
    //TODO: richtige Datei ermitteln!
    try(PrintWriter out = new PrintWriter(sow))
    {
      print(out, 0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      print(out, 0, "<!DOCTYPE tmx SYSTEM \"tmx%s.dtd\">", dtdVersion);
      print(out, 0, "<tmx version=\"%s\">", version);
      print(out, 2, "<header");
      print(out, 4, "creationtool=\"%s\"", "SuiteTranslator");
      print(out, 4, "creationtoolversion=\"%s\"", "0.9.2");
      print(out, 4, "segtype=\"%s\"", "paragraph");
      print(out, 4, "o-tmf=\"%s\"", "OmegaT TMX");
      print(out, 4, "adminlang=\"%s\"", "EN-US");
      print(out, 4, "srclang=\"%s\"", "de");
      print(out, 4, "datatype=\"%s\"", "plaintext");
      print(out, 2, ">");
      print(out, 2, "</header>");
      print(out, 2, "<body>");

      for(Map.Entry<String, String> entry : translationMemory.entrySet())
      {
        String key = entry.getKey();
        String value = entry.getValue();
        print(out, 4, "<tu>");
        print(out, 6, "<tuv lang=\"%s\">", "de");
        print(out, 8, "<seg>%s</seg>", toXMLString(key));
        print(out, 6, "</tuv>");
        // lang="en"
        // changedate="20111019T124556Z"
        // changeid="hof"
        print(out, 6, "<tuv lang=\"%s\">", "en");
        print(out, 8, "<seg>%s</seg>", toXMLString(value));
        print(out, 6, "</tuv>");
        print(out, 4, "</tu>");
      }

      print(out, 2, "</body>");
      print(out, 0, "</tmx>");
      //zipOut.closeEntry();
    }
  }

  private void print(PrintWriter out, int indent, String format, Object... args)
  {
    if(indent > 0)
    {
      out.print(String.format("%" + indent + "s", ""));
    }
    out.println(String.format(format, args));
  }

  private void createDefaultEntries() throws IOException
  {
    FileObject projectFolder = suiteDir.getFileObject("omegat");
    if(projectFolder == null)
    {
      projectFolder = suiteDir.createFolder("omegat");
    }
    if(projectFolder != null && !projectFolder.isFolder())
    {
      throw new IOException("no omegat project folder");
    }

    FileObject projectFile = suiteDir.getFileObject("omegat", "project");
    if(projectFile == null)
    {
      projectFile = suiteDir.createData("omegat", "project");
    }

    //ZipEntry entry = new ZipEntry("omegat.project");
    try(PrintWriter out = new PrintWriter(projectFile.getOutputStream()))
    {
      print(out, 0, "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      print(out, 0, "<omegat>");
      print(out, 2, "<project version=\"1.0\">");
      print(out, 2, "<source_dir>%s</source_dir>", "l10n/bundles");
      print(out, 4, "<target_dir>%s</target_dir>", "l10n/bundles_en");
      print(out, 4, "<tm_dir>%s</tm_dir>", "l10n/memory");
      print(out, 4, "<glossary_dir>%s</glossary_dir>", "l10n/glossary");
      print(out, 4, "<dictionary_dir>%s</dictionary_dir>", "l10n/dictionary");
      print(out, 4, "<source_lang>%s</source_lang>", "de");
      print(out, 4, "<target_lang>%s</target_lang>", "en");
      print(out, 4, "<sentence_seg>%s</sentence_seg>", "false");
      print(out, 2, "</project>");
      print(out, 0, "</omegat>");
    }

    FileObject l10n = makeFolder(suiteDir, "l10n");
    sourceBundles = makeFolder(l10n, "bundles", true);
    targetBundles = makeFolder(l10n, "bundles_en", true);
    makeFolder(l10n, "memory");
    makeFolder(l10n, "glossary");
    makeFolder(l10n, "dictionary");
  }

  private FileObject makeFolder(FileObject parent, String folder)
      throws IOException
  {
    return makeFolder(parent, folder, false);
  }

  private FileObject makeFolder(FileObject parent, String folder, boolean clear)
      throws IOException
  {
    FileObject projectFolder = parent.getFileObject(folder);
    if(projectFolder == null)
    {
      projectFolder = parent.createFolder(folder);
    }
    else if(clear && projectFolder.isFolder())
    {
      projectFolder.delete();
      projectFolder = parent.createFolder(folder);
    }
    if(projectFolder != null && !projectFolder.isFolder())
    {
      throw new IOException("not a folder");
    }
    return projectFolder;
  }

  private String toXMLString(String text)
  {
    text = text.replaceAll("&", "&amp;");
    text = text.replaceAll("\"", "&quot;");
    text = text.replaceAll("<", "&lt;");
    text = text.replaceAll(">", "&gt;");
    return text;
  }

  private EditableProperties copyBundle(BundleGroup group, Locale locale,
      FileObject targetFolder) throws IOException
  {
    String basename = group.getBasename();
    String resourceFolderName =
        basename.substring(0, basename.lastIndexOf('.')).replace('.', '-');

    BundleGroupEntry sourceFileData = group.getFile(locale);

    if(sourceFileData == null)
    {
      return null;
    }
    FileObject sourceFile = sourceFileData.getFile();
    if(sourceFile == null)
    {
      return null;
    }

    FileObject resourceFolder = makeFolder(targetFolder, resourceFolderName);
    //sourceFolder.refresh(true);
    FileObject copyTarget = resourceFolder.getFileObject(
        sourceFile.getName(), sourceFile.getExt());
    if(copyTarget != null)
    {
      Logger.getLogger(OmegaTExporter.class.getName()).log(
          Level.WARNING, "duplicate Bundle name ''{0}''", basename);
      return null;
    }
    EditableProperties targetData = new EditableProperties(false);
    // Daten einlesen
    try(InputStream in = sourceFile.getInputStream())
    {
      targetData.load(in);
    }
    // und wieder speichern
    FileObject targetFile = resourceFolder.createData(sourceFile.getNameExt());
    try(OutputStream out = targetFile.getOutputStream())
    {
      targetData.store(out);
    }

    return targetData;
  }

}
