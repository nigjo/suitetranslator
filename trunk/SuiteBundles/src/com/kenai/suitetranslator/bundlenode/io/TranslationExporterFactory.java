package com.kenai.suitetranslator.bundlenode.io;

/**
 *
 * @author hof
 */
public interface TranslationExporterFactory
{
  public String getDisplayName();

  public TranslationExporter createExporter();

}
