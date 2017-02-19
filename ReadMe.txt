################--RUNNING THE APPLICATION--#################
#akka-http #caching #spray-cache #http-proxy

The main class to run is io.kodeasync.server.boot.Server

Running from terminal:
sbt run

REST End Point exposed: http://localhost:8080/comics
This endpoint feeds an array of comic id.
Clients can do a POST request to the above end point with sample data [57245, 61308] with header
'Content-Type' 'application/json'

Sample Curl Command for terminal:
curl -H 'Content-Type: application/json' -X POST -d '[57245, 61308]' http://localhost:8080/comics

The response will be a list of Comic detail objects.

If any of the comics is not found for the id in the post array, then an error is logged in the server console as follows :
ERROR io.kodeasync.server.service.DefaultMarvelHttpComponent$DefaultMarvelHttpService - Comic Not Found - {"code":404,"status":"We couldn't find that comic_issue"}

################--IN-MEMORY CACHE--#################
The fetched comic details is stored in in-memory cache. The comic detail is first fetched from cache and
if the comic detail is unavailable in the cache, the MarvelHttpClient does the http request to the Marvel API server.

In-memory cache implemented here is a SimpleLRU Cache copied from :
https://github.com/spray/spray/tree/master/spray-caching/src/main/scala/spray/caching/Cache.scala

This SimpleLRU cache declared with fixed size e.g. 10 will remove the cached value on Last Recently Used manner.

The cache is defined in io.kodeasync.server.util.AkkaServiceProvider

################--MARVEL API REQUEST CLIENT--#################
io.kodeasync.server.rest.client.MarvelHttpClient takes care of the comic endpoint

################--SHUTDOWN--#################
On key press, the server will gracefully shutdown.

This repository lacks unit tests.
