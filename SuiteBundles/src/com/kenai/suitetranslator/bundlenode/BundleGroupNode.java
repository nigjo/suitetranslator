package com.kenai.suitetranslator.bundlenode;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;

import javax.swing.Action;

import org.openide.actions.OpenAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;

import com.kenai.suitetranslator.bundlenode.data.BundleFile;
import com.kenai.suitetranslator.bundlenode.data.BundleGroup;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * A Node for a BundleGroup.
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @see BundleGroup
 * @author nigjo
 */
public class BundleGroupNode extends FilterNode
{
  private final BundleGroup bundleGroup;

  public BundleGroupNode(BundleGroup key)
  {
    this(getPropertyData(key), key);
  }

  private BundleGroupNode(DataObject properties, BundleGroup key)
  {
    this(properties.getNodeDelegate(), key);
  }

  private BundleGroupNode(Node original, BundleGroup key)
  {
    super(original, Children.LEAF,
        new ProxyLookup(original.getLookup(), Lookups.fixed(key)));
    this.bundleGroup = key;
    initChangeEvents(this.bundleGroup);
  }

  // <editor-fold defaultstate="collapsed" desc="shortDescription">
  @Override
  public String getShortDescription()
  {
    StringBuilder tooltip = new StringBuilder("<html>"); //NOI18N

    BundleFile defaultBundleFile = bundleGroup.getFile(null);
    if(defaultBundleFile == null)
    {
      tooltip.append("<span style='color:red'>"); //NOI18N
      tooltip.append("no default bundle for <em>");
      tooltip.append(bundleGroup.getBasename());
      tooltip.append("</em>");
      tooltip.append("</span>");
      return tooltip.toString();
    }
    tooltip.append("<span style='color:gray'>"); //NOI18N
    FileObject defaultFile = defaultBundleFile.getFile();
    Project owner = FileOwnerQuery.getOwner(defaultFile);
    if(owner != null)
    {
      String displayName =
          ProjectUtils.getInformation(owner).getDisplayName();
      tooltip.append(makePair("project", displayName));
      tooltip.append("<br>");
    }

    tooltip.append(makePair("bundle", bundleGroup.getBasename()));

    if(bundleGroup.getLocaleCount() > 1)
    {
      StringBuilder locales = null;
      for(BundleFile file : bundleGroup)
      {
        if(locales == null)
        {
          locales = new StringBuilder();
        }
        else
        {
          locales.append(", ");
        }
        Locale locale = file.getLocale();
        if(locale == null)
        {
          locales.append(NbBundle.getMessage(
              BundleGroupNode.class, "BundleGroupNode.default_locale"));
        }
        else
          locales.append(locale.getDisplayName());
      }
      tooltip.append("<br>");
      tooltip.append(makePair("locales", locales.toString()));
    }
    tooltip.append("</span>");
    return tooltip.toString();
  }

  private String makePair(String key, String value)
  {
    String pattern = NbBundle.getMessage(BundleGroupNode.class,
        "BundleGroupNode.tooltip_entry_pattern");
    key = NbBundle.getMessage(BundleGroupNode.class, "BundleGroupNode." + key);
    value = encodeHtml(value);
    return MessageFormat.format(pattern, key, value);
  }

  private static String encodeHtml(String text)
  {
    return encodeHtml(text, true);
  }

  private static String encodeHtml(String text, boolean blackText)
  {
    String htmlText = text;
    htmlText = htmlText.replace("&", "&amp;");
    htmlText = htmlText.replace("<", "&lt;");
    htmlText = htmlText.replace(">", "&gt;");
    htmlText = htmlText.replace("\"", "&quot;");
    htmlText = htmlText.replace("\'", "&#39;");

    if(blackText)
      htmlText = "<span style='color:black'>" + htmlText + "</span>";

    return htmlText;
  }// </editor-fold>

  @Override
  public String getDisplayName()
  {
    if(bundleGroup == null)
      return super.getDisplayName();
    String basename = bundleGroup.getBasename();
    return basename;
  }

  @Override
  public Action getPreferredAction()
  {
    return OpenAction.get(OpenAction.class);
  }

  private static DataObject getPropertyData(BundleGroup key)
  {
    BundleFile defaultBundleFile = key.getFile(null);
    if(defaultBundleFile == null)
      return null;
    FileObject file = defaultBundleFile.getFile();
    try
    {
      return DataObject.find(file);
    }
    catch(DataObjectNotFoundException ex)
    {
      return null;
    }
  }

  private void changedLocaleCount(int oldCount, int newCount)
  {
    setShortDescription(getShortDescription());
    firePropertyChange("BundleGroupNode.localeCount", oldCount, newCount);
  }

  private void changesBundleDir(FileObject oldFolder, FileObject newFolder)
  {
    firePropertyChange("BundleGroupNode.bundleDir", oldFolder, newFolder);
  }

  private void changesBundleData(long oldTime, long newTime)
  {
    firePropertyChange("BundleGroupNode.lastModified", oldTime, newTime);
  }

  // <editor-fold defaultstate="collapsed" desc="PropertyChangeListener">
  private Map<String, PropertyChangeListener> groupListener;

  private void initChangeEvents(BundleGroup group)
  {
    groupListener = new HashMap<String, PropertyChangeListener>();

    groupListener.put(BundleGroup.PROP_LOCALE_COUNT,
        new PropertyChangeListener()
        {
          @Override
          public void propertyChange(PropertyChangeEvent evt)
          {
            changedLocaleCount(
                (Integer)evt.getOldValue(),
                (Integer)evt.getNewValue());
          }

        });

    groupListener.put(BundleGroup.PROP_BUNDLE_DIR,
        new PropertyChangeListener()
        {
          @Override
          public void propertyChange(PropertyChangeEvent evt)
          {
            changesBundleDir(
                (FileObject)evt.getOldValue(),
                (FileObject)evt.getNewValue());
          }

        });
    groupListener.put(BundleGroup.PROP_LAST_CHANGED,
        new PropertyChangeListener()
        {
          @Override
          public void propertyChange(PropertyChangeEvent evt)
          {
            changesBundleData(
                (Long)evt.getOldValue(),
                (Long)evt.getNewValue());
          }

        });

    for(String name : groupListener.keySet())
    {
      PropertyChangeListener listener = groupListener.get(name);
      bundleGroup.addPropertyChangeListener(name, listener);
    }
  }

  @Override
  public void destroy() throws IOException
  {
    // listener loechen
    if(groupListener != null)
    {

      for(String name : groupListener.keySet())
      {
        PropertyChangeListener listener = groupListener.get(name);
        bundleGroup.removePropertyChangeListener(name, listener);
      }
    }

    super.destroy();
  }// </editor-fold>

}
