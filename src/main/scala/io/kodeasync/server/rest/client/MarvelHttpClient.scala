package io.kodeasync.server.rest.client

import java.security.MessageDigest

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.model.StatusCodes._
import akka.stream.scaladsl.{Sink, Source}
import io.kodeasync.server.boot.Config.MarvelConfig
import io.kodeasync.server.rest.client.MarvelHttpClient.MarvelResponse
import io.kodeasync.server.util.{AkkaServiceProvider, DefaultAkkaServiceProvider, JsonSerialization, Loggable}
import org.joda.time.DateTime

import scala.concurrent.duration._
import akka.util.ByteString
import io.kodeasync.server.util.{AkkaServiceProvider, JsonSerialization, Loggable}

import scala.concurrent.Future

/**
  * Created by shishir on 2/4/17.
  */
trait MarvelHttpClient extends JsonSerialization with Loggable {

  this: AkkaServiceProvider =>

  import MarvelHttpClient._

  def fetchComic(comicId: Int, currentTs: Long, hash: String): Future[MarvelResponse] = {
    logger.info("Reached inside fetch comic ")
    val apiUrl = s"https://gateway.marvel.com:443/v1/public/comics/$comicId?" +
      s"ts=$currentTs&apikey=2f5b29ee88fae05cd1a863ce1cd595ae&hash=$hash"
    logger.info(s"Request URL : $apiUrl")
    val httpRequest = HttpRequest(uri = s"https://gateway.marvel.com:443/v1/public/comics/$comicId?" +
      s"ts=$currentTs&apikey=2f5b29ee88fae05cd1a863ce1cd595ae&hash=$hash")
    val httpResponse: Future[HttpResponse] = Http().singleRequest(httpRequest)
    logger.info("Executed http request ")

    httpResponse flatMap { response =>
      // toStrict to enforce all data be loaded into memory from the connection
      val strictEntity = response.entity.toStrict(5.seconds)
      strictEntity flatMap { e =>
        val responseStr = e.dataBytes
          .runFold(ByteString.empty) { case (acc, b) => acc ++ b }
        response.status match {
          case OK => responseStr map(s => serialization.read[ComicDataInfo](s.utf8String))
          case Forbidden => responseStr.map(s => MarvelErrorForbidden(s.utf8String))
          case TooManyRequests => responseStr.map(s => MarvelErrorTooManyRequests(s.utf8String))
          case NotFound => responseStr.map(s => MarvelErrorNotFound(s.utf8String))
          case _ => responseStr.map(s => MarvelGenericError(s.utf8String))
        }
      }
    }
  }

}

object MarvelHttpClient {

  //Marvel response from Comic lookup
  trait MarvelResponse

  // Generic response used to share model for error conditions
  trait MarvelError extends MarvelResponse

  //Valid ComicDetail response data model from Marvel API
  case class ComicDataInfo(code: Option[Int],
                           status: Option[String],
                           copyright: Option[String],
                           attributionText: Option[String],
                           attributionHTML: Option[String],
                           etag: Option[String],
                           data: Option[Data]) extends MarvelResponse

  case class Data(offset: Option[Int],
                  limit: Option[Int],
                  total: Option[Int],
                  count: Option[Int],
                  results: Option[Seq[Comic]])

  case class Comic(id: Option[Int],
                   digitalId: Option[Int],
                   title: Option[String],
                   issueNumber: Option[Double],
                   variantDescription: Option[String],
                   description: Option[String],
                   modified: Option[DateTime],
                   isbn: Option[String],
                   upc: Option[String],
                   diamondCode: Option[String],
                   ean: Option[String],
                   issn: Option[String],
                   format: Option[String],
                   pageCount: Option[Int],
                   textObjects: Option[Seq[TextObject]],
                   resourceURI: Option[String],
                   urls: Option[Seq[Url]],
                   series: Option[Summary],
                   variants: Option[Seq[Summary]],
                   collections: Option[Seq[Summary]],
                   collectedIssues: Option[Seq[Summary]],
                   dates: Option[Seq[ComicDate]],
                   prices: Option[Seq[ComicPrice]],
                   thumbnail: Option[Image],
                   images: Option[Seq[Image]],
                   creators: Option[CreatorList],
                   characters: Option[CharacterList],
                   stories: Option[StoryList],
                   events: Option[EventList])

  case class TextObject(`type`: Option[String],
                        language: Option[String],
                        text: Option[String])

  case class Url(`type`: Option[String],
                 url: Option[String])

  /**
    * Summary represents SeriesSummary, ComicSummary and EventSummary
    * @param resourceURI
    * @param name
    */
  case class Summary(resourceURI: Option[String],
                     name: Option[String])

  case class ComicDate(`type`: Option[String],
                       date: Option[DateTime])

  case class ComicPrice(`type`: Option[String],
                        price: Option[Float])

  case class Image(path: Option[String],
                   extension: Option[String])

  case class CreatorList(available: Option[Int],
                         returned: Option[Int],
                         collectionURI: Option[String],
                         items: Option[Seq[CreatorSummary]])

  case class CreatorSummary(resourceURI: Option[String],
                            name: Option[String],
                            role: Option[String])

  case class CharacterList(available: Option[Int],
                           returned: Option[Int],
                           collectionURI: Option[String],
                           items: Option[Seq[CharacterSummary]])

  case class CharacterSummary(resourceURI: Option[String],
                              name: Option[String],
                              role: Option[String])

  case class StoryList(available: Option[Int],
                       returned: Option[Int],
                       collectionURI: Option[String],
                       items: Option[Seq[StorySummary]])

  case class StorySummary(resourceURI: Option[String],
                          name: Option[String],
                          `type`: Option[String])

  case class EventList(available: Option[Int],
                       returned: Option[Int],
                       collectionURI: Option[String],
                       items: Option[Seq[Summary]])


  //Error details if provided in Marvel response
  case class Error(code: Int,
                   message: String) extends MarvelError

  //Exceptional API call conditions
  case class MarvelGenericError(message: String) extends MarvelError
  case class MarvelErrorNotFound(message: String) extends MarvelError
  case class MarvelErrorTooManyRequests(message: String) extends MarvelError
  case class MarvelErrorForbidden(message: String) extends MarvelError


  //If wrong id
  /*
  Response Body
  {
  "code": 404,
  "status": "We couldn't find that comic_issue"
  }

  Response Code
  404


  //if unacceptable characters instead of comic id

  Response Body
  no content
  Response Code
  0
   */

}
