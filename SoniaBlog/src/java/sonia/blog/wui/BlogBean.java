/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.blog.wui;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.myfaces.custom.navmenu.NavigationMenuItem;
import org.apache.myfaces.custom.navmenu.UINavigationMenuItem;

import sonia.blog.api.app.BlogContext;
import sonia.blog.api.app.BlogRequest;
import sonia.blog.api.link.LinkBuilder;
import sonia.blog.api.search.SearchContext;
import sonia.blog.api.search.SearchEntry;
import sonia.blog.api.search.SearchException;
import sonia.blog.api.template.Template;
import sonia.blog.api.util.AbstractBean;
import sonia.blog.entity.Blog;
import sonia.blog.entity.Comment;
import sonia.blog.entity.Entry;
import sonia.blog.entity.Role;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author sdorra
 */
public class BlogBean extends AbstractBean
{

  /**
   * Method description
   *
   *
   *
   */
  public void addComment()
  {
    comment.setAuthorAddress(getRequest().getRemoteAddr());
    comment.setEntry(entry);

    EntityManager em = BlogContext.getInstance().getEntityManager();

    em.getTransaction().begin();

    try
    {
      em.persist(comment);
      entry.getComments().add(comment);
      entry = em.merge(entry);

      Comment newComment = new Comment();

      newComment.setAuthorMail(comment.getAuthorMail());
      newComment.setAuthorName(comment.getAuthorName());
      newComment.setAuthorURL(comment.getAuthorURL());
      comment = newComment;
      em.getTransaction().commit();
      entry = em.find(Entry.class, entry.getId());
      messageHandler.info("createCommentSuccess");
    }
    catch (Exception ex)
    {
      if (em.getTransaction().isActive())
      {
        em.getTransaction().rollback();
      }

      logger.log(Level.SEVERE, null, ex);
      messageHandler.error("createCommentFailure");
    }
    finally
    {
      em.close();
    }

    String uri =
      BlogContext.getInstance().getLinkBuilder().buildLink(getRequest(), entry);

    sendRedirect(uri);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public List<NavigationMenuItem> getAdminNavigation()
  {
    List<NavigationMenuItem> navigation = new ArrayList<NavigationMenuItem>();

    if (getRequest().isUserInRole(Role.ADMIN))
    {
      ResourceBundle bundle = getResourceBundle("label");

      navigation.add(new NavigationMenuItem(bundle.getString("templates"),
              "templates"));
      navigation.add(new NavigationMenuItem(bundle.getString("general"),
              "general"));
      navigation.add(new NavigationMenuItem(bundle.getString("members"),
              "members"));
    }

    return navigation;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public List<NavigationMenuItem> getAuthorNavigation()
  {
    List<NavigationMenuItem> navigation = new ArrayList<NavigationMenuItem>();

    if (getRequest().isUserInRole(Role.AUTHOR)
        || getRequest().isUserInRole(Role.ADMIN))
    {
      ResourceBundle bundle = getResourceBundle("label");

      navigation.add(new NavigationMenuItem(bundle.getString("entries"),
              "personal"));
      navigation.add(new NavigationMenuItem(bundle.getString("categories"),
              "categories"));
      navigation.add(new NavigationMenuItem(bundle.getString("comments"),
              "comments"));
    }

    return navigation;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Blog getBlog()
  {
    return getRequest().getCurrentBlog();
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public DataModel getCategories()
  {
    categories = new ListDataModel();

    EntityManager em = BlogContext.getInstance().getEntityManager();
    Query q = em.createNamedQuery("Category.findAllFromBlog");

    q.setParameter("blog", getRequest().getCurrentBlog());

    List list = q.getResultList();

    if (list != null)
    {
      categories.setWrappedData(list);
    }

    em.close();

    return categories;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Comment getComment()
  {
    if (comment == null)
    {
      comment = new Comment();
    }

    return comment;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public DataModel getComments()
  {
    DataModel comments = new ListDataModel();
    EntityManager em = BlogContext.getInstance().getEntityManager();
    Query q = em.createNamedQuery("Comment.entryOverview");

    q.setParameter("entry", entry);

    List commentList = q.getResultList();

    if (commentList != null)
    {
      comments.setWrappedData(commentList);
    }

    em.close();

    return comments;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public DataModel getEntries()
  {
    if (entries == null)
    {
      entries = new ListDataModel();

      EntityManager em = BlogContext.getInstance().getEntityManager();
      List list = null;
      Query q = em.createNamedQuery("Entry.overview");

      q.setParameter("blog", getRequest().getCurrentBlog());
      list = q.getResultList();
      Collections.reverse(list);

      if (list != null)
      {
        entries.setWrappedData(list);
      }

      em.close();
    }

    return entries;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Entry getEntry()
  {
    return entry;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public List<NavigationMenuItem> getExtraNavigation()
  {
    List<NavigationMenuItem> navigation = new ArrayList<NavigationMenuItem>();
    ResourceBundle bundle = getResourceBundle("label");
    BlogRequest request = getRequest();

    if (request.getUser() == null)
    {
      navigation.add(new NavigationMenuItem(bundle.getString("login"),
              "login"));
    }
    else
    {
      if (request.isUserInRole("author") || request.isUserInRole("admin"))
      {
        navigation.add(new NavigationMenuItem(bundle.getString("personal"),
                "personal"));
      }

      navigation.add(new NavigationMenuItem(bundle.getString("logout"),
              "#{LoginBean.logout}"));
    }

    return navigation;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public List<NavigationMenuItem> getMainNavigation()
  {
    List<NavigationMenuItem> navigation = new ArrayList<NavigationMenuItem>();
    BlogRequest request = getRequest();
    LinkBuilder linkBuilder = BlogContext.getInstance().getLinkBuilder();
    ResourceBundle bundle = getResourceBundle("label");
    NavigationMenuItem overview = new NavigationMenuItem();

    overview.setValue(bundle.getString("overview"));
    overview.setExternalLink(linkBuilder.buildLink(request, "/blog/list/"));
    navigation.add(overview);

    return navigation;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Entry getNextEntry()
  {
    Entry e = null;

    if (entry != null)
    {
      if (entries instanceof ListDataModel)
      {
        List data = (List) entries.getWrappedData();
        int pos = data.indexOf(entry);

        if (pos >= 0)
        {
          pos++;

          if (pos < data.size())
          {
            e = (Entry) data.get(pos);
          }
        }
      }
    }

    return e;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getNextUri()
  {
    return nextUri;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public UINavigationMenuItem getOverviewItem()
  {
    if (overviewItem == null)
    {
      LinkBuilder linkBuilder = BlogContext.getInstance().getLinkBuilder();

      overviewItem = new UINavigationMenuItem();
      overviewItem.setValue(getResourceBundle("label").getString("overview"));
      overviewItem.setExternalLink(linkBuilder.buildLink(getRequest(),
              "/blog/list/"));
    }

    return overviewItem;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Entry getPrevEntry()
  {
    Entry e = null;

    if (entry != null)
    {
      if (entries instanceof ListDataModel)
      {
        List data = (List) entries.getWrappedData();
        int pos = data.indexOf(entry);

        if (pos > 0)
        {
          pos--;
          e = (Entry) data.get(pos);
        }
      }
    }

    return e;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getPrevUri()
  {
    return prevUri;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public DataModel getSearchResult()
  {
    searchResult = new ListDataModel();

    SearchContext context = BlogContext.getInstance().getSearchContext();
    Blog blog = getRequest().getCurrentBlog();

    try
    {
      List<SearchEntry> resultList = context.search(blog, searchString);

      if (resultList != null)
      {
        searchResult.setWrappedData(resultList);
      }
    }
    catch (SearchException ex)
    {
      FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
                           ex.getMessage(), null);

      FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    return searchResult;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getSearchString()
  {
    return searchString;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public DataModel getTags()
  {
    tags = new ListDataModel();

    EntityManager em = BlogContext.getInstance().getEntityManager();
    Query q = em.createNamedQuery("Tag.findFromBlog");

    q.setParameter("blog", getRequest().getCurrentBlog());

    try
    {
      List list = q.getResultList();

      tags.setWrappedData(list);
    }
    catch (Exception ex)
    {
      logger.log(Level.SEVERE, null, ex);
    }
    finally
    {
      em.close();
    }

    return tags;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Template getTemplate()
  {
    return BlogContext.getInstance().getTemplateManager().getTemplate(
        getBlog());
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public boolean isInstalled()
  {
    return BlogContext.getInstance().isInstalled();
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param categories
   */
  public void setCategories(DataModel categories)
  {
    this.categories = categories;
  }

  /**
   * Method description
   *
   *
   * @param comment
   */
  public void setComment(Comment comment)
  {
    this.comment = comment;
  }

  /**
   * Method description
   *
   *
   * @param entries
   */
  public void setEntries(DataModel entries)
  {
    this.entries = entries;
  }

  /**
   * Method description
   *
   *
   * @param entry
   */
  public void setEntry(Entry entry)
  {
    this.entry = entry;
  }

  /**
   * Method description
   *
   *
   * @param nextUri
   */
  public void setNextUri(String nextUri)
  {
    this.nextUri = nextUri;
  }

  /**
   * Method description
   *
   *
   * @param overviewItem
   */
  public void setOverviewItem(UINavigationMenuItem overviewItem)
  {
    this.overviewItem = overviewItem;
  }

  /**
   * Method description
   *
   *
   * @param prevUri
   */
  public void setPrevUri(String prevUri)
  {
    this.prevUri = prevUri;
  }

  /**
   * Method description
   *
   *
   * @param searchResult
   */
  public void setSearchResult(DataModel searchResult)
  {
    this.searchResult = searchResult;
  }

  /**
   * Method description
   *
   *
   * @param searchString
   */
  public void setSearchString(String searchString)
  {
    this.searchString = searchString;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private DataModel categories;

  /** Field description */
  private Comment comment;

  /** Field description */
  private DataModel entries;

  /** Field description */
  private Entry entry;

  /** Field description */
  private String nextUri;

  /** Field description */
  private UINavigationMenuItem overviewItem;

  /** Field description */
  private String prevUri;

  /** Field description */
  private DataModel searchResult;

  /** Field description */
  private String searchString;

  /** Field description */
  private DataModel tags;
}
