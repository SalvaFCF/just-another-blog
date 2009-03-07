/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.plugin.service;

//~--- JDK imports ------------------------------------------------------------

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author sdorra
 */
@Documented @Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
public @interface Service
{
  String value();
}