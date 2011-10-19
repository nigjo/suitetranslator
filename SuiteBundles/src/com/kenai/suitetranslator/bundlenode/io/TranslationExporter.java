package com.kenai.suitetranslator.bundlenode.io;

import java.io.IOException;

import org.netbeans.api.project.Project;

import com.kenai.suitetranslator.bundlenode.data.TranslationBundle;

/**
 *
 * @author hof
 */
public interface TranslationExporter
{
  public void setSuiteProject(Project lookup);

  public void export(TranslationBundle projectInfo);

  public void close() throws IOException;

}
