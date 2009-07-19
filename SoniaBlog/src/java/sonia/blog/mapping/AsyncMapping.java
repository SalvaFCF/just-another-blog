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



package sonia.blog.mapping;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.app.BlogContext;
import sonia.blog.api.app.BlogRequest;
import sonia.blog.api.app.BlogResponse;
import sonia.blog.api.app.Constants;
import sonia.blog.api.link.LinkBuilder;
import sonia.blog.api.mapping.FinalMapping;
import sonia.blog.api.search.SearchContext;
import sonia.blog.api.search.SearchEntry;
import sonia.blog.api.search.SearchException;
import sonia.blog.entity.Blog;

import sonia.cache.Cache;

import sonia.rss.AbstractBase;
import sonia.rss.Channel;
import sonia.rss.FeedParser;
import sonia.rss.Item;

import sonia.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.net.URL;

import java.text.DateFormat;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sebastian Sdorra
 */
public class AsyncMapping extends FinalMapping
{

  /** Field description */
  private static Logger logger = Logger.getLogger(AsyncMapping.class.getName());

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param request
   * @param response
   * @param param
   *
   * @throws IOException
   * @throws ServletException
   */
  @Override
  protected void handleFinalMapping(BlogRequest request, BlogResponse response,
                                    String[] param)
          throws IOException, ServletException
  {
    System.out.println();

    if ((param != null) && (param.length > 0))
    {
      String provider = param[0];

      if (Util.hasContent(provider))
      {
        response.setContentType("application/x-javascript");

        if (provider.equals("search"))
        {
          search(request, response);
        }
        else if (provider.equals("feed"))
        {
          feed(request, response);
        }
      }
      else
      {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
    }
    else
    {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  /**
   * Method description
   *
   * @param request
   * @param response
   *
   * @throws IOException
   */
  private void feed(BlogRequest request, BlogResponse response)
          throws IOException
  {
    String urlParam = request.getParameter("url");
    String type = request.getParameter("type");

    if (Util.hasContent(urlParam) && Util.hasContent(type))
    {
      Channel channel = null;
      StringBuffer cacheKey = new StringBuffer();

      cacheKey.append(urlParam).append(":").append(type);

      Cache cache =
        BlogContext.getInstance().getCacheManager().get(Constants.CACHE_FEED);

      if (cache != null)
      {
        channel = (Channel) cache.get(cacheKey.toString());
      }
      else if (logger.isLoggable(Level.WARNING))
      {
        StringBuffer msg = new StringBuffer();

        msg.append("cache ").append(Constants.CACHE_FEED).append(" not found");
        logger.warning(msg.toString());
      }

      if (channel == null)
      {
        FeedParser parser = FeedParser.getInstance(type);

        if (parser != null)
        {
          URL url = new URL(urlParam);
          InputStream in = null;

          try
          {
            in = url.openStream();
            channel = parser.load(in);

            if (channel != null)
            {
              printChannel(response, request.getCurrentBlog(), channel);
              cache.put(cacheKey.toString(), channel);
            }
            else
            {
              if (logger.isLoggable(Level.WARNING))
              {
                logger.warning("could not create channel object");
              }

              response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
          }
          finally
          {
            if (in != null)
            {
              in.close();
            }
          }
        }
        else
        {
          if (logger.isLoggable(Level.WARNING))
          {
            StringBuffer log = new StringBuffer();

            log.append("no parser for type ").append(type).append(" found");
            logger.warning(log.toString());
          }

          response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
      }
      else
      {
        printChannel(response, request.getCurrentBlog(), channel);
      }
    }
    else
    {
      if (logger.isLoggable(Level.WARNING))
      {
        logger.warning("url or feed is empty or null");
      }

      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  /**
   * Method description
   *
   *
   * @param response
   * @param blog
   * @param channel
   *
   * @throws IOException
   */
  private void printChannel(BlogResponse response, Blog blog, Channel channel)
          throws IOException
  {
    PrintWriter writer = null;

    try
    {
      writer = response.getWriter();
      writer.println("[");

      DateFormat df = blog.getDateFormatter();

      writer.print("  {");
      printItem(writer, df, channel);
      writer.println(", \"items\": [");

      List<Item> items = channel.getItems();

      if (Util.hasContent(items))
      {
        Iterator<Item> itemIt = items.iterator();

        while (itemIt.hasNext())
        {
          writer.print("    {");
          printItem(writer, df, itemIt.next());
          writer.println(" }");

          if (itemIt.hasNext())
          {
            writer.print(", ");
          }
        }
      }

      writer.println("]}]");
    }
    finally
    {
      if (writer != null)
      {
        writer.close();
      }
    }
  }

  /**
   * Method description
   *
   *
   * @param writer
   * @param df
   * @param item
   *
   * @throws IOException
   */
  private void printItem(PrintWriter writer, DateFormat df, AbstractBase item)
          throws IOException
  {
    writer.print(" \"title\": \"");
    writer.print((item.getTitle() != null)
                 ? item.getTitle()
                 : "");
    writer.print("\", \"date\": \"");

    if ((df != null) && (item.getPubDate() != null))
    {
      writer.print(df.format(item.getPubDate()));
    }

    writer.print("\", \"link\": \"");
    writer.print((item.getLink() != null)
                 ? item.getLink()
                 : "");
    writer.print("\"");
  }

  /**
   * Method description
   *
   *
   * @param request
   * @param response
   *
   * @throws IOException
   */
  private void search(BlogRequest request, BlogResponse response)
          throws IOException
  {
    PrintWriter writer = response.getWriter();

    writer.println("[");

    String query = request.getParameter("query");

    if (Util.hasContent(query))
    {
      try
      {
        Blog blog = request.getCurrentBlog();
        SearchContext ctx = BlogContext.getInstance().getSearchContext();
        List<SearchEntry> entries = ctx.search(blog, query);

        if (Util.hasContent(entries))
        {
          LinkBuilder linkBuilder = BlogContext.getInstance().getLinkBuilder();
          Iterator<SearchEntry> entryIt = entries.iterator();

          while (entryIt.hasNext())
          {
            SearchEntry e = entryIt.next();

            writer.print("  {");
            writer.print(" value : '");
            writer.print(e.getTitle());
            writer.print("',");
            writer.print(" url : '");
            writer.print(linkBuilder.buildLink(request, e.getData()));
            writer.print("'");

            if (entryIt.hasNext())
            {
              writer.println(" },");
            }
            else
            {
              writer.println(" }");
            }
          }
        }
      }
      catch (SearchException ex)
      {
        logger.log(Level.SEVERE, null, ex);
      }
    }

    writer.println("]");
  }
}
