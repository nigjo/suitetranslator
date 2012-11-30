package com.kenai.suitetranslator.l10nfiles;

import javax.swing.JComponent;

import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.CompositeCategoryProvider;

import org.openide.util.Lookup;

/**
 * Eine neue Klasse von hof. Erstellt Nov 29, 2012, 9:32:48 AM.
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
@ProjectCustomizer.CompositeCategoryProvider.Registration(projectType =
"org-netbeans-modules-apisupport-project", position = 2500)
public class LocalizedFilesCustomizer implements CompositeCategoryProvider
{
  @Override
  public Category createCategory(Lookup context)
  {
    return Category.create("NigjoL10n", "Localized Files", null);
  }

  @Override
  public JComponent createComponent(Category category, Lookup context)
  {
    LocalizedFilesPanel panel = new LocalizedFilesPanel();
    panel.setContext(category, context);
    return panel;
  }

}