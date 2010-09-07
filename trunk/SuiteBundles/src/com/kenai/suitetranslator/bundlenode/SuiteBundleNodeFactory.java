package com.kenai.suitetranslator.bundlenode;

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;

/**
 * NodeFactory for additional Node in Module Suite.
 *
 * @author nigjo
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
