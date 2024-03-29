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



package sonia.blog.api.macro;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.app.BlogContext;
import sonia.blog.api.app.BlogRequest;
import sonia.blog.api.exception.BlogException;
import sonia.blog.entity.ContentObject;
import sonia.blog.entity.Entry;
import sonia.blog.entity.Page;

import sonia.macro.Macro;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sebastian Sdorra
 */
public abstract class AbstractBlogMacro implements Macro
{

  /**
   * Method description
   *
   *
   *
   * @param request
   * @param linkBase - the blog link base
   * @param object - the current ContentObject (Entry)
   * @param body - the body of the macro
   *
   * @return
   */
  protected abstract String doBody(BlogRequest request, String linkBase,
                                   ContentObject object, String body);

  /**
   * Method description
   *
   *
   * @param environment
   * @param body
   *
   * @return
   */
  public String doBody(Map<String, Object> environment, String body)
  {
    this.environment = environment;

    String result = null;
    ContentObject object = (ContentObject) environment.get("object");
    BlogRequest request = (BlogRequest) environment.get("request");
    String linkBase = (String) environment.get("linkBase");

    if ((object != null) && (request != null))
    {
      result = doBody(request, linkBase, object, body);
    }
    else
    {
      result = "-- object or request is null --";
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param parameters
   * @param path
   *
   * @return
   */
  protected String parseTemplate(Map<String, Object> parameters, String path)
  {
    String result = "-- error during template parsing --";
    TemplateParser parser = BlogContext.getInstance().getMacroTemplateParser();

    if (parser == null)
    {
      throw new BlogException("no macro templateParser available");
    }

    try
    {
      result = parser.parseTemplate(parameters, path);
    }
    catch (IOException ex)
    {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
    }

    return result;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param object
   *
   * @return
   */
  protected boolean isEntry(ContentObject object)
  {
    return object instanceof Entry;
  }

  /**
   * Method description
   *
   *
   * @param object
   *
   * @return
   */
  protected boolean isPage(ContentObject object)
  {
    return object instanceof Page;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  protected Map<String, Object> environment;
}
