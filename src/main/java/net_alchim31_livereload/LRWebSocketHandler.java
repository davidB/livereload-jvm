package net_alchim31_livereload;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

class LRWebSocketHandler extends WebSocketHandler {
	private static final Logger LOG = Logger.getLogger(WebSocketHandler.class.getName());
	static final ConcurrentLinkedQueue<LRWebSocket> _sockets = new ConcurrentLinkedQueue<LRWebSocket>();
	final LRProtocol _protocol = new LRProtocol();

	// FIXME: review this code. Has it been ported properly?

	// @Override
	// public WebSocket doWebSocketConnect(HttpServletRequest request,
	// String protocol) {
	// if ("/livereload".equals(request.getPathInfo())) {
	// return new LRWebSocket();
	// }
	// return new WebSocket() {
	// @Override
	// public void onOpen(Connection connection) {
	// connection.close();
	// }
	//
	// @Override
	// public void onClose(int code, String msg) {
	// }
	// };
	// }

	public void notifyChange(String path) throws Exception {
		for (LRWebSocket socket : _sockets) {
			try {
				socket.notifyChange(path);
			} catch (IOException e) {
				_sockets.remove(socket);
				LOG.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	// FIXME: review this code. Has it been ported properly?

	// class LRWebSocket implements WebSocket.OnTextMessage {
	// protected Connection _connection;
	//
	// @Override
	// public void onOpen(Connection connection) {
	// _connection = connection;
	// _broadcast.add(this);
	// }
	//
	// @Override
	// public void onClose(int code, String message) {
	// _broadcast.remove(this);
	// }
	//
	// @Override
	// public void onMessage(final String data) {
	// try {
	// if (_protocol.isHello(data)) {
	// _connection.sendMessage(_protocol.hello());
	// }
	// } catch (Exception exc) {
	// exc.printStackTrace();
	// }
	// }
	// }

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(LRWebSocket.class);
	}

	public static void add(LRWebSocket socket) {
		_sockets.add(socket);
	}

	public static void remove(LRWebSocket socket) {
		_sockets.remove(socket);
	}
}
