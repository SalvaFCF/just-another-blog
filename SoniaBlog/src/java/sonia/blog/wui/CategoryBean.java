/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.blog.wui;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.app.BlogContext;
import sonia.blog.api.util.AbstractBean;
import sonia.blog.entity.Category;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;
import java.util.logging.Level;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author sdorra
 */
public class CategoryBean extends AbstractBean
{

  /**
   * Constructs ...
   *
   */
  public CategoryBean()
  {
    category = new Category();
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public String edit()
  {
    category = (Category) categories.getRowData();

    return SUCCESS;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String newCategory()
  {
    category = new Category();

    return SUCCESS;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String remove()
  {
    String result = SUCCESS;
    Category cat = (Category) categories.getRowData();

    if (cat.getEntries().isEmpty())
    {
      EntityManager em = BlogContext.getInstance().getEntityManager();

      em.getTransaction().begin();

      try
      {
        em.remove(em.merge(cat));
        messageHandler.info("removeCategorySuccess");
        em.getTransaction().commit();
      }
      catch (Exception ex)
      {
        if (em.getTransaction().isActive())
        {
          em.getTransaction().rollback();
        }

        logger.log(Level.SEVERE, null, ex);
        result = FAILURE;
        messageHandler.error("categoryActionFailure");
      }
      finally
      {
        em.close();
      }
    }
    else
    {
      result = FAILURE;
      messageHandler.warn("categoryHasEntries");
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String save()
  {
    String result = SUCCESS;
    EntityManager em = BlogContext.getInstance().getEntityManager();

    em.getTransaction().begin();

    try
    {
      category.setBlog(getRequest().getCurrentBlog());

      if (category.getId() != null)
      {
        em.merge(category);
        messageHandler.info("updateCategorySuccess");
      }
      else
      {
        em.persist(category);
        messageHandler.info("createCategorySuccess");
      }

      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      if (em.getTransaction().isActive())
      {
        em.getTransaction().rollback();
      }

      logger.log(Level.SEVERE, null, ex);
      messageHandler.error("categoryActionFailure");
      result = FAILURE;
    }
    finally
    {
      em.close();
    }

    return result;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  @SuppressWarnings("unchecked")
  public DataModel getCategories()
  {
    categories = new ListDataModel();

    EntityManager em = BlogContext.getInstance().getEntityManager();
    Query q = em.createNamedQuery("Category.findAllFromBlog");

    q.setParameter("blog", getRequest().getCurrentBlog());

    List<Category> categoryList = q.getResultList();

    if (categoryList != null)
    {
      categories.setWrappedData(categoryList);
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
  public Category getCategory()
  {
    return category;
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
   * @param category
   */
  public void setCategory(Category category)
  {
    this.category = category;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private DataModel categories;

  /** Field description */
  private Category category;
}
