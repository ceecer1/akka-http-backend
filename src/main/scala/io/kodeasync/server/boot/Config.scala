package io.kodeasync.server.boot

import com.typesafe.config.ConfigFactory

/**
  * Created by shishir on 2/4/17.
  */
object Config {

  val config = ConfigFactory.load()

  case object ServerConfig {
    private val serverConfig = config.getConfig("server")
    lazy val systemName = serverConfig.getString("system-name")
    lazy val port = serverConfig.getInt("port")
    lazy val interface = serverConfig.getString("interface")
    lazy val defaultTimeout = serverConfig.getInt("request-timeout")
  }

  case object MarvelConfig {
    private val marvelConfig = config.getConfig("marvel-api")
    lazy val key = marvelConfig.getString("key")
    lazy val secret = marvelConfig.getString("secret")
  }

}
