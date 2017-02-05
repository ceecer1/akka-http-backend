package io.kodeasync.server.rest

import akka.http.scaladsl.server.Route
import io.kodeasync.server.rest.resources.ComicResources
import io.kodeasync.server.service.MarvelHttpComponent
import io.kodeasync.server.util.AkkaServiceProvider
import io.kodeasync.server.rest.resources.ComicResources
import io.kodeasync.server.service.MarvelHttpComponent
import io.kodeasync.server.util.AkkaServiceProvider
import ch.megard.akka.http.cors.CorsDirectives._
import ch.megard.akka.http.cors.CorsSettings

/**
  * Created by shishir on 2/4/17.
  */
trait RestModule extends ComicResources {

  this: MarvelHttpComponent with AkkaServiceProvider =>

  val settings = CorsSettings.defaultSettings.copy(allowCredentials = false)

  val routes: Route = cors(settings) {
    comicRoute
  }

}
