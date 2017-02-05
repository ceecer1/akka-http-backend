package io.kodeasync.server.service

import java.security.MessageDigest

import io.kodeasync.server.rest.client.MarvelHttpClient
import io.kodeasync.server.util.{AkkaServiceProvider, Loggable}
import io.kodeasync.server.boot.Config.MarvelConfig
import io.kodeasync.server.rest.client.MarvelHttpClient._
import io.kodeasync.server.util.{AkkaServiceProvider, Loggable}

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by shishir on 2/4/17.
  */

trait MarvelHttpComponent {

    val marvelHttpService: MarvelHttpService

    trait MarvelHttpService {
      def getComics(comicIdList: List[Int]): Future[List[Option[MarvelResponse]]]
      def getComicById(comicId: Int, currentTs: Long, hash: String): Future[Future[Option[ComicDataInfo]]]
    }
}

trait DefaultMarvelHttpComponent extends MarvelHttpComponent with MarvelHttpClient {

  this: AkkaServiceProvider =>

  override val marvelHttpService: MarvelHttpService = new DefaultMarvelHttpService

  case class ComicServiceHandlerException(e: String) extends Exception

  private class DefaultMarvelHttpService extends MarvelHttpService with Loggable {

    override def getComics(comicIdList: List[Int]): Future[List[Option[ComicDataInfo]]] = {

      val currentTs = System.currentTimeMillis()
      val hash = getMd5(currentTs, MarvelConfig.key, MarvelConfig.secret)
      Future.sequence{ comicIdList.map {comicId =>
        val fetchedComic = getComicById(comicId, currentTs, hash).flatMap(s => s)
        //put into cache if does not exist
        cache(comicId)(fetchedComic)
        fetchedComic
      }
      }
    }

    override def getComicById(comicId: Int, currentTs: Long, hash: String): Future[Info] = {

      def getComicFromHttpRequest(comicId: Int, currentTs: Long, hash: String): Future[Option[ComicDataInfo]] = {
        val comicInfo = fetchComic(comicId, currentTs, hash).map { response =>
          val result = response match {
            case rep: ComicDataInfo =>
              Some(rep)
            case rep: MarvelErrorNotFound =>
              logger.error(s"Comic Not Found - ${rep.message}")
              None
            case rep: MarvelErrorTooManyRequests =>
              logger.error(s"Too many requests - ${rep.message}")
              None
            case rep: MarvelErrorForbidden =>
              logger.error(s"Access Forbidden - ${rep.message}")
              None
            case rep: MarvelGenericError =>
              logger.error(s"Other Errors - ${rep.message}")
              None
          }
          result
        }
        comicInfo
      }

      //Get from cache if exists
      val comic = cache.get(comicId)
      comic.isDefined match {
        case true => comic.get
        case false => Future(getComicFromHttpRequest(comicId, currentTs, hash))
      }
    }

    private def getMd5(ts: Long, key: String, secret: String) = {
      val md: MessageDigest = MessageDigest.getInstance("MD5")
      val strToDigest = ts.toString + secret + key
      md.digest(strToDigest.getBytes).foldLeft("")((acc, v) => acc + "%02x".format(v))
    }
  }

}

