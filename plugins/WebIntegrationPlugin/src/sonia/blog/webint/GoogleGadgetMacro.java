/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.blog.webint;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.util.AbstractBlogMacro;
import sonia.blog.entity.ContentObject;

import sonia.util.Util;

//~--- JDK imports ------------------------------------------------------------

import javax.faces.context.FacesContext;

/**
 *
 * @author sdorra
 */
public class GoogleGadgetMacro extends AbstractBlogMacro
{

  /** Field description */
  public static final String NAME = "googleGadget";

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param url
   */
  public void setUrl(String url)
  {
    this.url = url;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param facesContext
   * @param linkBase
   * @param object
   * @param body
   *
   * @return
   */
  @Override
  protected String doBody(FacesContext facesContext, String linkBase,
                          ContentObject object, String body)
  {
    String result = null;

    if (url != null)
    {
      if (!Util.isBlank(url))
      {
        result = "<script src=\"" + url + "\"></script>";
      }
      else
      {
        result = "-- parameter value url is blank --";
      }
    }
    else
    {
      result = "-- parameter url is required --";
    }

    return result;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private String url;
}