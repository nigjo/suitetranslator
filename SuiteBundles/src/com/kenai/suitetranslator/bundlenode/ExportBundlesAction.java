package com.kenai.suitetranslator.bundlenode;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;

import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

import com.kenai.suitetranslator.bundlenode.data.TranslationBundle;
import com.kenai.suitetranslator.bundlenode.io.TranslationExporter;
import com.kenai.suitetranslator.bundlenode.io.TranslationExporterFactory;

public final class ExportBundlesAction extends AbstractAction
{
  private static final long serialVersionUID = -710022056503015685L;
  private final SuiteBundlesNode node;
  private final TranslationExporterFactory factory;

  public ExportBundlesAction(
      SuiteBundlesNode context, TranslationExporterFactory factory)
  {
    super(factory.getDisplayName());
    this.node = context;
    this.factory = factory;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    RequestProcessor.getDefault().execute(new BundleCollector());
  }

  private class BundleCollector implements Runnable
  {
    private BundleCollector()
    {
    }

    @Override
    public void run()
    {
      long start = System.currentTimeMillis();
      ProgressHandle handle =
          ProgressHandleFactory.createHandle("scanning for bundles");
      handle.start();

      TranslationExporter exporter = factory.createExporter();
      exporter.setSuiteProject(node.getLookup().lookup(Project.class));

      try
      {
        // Dies koennte dauern, je nach Plattengeschwindigkeit und Suitegroesse.
        List<TranslationBundle> bundles = node.getBundles();

        // eigentlichen Export starten.
        handle.switchToDeterminate(bundles.size());
        handle.setDisplayName("exporting bundles");
        int workunit = 0;
        for(TranslationBundle projectInfo : bundles)
        {
          handle.progress(String.format(
              "Project \"%s\"", projectInfo.getDisplayName()));
          Logger.getLogger(ExportBundlesAction.class.getName()).log(
              Level.FINEST, projectInfo.getDisplayName());
          exporter.export(projectInfo);
          //TODO: Debug only
          //sleep();
          handle.progress(++workunit);
        }
      }
      finally
      {
        handle.finish();
        try
        {
          exporter.close();
        }
        catch(IOException e)
        {
          Logger.getLogger(BundleCollector.class.getName()).log(
              Level.WARNING, e.toString(), e);
        }
      }
      long finish = System.currentTimeMillis();
      Logger.getLogger(BundleCollector.class.getName()).log(Level.FINER,
          "duration {0}ms", finish - start);
    }

    private void sleep()
    {
      try
      {
        TimeUnit.MILLISECONDS.sleep(500);
      }
      catch(InterruptedException ex)
      {
        Exceptions.printStackTrace(ex);
      }
    }

  }
}
