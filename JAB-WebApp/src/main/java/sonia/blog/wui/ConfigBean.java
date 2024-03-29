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



package sonia.blog.wui;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.myfaces.custom.navmenu.NavigationMenuItem;

import sonia.blog.api.app.BlogContext;
import sonia.blog.api.app.BlogSession;
import sonia.blog.api.app.Constants;
import sonia.blog.api.authentication.RequireRole;
import sonia.blog.api.dao.BlogDAO;
import sonia.blog.api.dao.Dao;
import sonia.blog.api.search.SearchContext;
import sonia.blog.api.util.AbstractBean;
import sonia.blog.entity.Blog;
import sonia.blog.entity.Role;
import sonia.blog.macro.CodeMacro;
import sonia.blog.util.BlogClearCondition;
import sonia.blog.util.BlogUtil;

import sonia.cache.ObjectCache;

import sonia.plugin.service.Service;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

/**
 *
 * @author Sebastian Sdorra
 */
@RequireRole(Role.ADMIN)
public class ConfigBean extends AbstractBean
{

  /**
   * Constructs ...
   *
   */
  public ConfigBean()
  {
    init();
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public String clearCache()
  {
    ObjectCache cache =
      BlogContext.getInstance().getCacheManager().get(Constants.CACHE_MAPPING);

    if (cache != null)
    {
      cache.clear(new BlogClearCondition(getBlog()));
    }

    getMessageHandler().info(getRequest(), "clearCache");

    return SUCCESS;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String clearImageCache()
  {
    File imageDirectory =
      BlogContext.getInstance().getResourceManager().getDirectory(
          Constants.RESOURCE_IMAGE, blog);
    boolean success = true;

    if (imageDirectory.exists())
    {
      for (File file : imageDirectory.listFiles())
      {
        if (file.exists() && file.isFile())
        {
          if (!file.delete())
          {
            success = false;
          }
        }
      }
    }

    if (success)
    {
      getMessageHandler().info(getRequest(), "successClearImageCache");
    }
    else
    {
      getMessageHandler().info(getRequest(), "failureClearImageCache");
    }

    return SUCCESS;
  }

  /**
   * Method description
   *
   *
   * @param event
   */
  public void imageValueChanged(ValueChangeEvent event)
  {
    clearImageCache();
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String reIndex()
  {
    BlogContext.getInstance().getSearchContext().reIndex(getBlogSession(),
            getBlog());
    getMessageHandler().info(getRequest(), "rebuildIndex");

    return SUCCESS;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String save()
  {
    String result = SUCCESS;

    if (blogDAO.edit(getBlogSession(), blog))
    {
      getMessageHandler().info(getRequest(), "updateConfigSuccess");
    }
    else
    {
      getMessageHandler().error(getRequest(), "updateConfigFailure");
      result = FAILURE;
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String saveTheme()
  {
    BlogSession session = getBlogSession();

    blogDAO.setParameter(session, blog, CodeMacro.CONFIG_THEME, codeTheme);
    getMessageHandler().info(getRequest(), "updateConfigSuccess");

    return SUCCESS;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public List<NavigationMenuItem> getActions()
  {
    List<NavigationMenuItem> actions = new ArrayList<NavigationMenuItem>();
    ResourceBundle label = getResourceBundle("label");
    SearchContext context = BlogContext.getInstance().getSearchContext();

    if (context != null)
    {
      actions.add(new NavigationMenuItem(label.getString("reIndexSearch"),
                                         "#{ConfigBean.reIndex}"));
    }

    actions.add(new NavigationMenuItem(label.getString("clearImageCache"),
                                       "#{ConfigBean.clearImageCache}"));
    actions.add(new NavigationMenuItem(label.getString("clearCache"),
                                       "#{ConfigBean.clearCache}"));

    return actions;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Blog getBlog()
  {
    blog = getRequest().getCurrentBlog();

    return blog;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getCodeTheme()
  {
    if (codeTheme == null)
    {
      Blog b = getBlog();

      codeTheme = blogDAO.getParameter(b, CodeMacro.CONFIG_THEME);
    }

    return codeTheme;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public SelectItem[] getLocaleItems()
  {
    return BlogUtil.getLocaleItems(FacesContext.getCurrentInstance());
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public List<String> getProviders()
  {
    return providers;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public SelectItem[] getTimeZoneItems()
  {
    return BlogUtil.getTimeZoneItems();
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param codeTheme
   */
  public void setCodeTheme(String codeTheme)
  {
    this.codeTheme = codeTheme;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private Blog blog;

  /** Field description */
  @Dao
  private BlogDAO blogDAO;

  /** Field description */
  private String codeTheme;

  /** Field description */
  @Service(Constants.SERVICE_BLOGCONFIGPROVIDER)
  private List<String> providers;
}
