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



package sonia.web.access;

//~--- JDK imports ------------------------------------------------------------

import java.io.FileInputStream;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sebastian Sdorra
 */
public class AccessFilter implements Filter
{

  /** Field description */
  private static Logger logger = Logger.getLogger(AccessFilter.class.getName());

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   */
  public void destroy()
  {

    // do nothing
  }

  /**
   * Method description
   *
   *
   * @param request
   * @param response
   * @param chain
   *
   * @throws IOException
   * @throws ServletException
   */
  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain)
          throws IOException, ServletException
  {
    if ((request instanceof HttpServletRequest)
        && (response instanceof HttpServletResponse))
    {
      if (checkAccess((HttpServletRequest) request,
                      (HttpServletResponse) response))
      {
        chain.doFilter(request, response);
      }
    }
  }

  /**
   * Method description
   *
   *
   * @param config
   *
   * @throws ServletException
   */
  public void init(FilterConfig config) throws ServletException
  {
    loadConfig(config.getServletContext());
  }

  /**
   * Method description
   *
   *
   * @param request
   * @param response
   *
   * @return
   */
  protected boolean checkAccess(HttpServletRequest request,
                                HttpServletResponse response)
  {
    return accessHandler.handleAccess(request, response);
  }

  /**
   * Method description
   *
   *
   *
   * @param context
   * @throws ServletException
   */
  private void loadConfig(ServletContext context) throws ServletException
  {
    accessHandler = AccessHandler.getInstance();

    String path = context.getRealPath("/WEB-INF/config/access.xml");
    FileInputStream fis = null;

    try
    {
      fis = new FileInputStream(path);
      accessHandler.readConfig(fis);
    }
    catch (IOException ex)
    {
      accessHandler = null;
      logger.log(Level.SEVERE, null, ex);

      throw new ServletException(ex);
    }
    finally
    {
      if (fis != null)
      {
        try
        {
          fis.close();
        }
        catch (IOException ex)
        {
          logger.log(Level.SEVERE, null, ex);

          throw new ServletException(ex);
        }
      }
    }
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private AccessHandler accessHandler;
}
