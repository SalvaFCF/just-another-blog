/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.blog.mapping;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.app.BlogContext;
import sonia.blog.api.app.BlogRequest;
import sonia.blog.api.app.BlogResponse;
import sonia.blog.api.app.Constants;
import sonia.blog.api.dao.EntryDAO;
import sonia.blog.api.link.LinkBuilder;
import sonia.blog.api.mapping.MappingNavigation;
import sonia.blog.api.mapping.ScrollableFilterMapping;
import sonia.blog.entity.Blog;
import sonia.blog.entity.Entry;
import sonia.blog.util.BlogUtil;
import sonia.blog.wui.BlogBean;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.faces.model.ListDataModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sdorra
 */
public class DateMapping extends ScrollableFilterMapping
{

  /**
   * Method description
   *
   *
   * @return
   */
  public MappingNavigation getMappingNavigation()
  {
    return navigation;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param request
   * @param response
   * @param param
   * @param start
   * @param max
   *
   * @return
   *
   * @throws IOException
   * @throws ServletException
   */
  @Override
  protected String handleScrollableFilterMapping(BlogRequest request,
          BlogResponse response, String[] param, int start, int max)
          throws IOException, ServletException
  {
    String result = null;

    if ((param != null) && (param.length > 0))
    {
      try
      {
        String[] dateParts = param[0].split("-");
        Integer year = Integer.parseInt(dateParts[0]);
        Integer month = null;
        Integer day = null;

        if (dateParts.length > 1)
        {
          month = Integer.parseInt(dateParts[1]);
          month--;

          if (dateParts.length > 2)
          {
            day = Integer.parseInt(dateParts[2]);
          }
        }

        Date startDate = BlogUtil.createStartDate(year, month, day);
        Date endDate = BlogUtil.createEndDate(year, month, day);

        if (param.length > 1)
        {
          Long id = Long.parseLong(param[1]);

          result = handleDetailView(request, response, id, startDate, endDate);
        }
        else
        {
          result = handleListView(request, param[0], startDate, endDate, start,
                                  max);
        }
      }
      catch (NumberFormatException ex)
      {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      }
    }
    else
    {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param request
   * @param response
   * @param id
   * @param startDate
   * @param endDate
   *
   * @return
   *
   * @throws IOException
   */
  private String handleDetailView(BlogRequest request, BlogResponse response,
                                  Long id, Date startDate, Date endDate)
          throws IOException
  {
    Blog blog = request.getCurrentBlog();
    EntryDAO entryDAO = BlogContext.getDAOFactory().getEntryDAO();
    Entry entry = entryDAO.get(id);

    if (entry == null)
    {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    else if ((entry.getCreationDate().getTime() < startDate.getTime())
             || (entry.getCreationDate().getTime() > endDate.getTime()))
    {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    if (logger.isLoggable(Level.FINER))
    {
      logger.finer("set entry(" + entry.getId() + ") to BlogBean");
    }

    // TODO: find next and previous
    BlogBean blogBean = BlogUtil.getSessionBean(request, BlogBean.class,
                          BlogBean.NAME);

    blogBean.setEntry(entry);

    return buildTemplateViewId(blog, Constants.TEMPLATE_DETAIL);
  }

  /**
   * Method description
   *
   *
   * @param request
   * @param datePattern
   * @param startDate
   * @param endDate
   * @param start
   * @param end
   *
   * @return
   */
  private String handleListView(BlogRequest request, String datePattern,
                                Date startDate, Date endDate, int start,
                                int end)
  {
    EntryDAO entryDAO = BlogContext.getDAOFactory().getEntryDAO();
    Blog blog = request.getCurrentBlog();
    List<Entry> entries = entryDAO.findAllByBlogAndDate(blog, startDate,
                            endDate, start, end + 1);
    String prevUri = null;
    String nextUri = null;

    if (logger.isLoggable(Level.FINER))
    {
      logger.finer("set entry list(" + entries.size() + ") to BlogBean");
    }

    if (start > 0)
    {
      int page = getCurrentPage(request);

      if (page > 0)
      {
        prevUri = getPageUri(request, page - 1);
      }
    }

    if ((entries != null) && (entries.size() > end - start))
    {
      int page = getCurrentPage(request) + 1;
      int entriesPerPage = blog.getEntriesPerPage();

      if (entries.size() > (page * entriesPerPage))
      {
        nextUri = getPageUri(request, page);
      }

      entries = entries.subList(0, end);
    }

    BlogBean blogBean = BlogUtil.getSessionBean(request, BlogBean.class,
                          BlogBean.NAME);

    blogBean.setPageEntries(new ListDataModel(entries));

    LinkBuilder linkBuilder = BlogContext.getInstance().getLinkBuilder();
    String link = linkBuilder.buildLink(request, "/date/" + datePattern + "/");

    navigation = new SimpleMappingNavigation(prevUri, nextUri,
            link + "{0}.jab");

    return buildTemplateViewId(blog, Constants.TEMPLATE_LIST);
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private MappingNavigation navigation;
}
