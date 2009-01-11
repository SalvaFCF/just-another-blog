/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.blog.dao.jpa;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.dao.CategoryDAO;
import sonia.blog.entity.Blog;
import sonia.blog.entity.Category;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author sdorra
 */
public class JpaCategoryDAO extends JpaGenericDAO<Category>
        implements CategoryDAO
{

  /**
   * Constructs ...
   *
   *
   * @param entityManagerFactory
   */
  public JpaCategoryDAO(EntityManagerFactory entityManagerFactory)
  {
    super(entityManagerFactory, Category.class);
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
    return countQuery("Category.count");
  }

  /**
   * Method description
   *
   *
   * @param blog
   *
   * @return
   */
  public long countByBlog(Blog blog)
  {
    return countQuery("Category.countByBlog", blog);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public List<Category> findAll()
  {
    return findList("Category.findAll");
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
  public List<Category> findAll(int start, int max)
  {
    return findList("Category.findAll", start, max);
  }

  /**
   * Method description
   *
   *
   * @param blog
   *
   * @return
   */
  public List<Category> findAllByBlog(Blog blog)
  {
    return findList("Category.findAllByBlog", blog);
  }

  /**
   * Method description
   *
   *
   * @param blog
   *
   * @return
   */
  public Category findFirstByBlog(Blog blog)
  {
    Category category = null;
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Category.findFirstByBlog");

    q.setParameter("blog", blog);

    try
    {
      category = (Category) q.getSingleResult();
    }
    catch (NoResultException ex) {}
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }

    return category;
  }
}