package io.kodeasync.server.util

import org.slf4j.LoggerFactory

/**
  * Created by shishir on 2/4/17.
  */
trait Loggable {

  val logger = LoggerFactory.getLogger(getClass)

}
