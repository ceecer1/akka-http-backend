package io.kodeasync.server.util

import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s
import org.json4s.{DefaultFormats, Formats, Serialization}

/**
  * Created by shishir on 2/4/17.
  */
trait JsonSerialization extends Json4sSupport {

  implicit val formats: Formats = DefaultFormats
  implicit val serialization: Serialization = json4s.native.Serialization

}
