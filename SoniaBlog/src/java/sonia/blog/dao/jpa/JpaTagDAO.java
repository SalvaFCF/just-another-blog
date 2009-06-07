/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.blog.dao.jpa;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.app.Constants;
import sonia.blog.api.dao.TagDAO;
import sonia.blog.api.util.TagWrapper;
import sonia.blog.entity.Blog;
import sonia.blog.entity.Tag;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author sdorra
 */
public class JpaTagDAO extends JpaGenericDAO<Tag> implements TagDAO
{

  /** Field description */
  private static Logger logger = Logger.getLogger(JpaTagDAO.class.getName());

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param entityManagerFactory
   */
  public JpaTagDAO(EntityManagerFactory entityManagerFactory)
  {
    super(entityManagerFactory, Tag.class, Constants.LISTENER_TAG);
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public long count()
  {
    return countQuery("Tag.count");
  }

  /**
   * Method description
   *
   *
   * @param blog
   *
   * @return
   */
  public List<Tag> findAllByBlog(Blog blog)
  {
    return findList("Tag.findAllByBlog", blog);
  }

  /**
   * Method description
   *
   *
   * @param blog
   * @param start
   * @param max
   *
   * @return
   */
  public List<Tag> findAllByBlog(Blog blog, int start, int max)
  {
    return findList("Tag.findAllByBlog", blog, start, max);
  }

  /**
   * Method description
   *
   *
   * @param blog
   *
   * @return
   */
  public List<TagWrapper> findByBlogAndCount(Blog blog)
  {
    return findByBlogAndCount(blog, -1, -1);
  }

  /**
   * Method description
   *
   *
   * @param blog
   * @param start
   * @param max
   *
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<TagWrapper> findByBlogAndCount(Blog blog, int start, int max)
  {
    List<TagWrapper> tags = null;
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Tag.findByBlogAndCount");

    q.setParameter("blog", blog);

    if (start > 0)
    {
      q.setFirstResult(start);
    }

    if (max > 0)
    {
      q.setMaxResults(max);
    }

    try
    {
      tags = q.getResultList();
    }
    catch (NoResultException ex) {}
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }

    return tags;
  }

  /**
   * Method description
   *
   *
   * @param name
   *
   * @return
   */
  public Tag findByName(String name)
  {
    Tag tag = null;
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Tag.findByName");

    q.setParameter("name", name);

    try
    {
      tag = (Tag) q.getSingleResult();
    }
    catch (NoResultException ex) {}
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }

    return tag;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public List<Tag> getAll()
  {
    return findList("Tag.findAll");
  }

  /**
   * Method description
   *
   *
   * @param start
   * @param max
   *
   * @return
   */
  public List<Tag> getAll(int start, int max)
  {
    return findList("Tag.findAll", start, max);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  protected Logger getLogger()
  {
    return logger;
  }
}
