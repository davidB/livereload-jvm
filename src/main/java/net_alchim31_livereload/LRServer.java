package net_alchim31_livereload;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class LRServer {
	private final int _port;
	private final Path _docroot;
	private Server _server;
	private Watcher _watcher;
	private static String[] _exclusions;
	private static final Logger LOG = Logger.getLogger(LRServer.class.getName());

	public LRServer(int port, Path docroot) {
		this._port = port;
		this._docroot = docroot;
	}

	public LRServer(Path docroot) {
		this._port = 35729;
		this._docroot = docroot;
	}

	private void init() throws Exception {

		ResourceHandler rHandler = new ResourceHandler() {
			@Override
			public Resource getResource(String path) {
				if ("/livereload.js".equals(path)) {
					return Resource.newResource(LRServer.class.getResource(path));
				}
				return super.getResource(path);
			}
		};
		rHandler.setDirectoriesListed(true);
		rHandler.setWelcomeFiles(new String[] { "index.html" });
		rHandler.setResourceBase(_docroot.toString());

		LRWebSocketHandler wsHandler = new LRWebSocketHandler();
		wsHandler.setHandler(rHandler);

		_server = new Server(_port);
		_server.setHandler(wsHandler);

		initWatcher();
		_watcher.listener = wsHandler;

	}

	private void initWatcher() throws Exception {
		_watcher = new Watcher(_docroot);
		if (_exclusions != null && _exclusions.length > 0) {
			List<Pattern> patterns = new ArrayList<Pattern>();
			for (String exclusion : _exclusions) {
				patterns.add(Pattern.compile(exclusion));
			}
			_watcher.set_patterns(patterns);
		}
	}

	public static void setExclusions(String[] exclusions) {
		LRServer._exclusions = exclusions;
	}

	public static String[] getExclusions() {
		return _exclusions;
	}

	public void start() throws Exception {
		this.init();
		_server.start();
		_watcher.start();
	}

	public void run() {
		try {
			start();
			join();
		} catch (Throwable t) {
			LOG.log(Level.SEVERE, t.getMessage(), t);
		} finally {
			try {
				stop();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	public static class StockServiceSocketServlet extends WebSocketServlet {
		@Override
		public void configure(WebSocketServletFactory factory) {
			factory.register(LRWebSocket.class);
		}
	}

	public void join() throws Exception {
		_server.join();
	}

	public void stop() {
		try {
			_watcher.stop();
			if (_server != null)
				_server.stop();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
