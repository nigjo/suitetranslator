package com.kenai.suitetranslator.bundlenode.data;

import java.util.Collection;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;

/**
 * Neue Klasse von hof. Erstellt Oct 19, 2011, 12:52:13 PM.
 *
 * @author hof
 */
public class TranslationBundle
{
  private Project project;
  private String projectDisplayName;
  private Collection<BundleGroup> groups;

  public String getDisplayName()
  {
    return projectDisplayName;
  }

  public Collection<BundleGroup> getGroups()
  {
    return groups;
  }

  public Project getProject()
  {
    return project;
  }

  public void setProject(Project project)
  {
    this.project = project;
    this.projectDisplayName =
        ProjectUtils.getInformation(project).getDisplayName();
  }

  public void setGroups(Collection<BundleGroup> groups)
  {
    this.groups = groups;
  }

}
