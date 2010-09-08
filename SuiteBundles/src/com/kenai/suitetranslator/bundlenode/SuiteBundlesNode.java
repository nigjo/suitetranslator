package com.kenai.suitetranslator.bundlenode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.UIManager;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.SubprojectProvider;

import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

import com.kenai.suitetranslator.bundlenode.data.BundleGroup;

/**
 * Hauptnode fuer den Eintrag "Text Resourcen" eines ModuleSuite Projektes.
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author nigjo
 */
class SuiteBundlesNode extends AbstractNode
{
  public static final String DEFAULT_NETBEANS_FOLDER = "org/openide/loaders/defaultFolder.gif";
  public static final String PROPERTY_ICON_BADGE = "com/kenai/suitetranslator/bundlenode/BundlesBadge.png";
  private final Project p;

  public SuiteBundlesNode(Project p)
  {
    super(Children.create(new BundleSearcher(p), true), p.getLookup());
    this.p = p;
    setName(getClass().getName());
    setIconBaseWithExtension(DEFAULT_NETBEANS_FOLDER);
  }

  // <editor-fold defaultstate="collapsed" desc="Filter: Icon">
  @Override
  public Image getIcon(int type)
  {
    Image icon;

    Object nbFolder = UIManager.get("Nb.Explorer.Folder.icon");
    if(nbFolder == null)
      nbFolder = super.getIcon(type);
    if(nbFolder instanceof Image)
      icon = (Image)nbFolder;
    else if(nbFolder instanceof Icon)
      icon = ImageUtilities.icon2Image((Icon)nbFolder);
    else
      icon = super.getIcon(type);
    Image badge = ImageUtilities.loadImage(PROPERTY_ICON_BADGE);
    return ImageUtilities.mergeImages(icon, badge, 9, 8);
  }

  @Override
  public Image getOpenedIcon(int type)
  {
    Image icon;

    Object nbFolder = UIManager.get("Nb.Explorer.Folder.openedIcon");
    if(nbFolder == null)
      nbFolder = super.getOpenedIcon(type);
    if(nbFolder instanceof Image)
      icon = (Image)nbFolder;
    else if(nbFolder instanceof Icon)
      icon = ImageUtilities.icon2Image((Icon)nbFolder);
    else
      icon = super.getOpenedIcon(type);
    Image badge = ImageUtilities.loadImage(PROPERTY_ICON_BADGE);
    return ImageUtilities.mergeImages(icon, badge, 9, 8);
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Filter: displayName">
  @Override
  public String getDisplayName()
  {
    try
    {
      return NbBundle.getMessage(SuiteBundlesNode.class, getName());
    }
    catch(MissingResourceException e)
    {
      return super.getDisplayName();
    }
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Child factory">
  private static class BundleSearcher extends ChildFactory<BundleGroup>
  {
    private final Project p;
    private Iterator<? extends Project> subProjects;

    public BundleSearcher(Project p)
    {
      this.p = p;
    }

    @Override
    protected boolean createKeys(List<BundleGroup> toPopulate)
    {
      Iterator<? extends Project> projects = getSubProjects();
      if(projects == null)
        return true;
      clearWaitNodes(toPopulate);
      while(projects.hasNext())
      {
        if(Thread.interrupted())
          return true;

        Project next = projects.next();
        if(!createProjectKeys(next, toPopulate))
          return true;

        if(Thread.interrupted())
          return true;

        // Die Bundles Projektweise anzeigen
        toPopulate.add(BundleGroup.createDummyGroup());
        return false;
      }
      return true;
    }

    protected Iterator<? extends Project> getSubProjects()
    {
      if(subProjects == null)
      {
        Lookup lookup = p.getLookup();
        SubprojectProvider provider = lookup.lookup(SubprojectProvider.class);
        Set<? extends Project> projects = provider.getSubprojects();
        subProjects = projects.iterator();
      }
      return subProjects;
    }

    @Override
    protected Node createNodeForKey(BundleGroup key)
    {
      if(key.isDummyGroup())
        return createWaitNode();
      return new BundleGroupNode(key);
    }

    private boolean createProjectKeys(
        Project moduleProject, List<BundleGroup> toPopulate)
    {
      Sources sources = ProjectUtils.getSources(moduleProject);
      SourceGroup[] genericSources = sources.getSourceGroups("java");
      if(genericSources == null || genericSources.length == 0)
        return true;
      for(SourceGroup group : genericSources)
      {
        FileObject sourceRoot = group.getRootFolder();
        FileObject[] children = sourceRoot.getChildren();
        for(FileObject child : children)
        {
          if(Thread.interrupted())
            return false;
          if(child.isFolder())
          {
            if(!scanForBundles(child, null, toPopulate))
              return false;
          }
        }
      }
      return true;
    }

    private boolean scanForBundles(FileObject folder, String base,
        List<BundleGroup> toPopulate)
    {
      Map<String, BundleGroup> groups = new HashMap<String, BundleGroup>();
      List<FileObject> subdirs = new ArrayList<FileObject>();
      if(base == null)
        base = folder.getNameExt();
      else
        base += '.' + folder.getNameExt();

      // search available Bundles
      FileObject[] children = folder.getChildren();
      for(FileObject child : children)
      {
        if(Thread.interrupted())
          return false;
        if(child.isFolder())
        {
          subdirs.add(child);
          continue;
        }
        if(!"properties".equals(child.getExt()))
          continue;
        String name = child.getName();
        String filebase = name;
        //filebase = name.substring(name.lastIndexOf('.') + 1);
        if(filebase.indexOf('_') > 0)
          filebase = filebase.substring(0, filebase.indexOf('_'));
        String bundleBase = base + '.' + filebase;
        BundleGroup group = groups.get(bundleBase);
        if(group == null)
        {
          group = new BundleGroup(bundleBase);
          groups.put(bundleBase, group);
        }
        group.add(child);
      }

      // add to pupulate list
      for(BundleGroup bundleGroup : groups.values())
        toPopulate.add(bundleGroup);

      // Unterverzeichnisse durchsuchen
      for(FileObject dir : subdirs)
      {
        if(!scanForBundles(dir, base, toPopulate))
          return false;
      }

      return true;
    }

    private void clearWaitNodes(List<BundleGroup> toPopulate)
    {
      Iterator<BundleGroup> it = toPopulate.iterator();
      while(it.hasNext())
      {
        BundleGroup group = it.next();
        if(group.isDummyGroup())
          it.remove();
      }
    }

  }
  // </editor-fold>
}
