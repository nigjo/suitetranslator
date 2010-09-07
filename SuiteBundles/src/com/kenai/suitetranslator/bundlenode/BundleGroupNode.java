package com.kenai.suitetranslator.bundlenode;

import com.kenai.suitetranslator.bundlenode.data.BundleGroup;
import com.kenai.suitetranslator.bundlenode.data.BundleFile;
import java.util.Locale;
import javax.swing.Action;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.openide.actions.OpenAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * Neue Klasse erstellt von hof. Erstellt am Sep 7, 2010, 11:29:33 AM.
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
class BundleGroupNode extends FilterNode
{
  //WZLGT:start
  public static final String WZLGT_PREFIX = "de.rwthaachen.wzl.gt";
  public static final String APP_PREFIX = ".app.";
  public static final String PLATFORM_PREFIX = ".platform.";
  //WZLGT:end
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
    StringBuilder tooltip = new StringBuilder("<html>");
    BundleFile defaultBundleFile = bundleGroup.getFile(null);
    FileObject defaultFile = defaultBundleFile.getFile();
    Project owner = FileOwnerQuery.getOwner(defaultFile);
    if(owner != null)
    {
      String displayName =
          ProjectUtils.getInformation(owner).getDisplayName();
      tooltip.append("<span style='color:gray'>");
      tooltip.append("Projekt");
      tooltip.append(":</span> ");
      tooltip.append(displayName);
      tooltip.append("<br>");
    }
    tooltip.append("<span style='color:gray'>");
    tooltip.append("Bundle");
    tooltip.append(":</span> ");
    tooltip.append(bundleGroup.getBasename());

    if(bundleGroup.getLocaleCount() > 1)
    {
      boolean first = true;
      for(BundleFile file : bundleGroup)
      {
        if(first)
        {
          first = false;
          tooltip.append("<br>");
          tooltip.append("<span style='color:gray'>");
          tooltip.append("Sprachen");
          tooltip.append(":</span> ");
        }
        else
        {
          tooltip.append(", ");
        }
        Locale locale = file.getLocale();
        if(locale == null)
          tooltip.append("&lt;Standard&gt;");
        else
          tooltip.append(locale.getDisplayName());
      }
    }
    return tooltip.toString();
  }

  @Override
  public String getDisplayName()
  {
    if(bundleGroup == null)
      return super.getDisplayName();
    String basename = bundleGroup.getBasename();
    //WZLGT:start
    if(basename.startsWith(WZLGT_PREFIX))
    {
      String prefix = "<wzlgt>";
      int needleLength = WZLGT_PREFIX.length();
      if(basename.substring(WZLGT_PREFIX.length()).startsWith(APP_PREFIX))
      {
        needleLength += APP_PREFIX.length();
        int endIndex = basename.indexOf('.', needleLength);
        String needle = basename.substring(needleLength, endIndex);
        prefix = '<' + needle + '>';
        needleLength += needle.length();
      }
      else if(basename.substring(WZLGT_PREFIX.length()).
          startsWith(PLATFORM_PREFIX))
      {
        needleLength += PLATFORM_PREFIX.length() - 1;
        prefix = "<platform>";
      }
      basename = prefix + basename.substring(needleLength);
    }
    //WZLGT:end
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
