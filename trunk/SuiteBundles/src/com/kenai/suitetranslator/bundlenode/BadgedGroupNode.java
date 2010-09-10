package com.kenai.suitetranslator.bundlenode;

import com.kenai.suitetranslator.bundlenode.data.BundleFile;
import com.kenai.suitetranslator.bundlenode.data.BundleGroup;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.Set;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * FilterNode to add a info badge to BundleGroupNodes.
 *
 * @author hof
 */
class BadgedGroupNode extends FilterNode
{
  private static final String BADGE_BASE =
      "com/kenai/suitetranslator/bundlenode/badges";
  private static final String GRAY = BADGE_BASE + "/gray.png";
  private static final String RED = BADGE_BASE + "/red.png";
  private static final String YELLOW = BADGE_BASE + "/yellow.png";
  private static final String GREEN = BADGE_BASE + "/green.png";
  private String badgeResource;

  public BadgedGroupNode(Node bundleGroupNode)
  {
    super(bundleGroupNode);
    addPropertyChangeListener(new BadgeListener());

    BundleGroup group = getLookup().lookup(BundleGroup.class);
    checkGroup(group);
  }

  @Override
  public Image getIcon(int type)
  {
    Image orginal = super.getIcon(type);
    if(badgeResource == null)
      return orginal;

    Image badge = ImageUtilities.loadImage(badgeResource);

    String tooltip = NbBundle.getMessage(BadgedGroupNode.class, GRAY);
    badge = ImageUtilities.assignToolTipToImage(badge,
        "<em>" + tooltip + "</em>");

    return ImageUtilities.mergeImages(orginal, badge, 15, 8);
  }

  /**
   * definiert den Badge zum Icon.
   *
   * @param badge
   */
  private void setBadge(String badge)
  {
    badgeResource = badge;
    fireIconChange();
  }

  private void checkGroup(BundleGroup group)
  {
    if(group == null)
      return;
    int localeCount = group.getLocaleCount();
    if(localeCount == 1)
      setBadge(GRAY);
    else
    {
      //TODO: add Property entry scans.
      BundleFile defaultFile = group.getFile(null);
      if(defaultFile == null)
      {
        setBadge(RED);
        return;
      }
      Set<String> defaultKeys = defaultFile.getKeys();
      int keyCount = defaultKeys.size();

      for(BundleFile f : group)
      {
        if(f == defaultFile)
          continue;
        Set<String> keys = f.getKeys();
        if(keyCount != keys.size())
        {
          setBadge(YELLOW);
          return;
        }
        for(String key : defaultKeys)
        {
          if(!keys.contains(key))
          {
            setBadge(YELLOW);
            return;
          }
        }
      }

      // everything seems to be ok.
      setBadge(null);
    }
  }

  private class BadgeListener implements PropertyChangeListener
  {
    public BadgeListener()
    {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
      if(!evt.getPropertyName().startsWith("BundleGroupNode"))
        return;
      BundleGroup group = getLookup().lookup(BundleGroup.class);
      checkGroup(group);
    }

  }
}
