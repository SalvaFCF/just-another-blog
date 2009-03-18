/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.blog.mapping.remote;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.common.XmlRpcNotAuthorizedException;

import sonia.blog.api.app.BlogContext;
import sonia.blog.api.dao.BlogDAO;
import sonia.blog.api.dao.CategoryDAO;
import sonia.blog.api.dao.EntryDAO;
import sonia.blog.api.dao.UserDAO;
import sonia.blog.entity.Blog;
import sonia.blog.entity.BlogMember;
import sonia.blog.entity.Category;
import sonia.blog.entity.Entry;
import sonia.blog.entity.Role;
import sonia.blog.entity.User;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 *
 * @author sdorra
 */
public class Blogger
{

  /** Field description */
  public static final String BLOGGER_KEY = "blogger";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  public Blogger()
  {
    logger = Logger.getLogger(getClass().getName());
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param appKey
   * @param postId
   * @param username
   * @param password
   * @param publish
   *
   * @return
   *
   *
   * @throws Exception
   */
  public Boolean deletePost(String appKey, String postId, String username,
                            String password, Boolean publish)
          throws Exception
  {
    logger.info("Blogger.deletePost");

    Boolean result = Boolean.FALSE;
    LoginContext ctx = login(username, password);
    EntryDAO entryDAO = BlogContext.getDAOFactory().getEntryDAO();
    Entry entry = entryDAO.get(convertId(postId));

    if (entry != null)
    {
      User user = getUser(ctx);
      boolean prev = false;

      if (entry.getAuthor().equals(user) || user.isGlobalAdmin())
      {
        prev = true;
      }
      else
      {
        Blog blog = entry.getBlog();

        prev = isAdmin(user, blog);
      }

      if (prev)
      {
        result = entryDAO.remove(entry);
      }
      else
      {
        throw new XmlRpcException("user has no access to the post");
      }
    }
    else
    {
      throw new XmlRpcException("post not found");
    }

    if (ctx != null)
    {
      logout(ctx);
    }

    return result;
  }

  /**
   * Method description
   *
   *
   *
   * @param postId
   * @param username
   * @param password
   * @param struct
   * @param publish
   *
   * @return
   *
   * @throws Exception
   */
  public Boolean editPost(String postId, String username, String password,
                          Map struct, Boolean publish)
          throws Exception
  {
    logger.info("Blogger.editPost");

    Boolean result = Boolean.FALSE;
    LoginContext ctx = login(username, password);
    EntryDAO entryDAO = BlogContext.getDAOFactory().getEntryDAO();
    CategoryDAO categoryDAO = BlogContext.getDAOFactory().getCategoryDAO();
    Entry entry = entryDAO.get(convertId(postId));

    if (entry != null)
    {
      boolean prev = false;
      User user = getUser(ctx);

      if (user.equals(entry.getAuthor()) || user.isGlobalAdmin())
      {
        prev = true;
      }
      else
      {
        Blog blog = entry.getBlog();

        prev = isAdmin(user, blog);
      }

      if (prev)
      {
        String title = (String) struct.get("title");
        String content = (String) struct.get("description");
        Object[] categoryArray = (Object[]) struct.get("categories");

        for (Object obj : categoryArray)
        {
          boolean found = false;
          String name = obj.toString();

          for (Category category : entry.getCategories())
          {
            if (category.getName().equals(name))
            {
              found = true;

              break;
            }
          }

          if (!found)
          {
            Category category = categoryDAO.get(entry.getBlog(), name);

            if (category != null)
            {
              entry.addCateogory(category);
            }
          }
        }

        List<Category> removeList = new ArrayList<Category>();

        for (Category category : entry.getCategories())
        {
          boolean found = false;

          for (Object obj : categoryArray)
          {
            String name = obj.toString();

            if (category.getName().equals(name))
            {
              found = true;

              break;
            }
          }

          if (!found)
          {
            removeList.add(category);
          }
        }

        for (Category c : removeList)
        {
          entry.removeCategory(c);
        }

        entry.setTitle(title);
        entry.setContent(content);
        entry.publish();
        result = entryDAO.edit(entry);
      }
      else
      {
        throw new XmlRpcException("user has no access to the post");
      }
    }
    else
    {
      throw new XmlRpcException("post not found");
    }

    if (ctx != null)
    {
      logout(ctx);
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param blogId
   * @param username
   * @param password
   * @param struct
   * @param publish
   *
   * @return
   *
   * @throws Exception
   */
  public String newPost(String blogId, String username, String password,
                        Map struct, Boolean publish)
          throws Exception
  {
    logger.info("Blogger.newPost");

    String result = null;
    LoginContext ctx = login(username, password);
    User user = getUser(ctx);
    Blog blog = findBlog(blogId);

    if (isAuthor(user, blog))
    {
      EntryDAO entryDAO = BlogContext.getDAOFactory().getEntryDAO();
      CategoryDAO categoryDAO = BlogContext.getDAOFactory().getCategoryDAO();
      String title = (String) struct.get("title");
      String content = (String) struct.get("description");

      // Category catgory = categoryDAO.findFirstByBlog(blog);
      Entry entry = new Entry();
      Object[] categories = (Object[]) struct.get("categories");

      for (Object obj : categories)
      {
        String name = obj.toString();
        Category cat = categoryDAO.get(blog, name);

        if (cat != null)
        {
          entry.addCateogory(cat);
        }
        else if (logger.isLoggable(Level.INFO))
        {
          logger.info("category " + name + " not found at blog " + blogId);
        }
      }

      if ((entry.getCategories() == null) || entry.getCategories().isEmpty())
      {
        Category cat = categoryDAO.getFirst(blog);

        entry.addCateogory(cat);
      }

      entry.setBlog(blog);
      entry.setTitle(title);
      entry.setContent(content);
      entry.setAuthor(user);
      entry.publish();

      if (entryDAO.add(entry))
      {
        result = entry.getId().toString();
      }
      else
      {
        throw new XmlRpcException("unknown error");
      }
    }
    else
    {
      throw new XmlRpcException("user has no rights to create posts");
    }

    if (ctx != null)
    {
      logout(ctx);
    }

    return result;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param appKey
   * @param blogId
   * @param username
   * @param password
   * @param templateType
   *
   * @return
   *
   * @throws Exception
   */
  public String getTemplate(String appKey, String blogId, String username,
                            String password, String templateType)
          throws Exception
  {
    logger.info("Blogger.getTemplate");

    String result = null;

    return result;
  }

  /**
   * Method description
   *
   *
   * @param appkey
   * @param username
   * @param password
   *
   * @return
   *
   * @throws Exception
   */
  public Map<String, String> getUserInfo(String appkey, String username,
          String password)
          throws Exception
  {
    logger.info("Blogger.getUserInfo");

    LoginContext ctx = login(username, password);
    User user = getUser(ctx);
    Map<String, String> result = new HashMap<String, String>();

    result.put("nickname", user.getDisplayName());
    result.put("userid", user.getName());
    result.put("email", user.getEmail());
    logout(ctx);

    return result;
  }

  /**
   * Method description
   *
   *
   * @param appkey
   * @param username
   * @param password
   *
   * @return
   *
   * @throws Exception
   */
  public Object getUsersBlogs(String appkey, String username, String password)
          throws Exception
  {
    logger.info("Blogger.getUsersBlogs");

    Vector<Map<String, String>> result = new Vector<Map<String, String>>();
    LoginContext ctx = login(username, password);
    User user = getUser(ctx);

    // LinkBuilder linkBuilder = BlogContext.getInstance().getLinkBuilder();
    UserDAO userDAO = BlogContext.getDAOFactory().getUserDAO();

    // TODO scrolling
    List<BlogMember> memberList = userDAO.getMembers(user, 0, 1000);

    for (BlogMember member : memberList)
    {
      Blog blog = member.getBlog();

      if (blog.isActive())
      {
        Map<String, String> blogMap = new HashMap<String, String>();

        // TODO: replace with linkBuilder
        System.out.println("http://" + blog.getIdentifier() + ":8080/jab/");
        blogMap.put("url", "http://" + blog.getIdentifier() + ":8080/jab/");
        blogMap.put("blogid", blog.getId().toString());
        blogMap.put("blogName", blog.getTitle());
        result.add(blogMap);
      }
    }

    if (ctx != null)
    {
      logout(ctx);
    }

    return result;
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param appkey
   * @param blogId
   * @param username
   * @param password
   * @param template
   * @param templateType
   *
   * @return
   *
   * @throws Exception
   */
  public Boolean setTemplate(String appkey, String blogId, String username,
                             String password, String template,
                             String templateType)
          throws Exception
  {
    logger.info("Blogger.setTemplate");

    Boolean result = Boolean.TRUE;

    return result;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param id
   *
   * @return
   *
   * @throws XmlRpcException
   */
  protected Long convertId(String id) throws XmlRpcException
  {
    Long result = null;

    try
    {
      result = Long.parseLong(id);
    }
    catch (NumberFormatException ex)
    {
      throw new XmlRpcException("wrong id format");
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param blogId
   *
   * @return
   *
   * @throws XmlRpcException
   */
  protected Blog findBlog(String blogId) throws XmlRpcException
  {
    BlogDAO blogDAO = BlogContext.getDAOFactory().getBlogDAO();
    Blog blog = blogDAO.get(convertId(blogId));

    if (blog == null)
    {
      throw new XmlRpcException("no such blog");
    }

    if (!blog.isActive())
    {
      throw new XmlRpcException("blog is disabled");
    }

    return blog;
  }

  /**
   * Method description
   *
   *
   * @param username
   * @param password
   *
   * @return
   *
   * @throws XmlRpcNotAuthorizedException
   */
  protected LoginContext login(String username, String password)
          throws XmlRpcNotAuthorizedException
  {
    LoginContext ctx = null;

    try
    {
      BlogContext context = BlogContext.getInstance();

      ctx = context.buildLoginContext(username, password.toCharArray());
      ctx.login();
    }
    catch (LoginException ex)
    {
      logger.log(Level.WARNING, null, ex);

      throw new XmlRpcNotAuthorizedException("login failure");
    }

    return ctx;
  }

  /**
   * Method description
   *
   *
   * @param ctx
   *
   * @throws XmlRpcNotAuthorizedException
   */
  protected void logout(LoginContext ctx) throws XmlRpcNotAuthorizedException
  {
    try
    {
      ctx.logout();
    }
    catch (LoginException ex)
    {
      logger.log(Level.WARNING, null, ex);

      throw new XmlRpcNotAuthorizedException("logout failure");
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param ctx
   *
   * @return
   *
   * @throws XmlRpcNotAuthorizedException
   */
  protected User getUser(LoginContext ctx) throws XmlRpcNotAuthorizedException
  {
    User user = null;
    Set<User> userSet = ctx.getSubject().getPrincipals(User.class);

    if ((userSet != null) &&!userSet.isEmpty())
    {
      user = userSet.iterator().next();
    }
    else
    {
      throw new XmlRpcNotAuthorizedException("invalid login");
    }

    return user;
  }

  /**
   * Method description
   *
   *
   * @param user
   * @param blog
   *
   * @return
   */
  protected boolean isAdmin(User user, Blog blog)
  {
    UserDAO userDAO = BlogContext.getDAOFactory().getUserDAO();
    Role role = userDAO.getRole(blog, user);

    return (role != null) && (role == Role.ADMIN);
  }

  /**
   * Method description
   *
   *
   * @param user
   * @param blog
   *
   * @return
   */
  protected boolean isAuthor(User user, Blog blog)
  {
    UserDAO userDAO = BlogContext.getDAOFactory().getUserDAO();
    Role role = userDAO.getRole(blog, user);

    return (role != null) && ((role == Role.ADMIN) || (role == Role.AUTHOR));
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  protected Logger logger;
}
