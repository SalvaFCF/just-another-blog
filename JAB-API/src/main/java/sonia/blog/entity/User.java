/**
 * Copyright (c) 2009, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of JAB; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://kenai.com/projects/jab
 *
 */



package sonia.blog.entity;

//~--- JDK imports ------------------------------------------------------------

import java.io.Serializable;

import java.security.Principal;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Sebastian Sdorra
 */
public class User implements Serializable, Principal, PermaObject
{

  /** Field description */
  private static final long serialVersionUID = 110524856290373350L;

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  public User()
  {
    globalAdmin = false;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param object
   *
   * @return
   */
  @Override
  public boolean equals(Object object)
  {

    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof User))
    {
      return false;
    }

    User other = (User) object;

    if (((this.id == null) && (other.id != null))
        || ((this.id != null) &&!this.id.equals(other.id)))
    {
      return false;
    }

    return true;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public int hashCode()
  {
    int hash = 0;

    hash += ((id != null)
             ? id.hashCode()
             : 0);

    return hash;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public String toString()
  {
    return "sonia.blop.entity.User[id=" + id + "]";
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public String getActivationCode()
  {
    return activationCode;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getAvatar()
  {
    return avatar;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getDisplayName()
  {
    return displayName;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getEmail()
  {
    return email;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getHomePage()
  {
    return homePage;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Long getId()
  {
    return id;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Date getLastLogin()
  {
    return lastLogin;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getName()
  {
    return name;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Date getRegistrationDate()
  {
    return registrationDate;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public boolean isActive()
  {
    return active;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public boolean isGlobalAdmin()
  {
    return globalAdmin;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public boolean isSelfManaged()
  {
    return selfManaged;
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param active
   */
  public void setActive(boolean active)
  {
    this.active = active;
  }

  /**
   * Method description
   *
   *
   * @param avatar
   */
  public void setAvatar(String avatar)
  {
    this.avatar = avatar;
  }

  /**
   * Method description
   *
   *
   * @param displayName
   */
  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }

  /**
   * Method description
   *
   *
   * @param email
   */
  public void setEmail(String email)
  {
    this.email = email;
  }

  /**
   * Method description
   *
   *
   * @param globalAdmin
   */
  public void setGlobalAdmin(boolean globalAdmin)
  {
    this.globalAdmin = globalAdmin;
  }

  /**
   * Method description
   *
   *
   * @param homePage
   */
  public void setHomePage(String homePage)
  {
    this.homePage = homePage;
  }

  /**
   * Method description
   *
   *
   * @param id
   */
  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * Method description
   *
   *
   * @param lastLogin
   */
  public void setLastLogin(Date lastLogin)
  {
    this.lastLogin = lastLogin;
  }

  /**
   * Method description
   *
   *
   * @param name
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Method description
   *
   *
   * @param password
   */
  public void setPassword(String password)
  {
    this.password = password;
  }

  /**
   * Method description
   *
   *
   * @param selfManaged
   */
  public void setSelfManaged(boolean selfManaged)
  {
    this.selfManaged = selfManaged;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   */
  void prePersists()
  {
    registrationDate = new Date();
    activationCode = buildActivationCode();
  }

  /**
   * Method description
   *
   *
   * @return
   */
  private String buildActivationCode()
  {
    return UUID.randomUUID().toString();
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private String activationCode;

  /** Field description */
  private String avatar;

  /** Field description */
  private String displayName;

  /** Field description */
  private String email;

  /** Field description */
  private boolean globalAdmin;

  /** Field description */
  private String homePage;

  /** Field description */
  private Long id;

  /** Field description */
  private Date lastLogin;

  /** Field description */
  private String name;

  /** Field description */
  private String password;

  /** Field description */
  private Date registrationDate;

  /** Field description */
  private boolean selfManaged = true;

  /** Field description */
  private boolean active = true;
}
