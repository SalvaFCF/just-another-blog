/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.blog.dao.jpa;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.app.BlogConfiguration;
import sonia.blog.api.app.BlogContext;
import sonia.blog.api.app.Constants;
import sonia.blog.api.dao.AttachmentDAO;
import sonia.blog.api.dao.BlogDAO;
import sonia.blog.api.dao.BlogHitCountDAO;
import sonia.blog.api.dao.CategoryDAO;
import sonia.blog.api.dao.CommentDAO;
import sonia.blog.api.dao.DAOFactory;
import sonia.blog.api.dao.EntryDAO;
import sonia.blog.api.dao.TagDAO;
import sonia.blog.api.dao.UserDAO;
import sonia.blog.api.dao.cache.CacheManager;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author sdorra
 */
public class JpaDAOFactory extends DAOFactory
{

  /**
   * Method description
   *
   */
  @Override
  public void close()
  {
    if (entityManagerFactory != null)
    {
      entityManagerFactory.close();
    }
  }

  /**
   * Method description
   *
   */
  @Override
  public void init()
  {
    BlogConfiguration config = BlogContext.getInstance().getConfiguration();
    Map<String, String> parameters = new HashMap<String, String>();

    parameters.put("toplink.jdbc.driver",
                   config.getString(Constants.CONFIG_DB_DRIVER));
    parameters.put("toplink.jdbc.url",
                   config.getString(Constants.CONFIG_DB_URL));
    parameters.put("toplink.jdbc.user",
                   config.getString(Constants.CONFIG_DB_USERNAME));
    parameters.put("toplink.jdbc.password",
                   config.getEncString(Constants.CONFIG_DB_PASSWORD));
    entityManagerFactory =
      Persistence.createEntityManagerFactory("SoniaBlog-PU", parameters);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public AttachmentDAO getAttachmentDAO()
  {
    return new JpaAttachmentDAO(entityManagerFactory);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public BlogDAO getBlogDAO()
  {
    return new JpaBlogDAO(entityManagerFactory);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public BlogHitCountDAO getBlogHitCountDAO()
  {
    return new JpaBlogHitCountDAO(entityManagerFactory);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public CacheManager getCacheManager()
  {
    return null;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public CategoryDAO getCategoryDAO()
  {
    return new JpaCategoryDAO(entityManagerFactory);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public CommentDAO getCommentDAO()
  {
    return new JpaCommentDAO(entityManagerFactory);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public Object getConnection()
  {
    return entityManagerFactory;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public EntityManagerFactory getEntityManagerFactory()
  {
    return entityManagerFactory;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public EntryDAO getEntryDAO()
  {
    return new JpaEntryDAO(entityManagerFactory);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public TagDAO getTagDAO()
  {
    return new JpaTagDAO(entityManagerFactory);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public UserDAO getUserDAO()
  {
    return new JpaUserDAO(entityManagerFactory);
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private EntityManagerFactory entityManagerFactory;
}
