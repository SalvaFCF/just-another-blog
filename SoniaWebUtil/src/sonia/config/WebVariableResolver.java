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



package sonia.config;

//~--- JDK imports ------------------------------------------------------------

import javax.servlet.ServletContext;

/**
 *
 * @author Sebastian Sdorra
 */
public class WebVariableResolver extends DefaultVariableResolver
{

  /** Field description */
  public static final String PROVIDER_WEB = "web";

  /** Field description */
  public static final String VARIABLE_CONTEXTPATH = "contextpath";

  /** Field description */
  public static final String VARIABLE_REALPATH = "realpath";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param servletContext
   */
  public WebVariableResolver(ServletContext servletContext)
  {
    this.servletContext = servletContext;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param configuration
   * @param provider
   * @param variable
   *
   * @return
   */
  @Override
  public String resolveVariable(Configuration configuration, String provider,
                                String variable)
  {
    String result = "";

    if (PROVIDER_WEB.equals(provider))
    {
      if (VARIABLE_REALPATH.equals(variable))
      {
        result = servletContext.getRealPath("/");
      }
      else if (VARIABLE_CONTEXTPATH.equals(variable))
      {
        result = servletContext.getContextPath();
      }
      else
      {
        result = servletContext.getInitParameter(variable);
      }
    }
    else
    {
      result = super.resolveVariable(configuration, provider, variable);
    }

    return result;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private ServletContext servletContext;
}
