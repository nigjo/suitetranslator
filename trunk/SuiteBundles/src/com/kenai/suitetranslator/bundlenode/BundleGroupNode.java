package com.kenai.suitetranslator.bundlenode;

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

/**
 * Neue Klasse erstellt von hof. Erstellt am Sep 7, 2010, 11:29:33 AM.
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
class BundleGroupNode extends FilterNode
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
  }

  @Override
  public String getShortDescription()
  {
    StringBuilder tooltip = new StringBuilder("<html>"); //NOI18N
    tooltip.append("<span style='color:gray'>"); //NOI18N

    BundleFile defaultBundleFile = bundleGroup.getFile(null);
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
  }

  @Override
  public String getDisplayName()
  {
    if(bundleGroup == null)
      return super.getDisplayName();
    String basename = bundleGroup.getBasename();
//    //WZLGT:start
//    if(basename.startsWith(WZLGT_PREFIX))
//    {
//      String prefix = "<wzlgt>";
//      int needleLength = WZLGT_PREFIX.length();
//      if(basename.substring(WZLGT_PREFIX.length()).startsWith(APP_PREFIX))
//      {
//        needleLength += APP_PREFIX.length();
//        int endIndex = basename.indexOf('.', needleLength);
//        String needle = basename.substring(needleLength, endIndex);
//        prefix = '<' + needle + '>';
//        needleLength += needle.length();
//      }
//      else if(basename.substring(WZLGT_PREFIX.length()).
//          startsWith(PLATFORM_PREFIX))
//      {
//        needleLength += PLATFORM_PREFIX.length() - 1;
//        prefix = "<platform>";
//      }
//      basename = prefix + basename.substring(needleLength);
//    }
//    //WZLGT:end
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

}
