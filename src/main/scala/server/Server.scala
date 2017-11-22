package server

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http
import com.twitter.util.{Await, Future}

object Server {
    private var cache = Map[String, Future[http.Response]]()

    def main(args: Array[String]): Unit = {
        val service = new Service[http.Request, http.Response] {
            def apply(req: http.Request): Future[http.Response] = {
                val proxy_response = sendRequest(req)

                proxy_response.onSuccess( resp => {
                    println(resp.statusCode, req.uri)
                })

                proxy_response
            }
        }

        val server = Http.serve(":8080", service)
        Await.ready(server)
    }

    def sendRequest(req: http.Request): Future[http.Response] = {
        val host = req.host.get
        val client = Http.newService(if (host.contains(":")) {
            host
        } else {
            host + ":80"
        })

        cache.get(req.uri) match {
            case Some(item) => {
                item
            }
            case None => {
                val request = http.Request(req.method, if (req.uri.replaceAll("https?://(.*)/$", "$1") == host) {
                    "/"
                } else {
                    req.uri
                })
                val header = request.headerMap
                req.headerMap.foreach { v =>
                    header.set(v._1, v._2)
                }
                //cache += (req.uri -> client(request))
                //cache(req.uri)
                client(request)
            }
        }
    }
}
