/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.blog.dao.jpa;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.app.Constants;
import sonia.blog.api.dao.PageDAO;
import sonia.blog.api.util.BasicPageNavigation;
import sonia.blog.api.util.PageNavigation;
import sonia.blog.entity.Attachment;
import sonia.blog.entity.Blog;
import sonia.blog.entity.Page;

import sonia.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 *
 * @author sdorra
 */
public class JpaPageDAO extends JpaGenericDAO<Page> implements PageDAO
{

  /** Field description */
  private static Logger logger = Logger.getLogger(JpaPageDAO.class.getName());

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param entityManagerFactory
   */
  public JpaPageDAO(EntityManagerFactory entityManagerFactory)
  {
    super(entityManagerFactory, Page.class, Constants.LISTENER_PAGE);
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
    return countQuery("Page.count");
  }

  /**
   * Method description
   *
   *
   * @param blog
   *
   * @return
   */
  public long count(Blog blog)
  {
    return countQuery("Page.count", blog);
  }

  /**
   * Method description
   *
   *
   * @param item
   *
   * @return
   */
  @Override
  public boolean remove(Page item)
  {
    boolean result = false;
    EntityManager em = createEntityManager();

    try
    {
      em.getTransaction().begin();

      List<Attachment> attachments = item.getAttachments();

      if (Util.hasContent(attachments))
      {
        for (Attachment a : attachments)
        {
          em.remove(em.merge(a));
        }
      }

      em.remove(em.merge(item));
      em.getTransaction().commit();
      result = true;
    }
    catch (Exception ex)
    {
      if (em.getTransaction().isActive())
      {
        em.getTransaction().rollback();
      }

      logger.log(Level.SEVERE, null, ex);
    }
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }

    return result;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param id
   * @param blog
   * @param published
   *
   * @return
   */
  public Page get(Long id, Blog blog, boolean published)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Page.getByIdBlogAndPublished");

    q.setParameter("id", id);
    q.setParameter("blog", blog);
    q.setParameter("published", published);

    return excecuteQuery(em, q);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public List<Page> getAll()
  {
    return findList("Page.getAll");
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
  public List<Page> getAll(int start, int max)
  {
    return findList("Page.getAll", start, max);
  }

  /**
   * Method description
   *
   *
   * @param blog
   * @param published
   *
   * @return
   */
  public List<? extends PageNavigation> getAllRoot(Blog blog, boolean published)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Page.getAllRootWithPublished");

    q.setParameter("blog", blog);
    q.setParameter("published", published);

    return excecuteListQuery(BasicPageNavigation.class, em, q);
  }

  /**
   * Method description
   *
   *
   * @param blog
   *
   * @return
   */
  public List<? extends PageNavigation> getAllRoot(Blog blog)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Page.getAllRoot");

    q.setParameter("blog", blog);

    return excecuteListQuery(BasicPageNavigation.class, em, q);
  }

  /**
   * Method description
   *
   *
   * @param parent
   * @param published
   *
   * @return
   */
  public List<? extends PageNavigation> getChildren(Page parent,
          boolean published)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Page.getChildrenWithPublished");

    q.setParameter("parent", parent);
    q.setParameter("published", published);

    return excecuteListQuery(BasicPageNavigation.class, em, q);
  }

  /**
   * Method description
   *
   *
   * @param parent
   *
   * @return
   */
  public List<? extends PageNavigation> getChildren(Page parent)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Page.getChildren");

    q.setParameter("parent", parent);

    return excecuteListQuery(BasicPageNavigation.class, em, q);
  }

  /**
   * Method description
   *
   *
   * @param parent
   *
   * @return
   */
  public List<? extends PageNavigation> getChildren(PageNavigation parent)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Page.getChildrenById");

    q.setParameter("id", parent.getId());

    return excecuteListQuery(BasicPageNavigation.class, em, q);
  }

  /**
   * Method description
   *
   *
   * @param parent
   * @param published
   *
   * @return
   */
  public List<? extends PageNavigation> getChildren(PageNavigation parent,
          boolean published)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Page.getChildrenByIdAndPublished");

    q.setParameter("id", parent.getId());
    q.setParameter("published", published);

    return excecuteListQuery(BasicPageNavigation.class, em, q);
  }

  /**
   * Method description
   *
   *
   * @param parent
   *
   * @return
   */
  public List<Page> getPageChildren(Page parent)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Page.getPageChildren");

    q.setParameter("parent", parent);

    return excecuteListQuery(em, q);
  }

  /**
   * Method description
   *
   *
   * @param parent
   * @param published
   *
   * @return
   */
  public List<Page> getPageChildren(Page parent, boolean published)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Page.getPageChildrenWithPublished");

    q.setParameter("published", published);
    q.setParameter("parent", parent);

    return excecuteListQuery(em, q);
  }

  /**
   * Method description
   *
   *
   * @param blog
   *
   * @return
   */
  public List<Page> getRootPages(Blog blog)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Page.getRootPages");

    q.setParameter("blog", blog);

    return excecuteListQuery(em, q);
  }

  /**
   * Method description
   *
   *
   * @param blog
   * @param published
   *
   * @return
   */
  public List<Page> getRootPages(Blog blog, boolean published)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Page.getRootPagesWithPublished");

    q.setParameter("published", published);
    q.setParameter("blog", blog);

    return excecuteListQuery(em, q);
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
