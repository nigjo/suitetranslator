package com.kenai.suitetranslator.l10nfiles;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;

import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * Eine neue Klasse von hof. Erstellt Nov 29, 2012, 1:39:36 PM.
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
public class ResourceManagementFile
{
  private final Project project;

  public ResourceManagementFile(Project project)
  {
    this.project = project;
  }

  public TableModel createTableModel()
  {
    return new ResourceFilesTableModel();
  }

  public void store() throws IOException
  {
    if(entries != null)
    {
      FileObject iniFile = getIniFile(project);
      OutputStream os = iniFile.getOutputStream();
      PrintWriter out = new PrintWriter(
          new OutputStreamWriter(os, "UTF-8"));
      try
      {
        for(ResourceFileEntry entry : entries)
        {
          if(entry.isActive())
          {
            out.println(entry.getSourcePath());
          }
        }
      }
      finally
      {
        out.close();
      }
    }
  }

  public static FileObject getIniFile(Project project)
  {
    FileObject dir = project.getProjectDirectory();
    FileObject cfgFile = dir.getFileObject("nbproject");
    if(cfgFile != null)
    {
      try
      {
        FileObject iniFile = cfgFile.getFileObject("l10n_files", "list");
        if(iniFile == null)
          iniFile = cfgFile.createData("l10n_files", "list");
        return iniFile;
      }
      catch(IOException ex)
      {
        Exceptions.printStackTrace(ex);
      }
    }
    return null;
  }

  public static FileObject findIniFile(Project project)
  {
    FileObject dir = project.getProjectDirectory();
    FileObject cfgFile = dir.getFileObject("nbproject");
    if(cfgFile != null)
      return cfgFile.getFileObject("l10n_files", "list");
    else
      return null;
  }

  private List<ResourceFileEntry> entries;

  public List<ResourceFileEntry> getEntries()
  {
    if(this.entries == null)
    {
      ResourcesChildren collector = new ResourcesChildren(project);
      List<ResourceFileEntry> children = new ArrayList<ResourceFileEntry>();
      collector.createKeys(children);
      FileObject activeFilesList = findIniFile(project);
      if(activeFilesList != null)
      {
        try
        {
          List<String> fileList = activeFilesList.asLines();
          for(String fileName : fileList)
          {
            for(ResourceFileEntry child : children)
            {
              if(fileName.equals(child.getSourcePath()))
              {
                child.setActive(true);
                break;
              }
            }
          }
        }
        catch(IOException ex)
        {
          Exceptions.printStackTrace(ex);
        }
      }

      entries = Collections.unmodifiableList(children);
    }
    return entries;
  }

  void clearList()
  {
    FileObject filesList = findIniFile(project);
    if(filesList != null)
    {
      try
      {
        OutputStream outputStream = filesList.getOutputStream();
        // direkt wieder schliessen. Keine Daten.
        outputStream.close();
      }
      catch(IOException ex)
      {
        Exceptions.printStackTrace(ex);
      }
    }
  }

  private static class ResourcesChildren
  {
    private final Project project;

    private ResourcesChildren(Project project)
    {
      this.project = project;
    }

    public void createKeys(List<ResourceFileEntry> toPopulate)
    {
      Sources sources = ProjectUtils.getSources(project);
      if(sources == null)
        return;
      for(SourceGroup s : sources.getSourceGroups("java"))
      {
        FileObject srcRoot = s.getRootFolder();
        Enumeration<? extends FileObject> all = srcRoot.getChildren(true);
        iterateFiles:
        while(all.hasMoreElements())
        {
          FileObject next = all.nextElement();
          if(next.isFolder())
            continue;

          //TODO: VCS.ignores pruefen. Wie mach NB selbst das?
          if(next.getPath().contains("/.svn/"))
            continue;
          if("java".equalsIgnoreCase(next.getExt()))
            continue;
          if("form".equalsIgnoreCase(next.getExt()))
            continue;

          toPopulate.add(new ResourceFileEntry(srcRoot, next));
        }
      }
    }

  }

  class ResourceFilesTableModel implements TableModel
  {
    private List<String> header = Arrays.asList(
        "ResourceFilesTableModel.active",
        "ResourceFilesTableModel.name");
    @SuppressWarnings("unchecked")
    private List<Class<?>> columnType =
        Arrays.asList((Class<?>)Boolean.class, String.class);
    private List<ResourceFileEntry> entries;

    public ResourceManagementFile getManager()
    {
      return ResourceManagementFile.this;
    }

    @Override
    public int getRowCount()
    {
      if(entries == null)
        entries = getEntries();
      return entries.size();
    }

    @Override
    public int getColumnCount()
    {
      return header.size();
    }

    @Override
    public String getColumnName(int columnIndex)
    {
      return null;// NbBundle.getMessage(
      //ResourceFilesTableModel.class, header.get(columnIndex));
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
      return columnType.get(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
      return columnIndex == 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
      if(entries == null)
        entries = getEntries();
      ResourceFileEntry row = entries.get(rowIndex);
      switch(columnIndex)
      {
        case 0:
          return row.isActive();
        case 1:
          return row.getDisplayName();
      }
      return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
      ResourceFileEntry row = entries.get(rowIndex);
      switch(columnIndex)
      {
        case 0:
          row.setActive((Boolean)aValue);
      }
      fireTableChanged(new TableModelEvent(this, rowIndex));
    }

    //<editor-fold defaultstate="collapsed" desc="TableModelChangeSupport">
    List<TableModelListener> listeners;

    @Override
    public void addTableModelListener(TableModelListener l)
    {
      if(listeners == null)
        listeners = new ArrayList<TableModelListener>();
      listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l)
    {
      if(listeners != null)
      {
        listeners.remove(l);
      }
    }

    private void fireTableChanged(TableModelEvent tableModelEvent)
    {
      if(listeners != null)
      {
        for(TableModelListener listener : listeners)
        {
          listener.tableChanged(tableModelEvent);
        }
      }
    }
    //</editor-fold>

  }
}