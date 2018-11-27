package com.brightercode.discourse.util

import akka.actor.{ActorSystem, Terminated}
import akka.stream.ActorMaterializer
import play.api.libs.ws.StandaloneWSRequest
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.{ExecutionContext, Future}

/**
  * @see https://github.com/playframework/play-ws
  */
abstract class PlayWebServiceClient(urlBase: String,
                                    commonQueryParams: Map[String, String] = Map.empty) {

  private implicit val system: ActorSystem = ActorSystem()
  system.registerOnTermination {
    System.exit(0)
  }

  private implicit val executionContext: ExecutionContext = system.dispatcher

  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private val wsClient = StandaloneAhcWSClient()

  def url(path: String, extraQueryParams: Map[String, String] = Map.empty): StandaloneWSRequest#Self =
    wsClient.url(s"$urlBase/$path")
      .withQueryStringParameters((extraQueryParams ++ commonQueryParams).toSeq: _*)

  def shutdown(): Future[Terminated] = {
    wsClient.close()
    system.terminate()
  }
}
