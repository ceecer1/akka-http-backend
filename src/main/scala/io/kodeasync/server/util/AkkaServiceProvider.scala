package io.kodeasync.server.util

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import io.kodeasync.server.boot.Config.{ServerConfig, config}
import io.kodeasync.server.rest.client.MarvelHttpClient.ComicDataInfo

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Created by shishir on 2/4/17.
  */
trait AkkaServiceProvider {

  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit val execContext: ExecutionContextExecutor
  implicit val timeout: Timeout

  type Info = Future[Option[ComicDataInfo]]
  implicit val cache: Cache[Info]

}

trait DefaultAkkaServiceProvider extends AkkaServiceProvider {
  override implicit val system: ActorSystem = ActorSystem(ServerConfig.systemName, config)
  override implicit val materializer: ActorMaterializer = ActorMaterializer()
  override implicit val timeout: Timeout = Timeout(ServerConfig.defaultTimeout.seconds)
  override implicit val execContext: ExecutionContextExecutor = system.dispatcher
  override implicit val cache = new SimpleLruCache[Info](10, 10)
}
