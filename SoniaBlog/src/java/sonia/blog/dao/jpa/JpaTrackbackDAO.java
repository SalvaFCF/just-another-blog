/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.blog.dao.jpa;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.app.Constants;
import sonia.blog.api.dao.TrackbackDAO;
import sonia.blog.entity.Entry;
import sonia.blog.entity.Trackback;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 *
 * @author sdorra
 */
public class JpaTrackbackDAO extends JpaGenericDAO<Trackback>
        implements TrackbackDAO
{

  /** Field description */
  private static Logger logger =
    Logger.getLogger(JpaTrackbackDAO.class.getName());

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param entityManagerFactory
   */
  public JpaTrackbackDAO(EntityManagerFactory entityManagerFactory)
  {
    super(entityManagerFactory, Trackback.class, Constants.LISTENER_TRACKBACK);
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
    return countQuery("Trackback.count");
  }

  /**
   * Method description
   *
   *
   * @param entry
   * @param type
   * @param url
   *
   * @return
   */
  public long count(Entry entry, int type, String url)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Trackback.countByEntryTypeAndUrl");

    q.setParameter("entry", entry);
    q.setParameter("type", type);
    q.setParameter("url", url);

    return (Long) q.getSingleResult();
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public List<Trackback> getAll()
  {
    return findList("Trackback.getAll");
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
  public List<Trackback> getAll(int start, int max)
  {
    return findList("Trackback.getAll", start, max);
  }

  /**
   * Method description
   *
   *
   * @param entry
   *
   * @return
   */
  public List<Trackback> getAll(Entry entry)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Trackback.getAllByEntry");

    q.setParameter("entry", entry);

    return excecuteListQuery(em, q);
  }

  /**
   * Method description
   *
   *
   * @param entry
   * @param start
   * @param max
   *
   * @return
   */
  public List<Trackback> getAll(Entry entry, int start, int max)
  {
    EntityManager em = createEntityManager();
    Query q = em.createNamedQuery("Trackback.getAllByEntry");

    q.setParameter("entry", entry);
    q.setFirstResult(start);
    q.setMaxResults(max);

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
