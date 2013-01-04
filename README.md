Overview
========

A micro http server usefull for dev ONLY :

* serve static file
* notify change to client via LiveReload protocol (over socket)

Usages
======

The default port is 35729 (like define in the LiveReload protocol).
If you change the port then you could not use the [Browsers Extension](http://feedback.livereload.com/knowledgebase/articles/86242-how-do-i-install-and-use-the-browser-extensions-) for LiveReload, but it should works if you insert a [JavaScript snippet](http://go.livereload.com/mobile) in your pages.

Cli
---

Download the [livereload-jvm-0.1.0-onejar.jar](http://repo2.maven.org/maven2/net/alchim31/livereload-jvm/0.1.0/livereload-jvm-0.1.0-onejar.jar) (or regular jar  + all dependencies from maven central).

    java -jar livereload-jvm-0.1.0-onejar.jar web/root/path [port]

Java integration
----------------

    //#repo central m2:http://repo1.maven.org/maven2/
    
    import java.nio.file.FileSystems;
    import net_alchim31_livereload.LRServer; //#from net.alchim31:livereload-jvm:0.1.0
    
    int port = 35729;
    Path docroot = FileSystems.getDefault().getPath("web/root/path");
    new LRServer(port, docroot).run(); // == start() + join()

If you provide a plugin for your builder (maven, ant, sbt, gradle, plob, ...), let me know.

Links
=====

* [Browsers Extension](http://feedback.livereload.com/knowledgebase/articles/86242-how-do-i-install-and-use-the-browser-extensions-)
* [livereload-js](https://github.com/livereload/livereload-js) the client side
* [LiveReload Protocol](http://feedback.livereload.com/knowledgebase/articles/86174-livereload-protocol)

Alternatives
============

* [LiveReload 2/3](http://livereload.com/) the main tool (Mac & Windows only) include GUI
* [guard-livereload](https://github.com/guard/guard-livereload) a LiveReload server-side for Guard (Ruby)
* [grunt-reload](https://github.com/webxl/grunt-reload) a LiveReload server-side for Grunt (javascript/nodejs)
* [LivePage](https://chrome.google.com/webstore/detail/livepage/pilnojpmdoofaelbinaeodfpjheijkbh) an other way to "auto-reload"
* without LiveReload : `cd web/root/path && python -m http.server 8000` (python)

License
=======

* the project is under [unlicense](http://unlicense.org/)
* the project (source and binaries) include [livereload.js], livereload.js is under MIT