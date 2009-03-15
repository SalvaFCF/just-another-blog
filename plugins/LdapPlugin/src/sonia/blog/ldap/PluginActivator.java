/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.blog.ldap;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.app.BlogContext;
import sonia.blog.api.app.Constants;

import sonia.config.ConfigurationListener;
import sonia.config.ModifyableConfiguration;

import sonia.plugin.Activator;
import sonia.plugin.PluginContext;
import sonia.plugin.service.ServiceReference;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashMap;

import javax.security.auth.login.AppConfigurationEntry;
import sonia.blog.api.app.BlogConfiguration;

/**
 *
 * @author sdorra
 */
public class PluginActivator implements Activator, ConfigurationListener
{

  /**
   * Method description
   *
   *
   * @param config
   * @param key
   */
  public void configChanged(ModifyableConfiguration config, String key)
  {
    if (key.equalsIgnoreCase(LdapConfigBean.CONFIG_LDAP_ENABLED))
    {
      boolean state = config.getBoolean(LdapConfigBean.CONFIG_LDAP_ENABLED,
                                        Boolean.FALSE);

      if (state &&!active)
      {
        enableLdapModule();
      }
      else if (!state && active)
      {
        disableLdapModule();
      }
    }
  }

  /**
   * Method description
   *
   *
   * @param context
   */
  public void start(PluginContext context)
  {
    if (configReference == null)
    {
      configReference = context.getServiceRegistry().get(String.class,
              Constants.SERVCIE_GLOBALCONFIGPROVIDER);
    }

    if (authReference == null)
    {
      authReference =
        context.getServiceRegistry().get(AppConfigurationEntry.class,
                                         Constants.SERVICE_AUTHENTICATION);
    }

    configReference.add("/view/ldap/config.xhtml");

    BlogConfiguration config = BlogContext.getInstance().getConfiguration();

    config.addListener(this);
    active = config.getBoolean(LdapConfigBean.CONFIG_LDAP_ENABLED,
                               Boolean.FALSE);

    if (active)
    {
      enableLdapModule();
    }
  }

  /**
   * Method description
   *
   *
   * @param context
   */
  public void stop(PluginContext context)
  {
    if (configReference != null)
    {
      configReference.remove("/view/ldap/config.xhtml");
    }

    BlogContext.getInstance().getConfiguration().removeListener(this);
    disableLdapModule();
  }

  /**
   * Method description
   *
   */
  private void disableLdapModule()
  {
    if (entry != null)
    {
      authReference.remove(entry);
    }

    active = false;
  }

  /**
   * Method description
   *
   */
  @SuppressWarnings("unchecked")
  private void enableLdapModule()
  {
    if (entry == null)
    {
      entry = new AppConfigurationEntry(
          LdapLoginModule.class.getName(),
          AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL,
          new HashMap<String, Object>());
    }

    authReference.add(entry);
    active = true;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private boolean active;

  /** Field description */
  private ServiceReference<AppConfigurationEntry> authReference;

  /** Field description */
  private ServiceReference<String> configReference;

  /** Field description */
  private AppConfigurationEntry entry;
}
