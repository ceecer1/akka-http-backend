
This backend Scala application is based on Akka-Http

The main class is io.kodeasync.server.boot.Server

REST End Point exposed: http://localhost:8080/comics
This endpoint feeds an array of comic id.
Clients can do a POST request to the above end point with sample data [57245, 57163, 62605, 61308, 60293] with header
'Content-Type' 'application/json'

The response will be a list of Comic detail objects.

The fetched comic details is stored in in-memory cache. The comic detail is first fetched from cache and
if the comic detail is unavailable in the cache, the MarvelHttpClient does the http request to the Marvel API server.

In-memory cache implemented here is a SimpleLRU Cache copied from :
https://github.com/spray/spray/tree/master/spray-caching/src/main/scala/spray/caching/Cache.scala

This SimpleLRU cache declared with fixed size e.g. 10 will remove the cached value on Last Recently Used manner.

io.kodeasync.server.rest.client.MarvelHttpClient takes care of the comic endpoint


On key press, the server will gracefully shutdown.

This repository lacks unit tests.