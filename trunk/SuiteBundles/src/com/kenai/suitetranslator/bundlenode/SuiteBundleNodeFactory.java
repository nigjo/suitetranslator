package com.kenai.suitetranslator.bundlenode;

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;

/**
 * Neue Klasse erstellt von hof. Erstellt am Sep 7, 2010, 10:28:12 AM.
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
public class SuiteBundleNodeFactory implements NodeFactory
{

  @Override
  public NodeList<?> createNodes(Project p)
  {
    NodeList<?> list;
    list = NodeFactorySupport.fixedNodeList(new SuiteBundlesNode(p));
    return list;
  }
}
