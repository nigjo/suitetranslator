package com.kenai.suitetranslator.bundlenode.io.omegat;

import com.kenai.suitetranslator.bundlenode.io.TranslationExporter;
import com.kenai.suitetranslator.bundlenode.io.TranslationExporterFactory;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Neue Klasse von hof. Erstellt Oct 19, 2011, 12:44:05 PM.
 *
 * @author hof
 */
@ServiceProvider(service = TranslationExporterFactory.class)
public class OmegaTExporterFactory implements TranslationExporterFactory
{
  @Override
  public String getDisplayName()
  {
    return NbBundle.getMessage(OmegaTExporterFactory.class,
        "OmegaTExporterFactory.displayName");
  }

  @Override
  public TranslationExporter createExporter()
  {
    return new OmegaTExporter();
  }

}
