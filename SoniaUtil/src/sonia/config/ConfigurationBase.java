/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.config;

/**
 *
 * @author sdorra
 */
public abstract class ConfigurationBase implements Configuration
{

  /**
   * Method description
   *
   *
   * @param key
   * @param def
   *
   * @return
   */
  public Boolean getBoolean(String key, Boolean def)
  {
    Boolean result = getBoolean(key);

    if (result == null)
    {
      result = def;
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param key
   * @param def
   *
   * @return
   */
  public Boolean[] getBooleans(String key, Boolean[] def)
  {
    Boolean[] result = getBooleans(key);

    if (result == null)
    {
      result = def;
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param key
   * @param def
   *
   * @return
   */
  public Double getDouble(String key, Double def)
  {
    Double result = getDouble(key);

    if (result == null)
    {
      result = def;
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param key
   * @param def
   *
   * @return
   */
  public Double[] getDoubles(String key, Double[] def)
  {
    Double[] result = getDoubles(key);

    if (result == null)
    {
      result = def;
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public ExceptionHandler getExceptionHandler()
  {
    if (exceptionHandler == null)
    {
      exceptionHandler = new DefaultExceptionHandler();
    }

    return exceptionHandler;
  }

  /**
   * Method description
   *
   *
   * @param key
   * @param def
   *
   * @return
   */
  public Float getFloat(String key, Float def)
  {
    Float result = getFloat(key);

    if (result == null)
    {
      result = def;
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param key
   * @param def
   *
   * @return
   */
  public Float[] getFloats(String key, Float[] def)
  {
    Float[] result = getFloats(key);

    if (result == null)
    {
      result = def;
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param key
   * @param def
   *
   * @return
   */
  public Integer getInteger(String key, Integer def)
  {
    Integer result = getInteger(key);

    if (result == null)
    {
      result = def;
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param key
   * @param def
   *
   * @return
   */
  public Integer[] getIntegers(String key, Integer[] def)
  {
    Integer[] result = getIntegers(key);

    if (result == null)
    {
      result = getIntegers(key);
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param key
   * @param def
   *
   * @return
   */
  public Long getLong(String key, Long def)
  {
    Long result = getLong(key);

    if (result == null)
    {
      result = def;
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param key
   * @param def
   *
   * @return
   */
  public Long[] getLongs(String key, Long[] def)
  {
    Long[] result = getLongs(key);

    if (result == null)
    {
      result = def;
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param clazz
   * @param key
   * @param def
   *
   * @return
   */
  public <T>T getObject(Class<T> clazz, String key, T def)
  {
    T result = getObject(clazz, key);

    if (result == null)
    {
      result = def;
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param clazz
   * @param key
   * @param def
   *
   * @return
   */
  public <T>T[] getObjects(Class<T> clazz, String key, T[] def)
  {
    T[] result = getObjects(clazz, key);

    if (result == null)
    {
      result = def;
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param key
   * @param def
   *
   * @return
   */
  public String getString(String key, String def)
  {
    String result = getString(key);

    if (result == null)
    {
      result = def;
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param key
   * @param def
   *
   * @return
   */
  public String[] getStrings(String key, String[] def)
  {
    String[] result = getStrings(key);

    if (result == null)
    {
      result = def;
    }

    return result;
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param exceptionHandler
   */
  public void setExceptionHandler(ExceptionHandler exceptionHandler)
  {
    this.exceptionHandler = exceptionHandler;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  protected ExceptionHandler exceptionHandler;
}