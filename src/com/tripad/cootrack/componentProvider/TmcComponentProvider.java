package com.tripad.cootrack.componentProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.openbravo.client.kernel.BaseComponentProvider;
import org.openbravo.client.kernel.Component;
import org.openbravo.client.kernel.ComponentProvider;
import org.openbravo.client.kernel.KernelConstants;

@ApplicationScoped
@ComponentProvider.Qualifier(TmcComponentProvider.TMC_VIEW_COMPONENT_TYPE)
public class TmcComponentProvider extends BaseComponentProvider {
  public static final String TMC_VIEW_COMPONENT_TYPE = "TmcViewComponentType";

  /*
   * (non-Javadoc)
   *
   * @see org.openbravo.client.kernel.ComponentProvider#getComponent(java.lang.String,
   * java.util.Map)
   */
  @Override
  public Component getComponent(String componentId, Map<String, Object> parameters) {
    throw new IllegalArgumentException("Component id " + componentId + " not supported.");
    /* in this howto we only need to return static resources so there is no need to return anything here */
  }

  @Override
  public List<ComponentResource> getGlobalComponentResources() {
    final List<ComponentResource> globalResources = new ArrayList<ComponentResource>();
		globalResources.add(createStaticResource("web/com.tripad.cootrack/js/TmcToolbarAction.js", false));
    globalResources.add(createStaticResource("web/com.tripad.cootrack/js/TmcManualProcess.js", false));
    globalResources.add(createStaticResource("web/com.tripad.cootrack/js/Component/LoadingPopup.js", false));
		globalResources.add(createStyleSheetResource("web/org.openbravo.userinterface.smartclient/openbravo/skins/" + KernelConstants.SKIN_PARAMETER + "/com.tripad.cootrack/tmc.css", false));

    return globalResources;
  }

  @Override
  public List<String> getTestResources() {
    return Collections.emptyList();
  }
}
