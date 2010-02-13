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



package sonia.blog.tbv;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.app.BlogContext;
import sonia.blog.api.app.BlogRequest;
import sonia.blog.api.link.LinkBuilder;
import sonia.blog.entity.Blog;
import sonia.blog.entity.PermaObject;

/**
 *
 * @author Sebastian Sdorra
 */
public class FakeBlogContext extends BlogContext
{
  public static final String URL = "http://junit.just-another-blog.org";

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public LinkBuilder getLinkBuilder()
  {
    return new FakeLinkBuilder();
  }

  //~--- inner classes --------------------------------------------------------

  /**
   * Class description
   *
   *
   * @version        Enter version here..., 10/02/11
   * @author         Enter your name here...
   */
  private static class FakeLinkBuilder implements LinkBuilder
  {

    /**
     * Method description
     *
     *
     * @param blog
     * @param link
     *
     * @return
     */
    public String buildLink(Blog blog, String link)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Method description
     *
     *
     * @param request
     * @param link
     *
     * @return
     */
    public String buildLink(BlogRequest request, String link)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Method description
     *
     *
     * @param request
     * @param object
     *
     * @return
     */
    public String buildLink(BlogRequest request, PermaObject object)
    {
      return URL;
    }

    /**
     * Method description
     *
     *
     * @param request
     */
    public void init(BlogRequest request)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param request
     * @param object
     *
     * @return
     */
    public String getRelativeLink(BlogRequest request, PermaObject object)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Method description
     *
     *
     * @param request
     * @param resource
     *
     * @return
     */
    public String getRelativeLink(BlogRequest request, String resource)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public boolean isInit()
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}