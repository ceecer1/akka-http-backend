package io.kodeasync.server.boot

import akka.http.scaladsl.Http
import Config.ServerConfig
import io.kodeasync.server.rest.RestModule
import io.kodeasync.server.service.{DefaultMarvelHttpComponent, MarvelHttpComponent}
import io.kodeasync.server.util.{AkkaServiceProvider, DefaultAkkaServiceProvider, Loggable}
import io.kodeasync.server.service.{DefaultMarvelHttpComponent, MarvelHttpComponent}
import io.kodeasync.server.util.{AkkaServiceProvider, DefaultAkkaServiceProvider, Loggable}

/**
  * Created by shishir on 2/4/17.
  */
object Server extends App with ApplicationConfig with Loggable {

  val bindingFut = Http().bindAndHandle(routes, ServerConfig.interface, ServerConfig.port)

  bindingFut.onFailure {
    case e: Exception =>
      logger.error(s"Failed to bind $e")
  }

  println(s"Server online at http://localhost:8080. Press any key to stop...")
  Console.in.read().toChar
  bindingFut.flatMap(_.unbind()).onComplete(_ => system.terminate())

}

/**
  * ApplicationStack defines what modules the application requires.
  */
trait ApplicationStack extends RestModule {
  this: MarvelHttpComponent with AkkaServiceProvider =>
}

/**
  *  Configure concrete module implementations currently used by http server.
  */
trait ApplicationConfig extends ApplicationStack
  with DefaultMarvelHttpComponent
  with DefaultAkkaServiceProvider



