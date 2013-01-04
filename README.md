Overview
========

A micro http server usefull for dev ONLY :

* serve static file
* notify change to client via LiveReload protocol (over socket)

Usages
======

The default port is 35729 (like define in the LiveReload protocol).
If you change the port then you could not use the Chrome Extension for LiveReload, but it should works with manual import of livereload.js in your pages

Cli
---

Download the [livereload-jvm-0.1.0-onejar.jar]() (or regular jar  + all dependencies from maven central).

    java -jar livereload-jvm-0.1.0-onejar.jar web/root/path [port]

Java integration
----------------

    import net_alchim31_livereload
    
    LRServer server = new LRServer(port, docroot);
    server.run();

If you provide a plugin for your builder (maven, ant, sbt, gradle, plob, ...), let me know.

Links
=====

* [LiveReload Protocol](http://feedback.livereload.com/knowledgebase/articles/86174-livereload-protocol)
* [livereload-js](https://github.com/livereload/livereload-js) the client side
* LiveReload [Chrome extension](https://chrome.google.com/webstore/detail/livereload/jnihajbhpnppcggbcgedagnkighmdlei)

Alternatives
============

* [LiveReload 2/3](http://livereload.com/) the main tool (Mac & Windows only) include GUI
* [guard-livereload](https://github.com/guard/guard-livereload) a LiveReload server-side for Guard (Ruby)
* [grunt-reload](https://github.com/webxl/grunt-reload) a LiveReload server-side for Grunt (javascript/nodejs)
* [LivePage](https://chrome.google.com/webstore/detail/livepage/pilnojpmdoofaelbinaeodfpjheijkbh) an other way to "auto-reload"
* without LiveReload : `cd web/root/path && python -m http.server 8000` (python)