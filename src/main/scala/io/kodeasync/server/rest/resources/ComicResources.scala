package io.kodeasync.server.rest.resources

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.kodeasync.server.util.{AkkaServiceProvider, JsonSerialization, Loggable}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.pattern.ask
import io.kodeasync.server.service.MarvelHttpComponent
import io.kodeasync.server.util.{JsonSerialization, Loggable}
import io.kodeasync.server.service.MarvelHttpComponent

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by shishir on 2/4/17.
  */
trait ComicResources extends JsonSerialization with Loggable {

  this: MarvelHttpComponent =>

  lazy val getComics = post & path("comics") & entity(as[List[Int]])

  def comicRoute: Route = getComics { idList =>
    logger.info("ComicId list received " + idList)
    complete(marvelHttpService.getComics(idList))
  }

}
