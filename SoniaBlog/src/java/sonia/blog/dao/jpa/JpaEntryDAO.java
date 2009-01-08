/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.blog.dao.jpa;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.dao.EntryDAO;
import sonia.blog.entity.Blog;
import sonia.blog.entity.Category;
import sonia.blog.entity.Entry;
import sonia.blog.entity.Tag;

//~--- JDK imports ------------------------------------------------------------

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import sonia.blog.entity.User;

/**
 *
 * @author sdorra
 */
public class JpaEntryDAO extends JpaGenericDAO<Entry> implements EntryDAO
{

  /**
   * Constructs ...
   *
   *
   * @param entityManagerFactory
   */
  public JpaEntryDAO(EntityManagerFactory entityManagerFactory)
  {
    super(entityManagerFactory, Entry.class);
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
    return countQuery("Entry.count");
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
    return countQuery("Entry.countByBlog", blog);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public List<Entry> findAll()
  {
    return findList("Entry.findAll");
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public List<Entry> findAllActives()
  {
    return findList("Entry.findAllActives");
  }

  /**
   * Method description
   *
   *
   * @param blog
   *
   * @return
   */
  public List<Entry> findAllActivesByBlog(Blog blog)
  {
    return findList("Entry.findAllActivesByBlog", blog);
  }

  /**
   * Method description
   *
   *
   * @param blog
   * @param startDate
   * @param endDate
   *
   * @return
   */
  public List<Entry> findByBlogAndDate(Blog blog, Date startDate, Date endDate)
  {
    List<Entry> entries = null;
    EntityManager em = createEntityManager();

    try
    {
      Query q = em.createNamedQuery("Entry.findByBlogAndDate");

      q.setParameter("blog", blog);
      q.setParameter("start", startDate);
      q.setParameter("end", endDate);
      entries = q.getResultList();
    }
    catch (NoResultException ex) {}
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }

    return entries;
  }

  /**
   * Method description
   *
   *
   * @param blog
   * @param tag
   *
   * @return
   */
  public List<Entry> findByBlogAndTag(Blog blog, Tag tag)
  {
    List<Entry> entries = null;
    EntityManager em = createEntityManager();

    try
    {
      Query q = em.createNamedQuery("Entry.findByBlogAndTag");

      q.setParameter("blog", blog);
      q.setParameter("tag", tag);
      entries = q.getResultList();
    }
    catch (NoResultException ex) {}
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }

    return entries;
  }

  /**
   * Method description
   *
   *
   * @param category
   *
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<Entry> findByCategory(Category category)
  {
    List<Entry> entries = null;
    EntityManager em = createEntityManager();

    try
    {
      Query q = em.createNamedQuery("Entry.findByCategory");

      q.setParameter("category", category);
      entries = q.getResultList();
    }
    catch (NoResultException ex) {}
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }

    return entries;
  }

  /**
   * Method description
   *
   *
   * @param blog
   * @param startDate
   * @param endDate
   *
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<Date> findCalendarDates(Blog blog, Date startDate, Date endDate)
  {
    List<Date> dates = null;
    EntityManager em = createEntityManager();

    try
    {
      Query q = em.createNamedQuery("Entry.calendar");

      q.setParameter("blog", blog);
      q.setParameter("start", startDate);
      q.setParameter("end", endDate);
      dates = q.getResultList();
    }
    catch (NoResultException ex) {}
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }

    return dates;
  }

  @SuppressWarnings("unchecked")
  public List<Entry> findAllDraftsByBlogAndUser(Blog blog, User user)
  {
    List<Entry> entries = null;

    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("User.findAllDraftsByBlogAndUser");
    q.setParameter("blog", blog);
    q.setParameter("user", user);
    try
    {
      entries = q.getResultList();
    }
    catch (NoResultException ex){}
    finally
    {
      if ( em != null )
      {
        em.close();
      }
    }

    return entries;
  }
}
