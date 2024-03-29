/**
 * Copyright (c) 2009, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of JAB; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://kenai.com/projects/jab
 *
 */



package sonia.blog.scripting;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.app.BlogContext;
import sonia.blog.api.app.Constants;
import sonia.blog.api.app.InstallationListener;
import sonia.blog.api.navigation.NavigationProvider;

import sonia.macro.MacroParser;

import sonia.plugin.Activator;
import sonia.plugin.PluginContext;
import sonia.plugin.service.Service;
import sonia.plugin.service.ServiceReference;

import sonia.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 *
 * @author Sebastian Sdorra
 */
public class PluginActivator implements Activator, InstallationListener
{

  /**
   * Method description
   *
   *
   * @param ctx
   */
  public void afterInstallation(BlogContext ctx)
  {
    init();
  }

  /**
   * Method description
   *
   *
   * @param ctx
   */
  public void beforeInstallation(BlogContext ctx)
  {

    // do nothing
  }

  /**
   * Method description
   *
   *
   * @param context
   */
  public void start(PluginContext context)
  {
    if (BlogContext.getInstance().isInstalled())
    {
      init();
    }
    else
    {
      ServiceReference<InstallationListener> reference =
        BlogContext.getInstance().getServiceRegistry().get(
            InstallationListener.class, Constants.SERVICE_INSTALLATIONLISTENER);

      if (reference != null)
      {
        reference.add(this);
      }
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
    if (BlogContext.getInstance().isInstalled())
    {
      BlogContext ctx = BlogContext.getInstance();

      ctx.getMappingHandler().remove(ScriptingMapping.class);

      ScriptingContext sCtx = ScriptingContext.getInstance();
      List<String> macroNames = sCtx.getScriptNames(MacroScript.class);

      if (Util.isNotEmpty(macroNames))
      {
        MacroParser parser = ctx.getMacroParser();

        for (String m : macroNames)
        {
          parser.removeMacroFactory(m);
        }
      }

      if (navigationProvider != null)
      {
        navigationProviderReference.remove(navigationProvider);
        navigationProvider = null;
      }

      if (freemarkerEngine != null)
      {
        templateEngineReference.remove(freemarkerEngine);
        freemarkerEngine = null;
      }

      context.getServiceRegistry().unregister(ScriptTemplateEngine.class,
              ScriptingContext.SERVICE_TEMPLATEENGINE);
      templateEngineReference = null;
    }
  }

  /**
   * Method description
   *
   */
  private void init()
  {
    templateEngineReference =
      BlogContext.getInstance().getServiceRegistry().register(
        ScriptTemplateEngine.class, ScriptingContext.SERVICE_TEMPLATEENGINE);
    navigationProvider = new ScriptNavigationProvider();
    navigationProviderReference.add(navigationProvider);
    freemarkerEngine = new FreemarkerScriptEngine();
    templateEngineReference.add(freemarkerEngine);

    BlogContext ctx = BlogContext.getInstance();

    ctx.getMappingHandler().add(ScriptingMapping.class);

    ScriptingContext sCtx = ScriptingContext.getInstance();
    List<String> macroNames = sCtx.getScriptNames(MacroScript.class);

    if (Util.isNotEmpty(macroNames))
    {
      MacroParser parser = ctx.getMacroParser();

      for (String m : macroNames)
      {
        parser.putMacroFactory(m, new MacroScriptFactory(m));
      }
    }
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private FreemarkerScriptEngine freemarkerEngine;

  /** Field description */
  private ScriptNavigationProvider navigationProvider;

  /** Field description */
  @Service(Constants.NAVIGATION_GLOBALADMIN)
  private ServiceReference<NavigationProvider> navigationProviderReference;

  /** Field description */
  private ServiceReference<ScriptTemplateEngine> templateEngineReference;
}
