package me.rotemfo.linkchecker

import java.util.concurrent.Executor

import org.asynchttpclient.DefaultAsyncHttpClient

import scala.concurrent.{Future, Promise}

/**
  * project: scala-playground
  * package: me.rotemfo.linkchecker
  * file:    WebClient
  * created: 2019-03-04
  * author:  rotem
  */
object WebClient {
  val client = new DefaultAsyncHttpClient

  def get(url: String)(implicit executor: Executor): Future[String] = {
    try {
      val f = client.prepareGet(url).execute()
      val p = Promise[String]()
      f.addListener(() => {
        val response = f.get()
        val statusCode = response.getStatusCode
        if (statusCode < 400)
          p.success(response.getResponseBody)
        else
          p.failure(BadStatus(statusCode))
      }, executor)
      p.future
    } catch {
      case e: IllegalArgumentException => Future.failed(e)
    }
  }

  def shutdown(): Unit = {
    client.close()
  }
}