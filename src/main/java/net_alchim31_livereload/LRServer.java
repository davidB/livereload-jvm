package net_alchim31_livereload;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.resource.Resource;

public class LRServer {
  private Server  _server;
  private Watcher _watcher;

  public LRServer(int port, Path docroot) throws Exception {
    SelectChannelConnector connector = new SelectChannelConnector();
    connector.setPort(port);

    ResourceHandler rHandler = new ResourceHandler() {
      @Override
      public Resource getResource(String path) throws MalformedURLException {
        if ("/livereload.js".equals(path)) {
          try {
            return Resource.newResource(LRServer.class.getResource(path));
          } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
          }
        }
        return super.getResource(path);
      }
    };
    rHandler.setDirectoriesListed(true);
    rHandler.setWelcomeFiles(new String[] { "index.html" });
    rHandler.setResourceBase(docroot.toString());

    LRWebSocketHandler wsHandler = new LRWebSocketHandler();
    wsHandler.setHandler(rHandler);

    _server = new Server();
    _server.setHandler(wsHandler);
    _server.addConnector(connector);

    _watcher = new Watcher(docroot);
    _watcher.listener = wsHandler;
  }

  // public void start() throws Exception {
  // _server.start();
  // }

  public void run() throws Exception {
    // start();
    _server.start();
    _watcher.run();
    _server.join();
  }

  // public void stop() throws Exception {
  // _server.stop();
  // }
}
