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



package sonia.cache;

//~--- non-JDK imports --------------------------------------------------------

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Sebastian Sdorra
 */
public class SimpleExpirationCacheTest extends CacheTestBase
{

  /**
   * Method description
   *
   *
   * @throws InterruptedException
   */
  @Test
  public void expirationTest() throws InterruptedException
  {
    ObjectCache cache = getCache();

    assertEquals("value", cache.put("key", "value"));
    assertEquals("value", cache.get("key"));
    Thread.sleep(1001l);
    assertNull(cache.get("key"));
    assertTrue(cache.size() == 0);
    assertTrue(cache.isEmpty());
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   *
   * @return
   */

  /*
   * @Test
   * public void extendedExpirationTest() throws InterruptedException
   * {
   * long time = 1000l;
   * ObjectCache cache = new SimpleExpirationCache("junit", time, true);
   *
   * assertEquals("value", cache.put("key", "value"));
   * assertFalse(cache.isEmpty());
   * Thread.sleep(3010l);
   * assertTrue(cache.isEmpty());
   * }
   */

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  protected ObjectCache getCache()
  {
    long time = 1000l;

    return new SimpleExpirationCache("junit", time, false);
  }
}
