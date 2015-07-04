package net_alchim31_livereload;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class LRWebSocket {
	final LRProtocol _protocol = new LRProtocol();
	private Session session;
	private static final Logger LOG = Logger.getLogger(LRWebSocket.class.getName());

	// called when the socket connection with the browser is established
	@OnWebSocketConnect
	public void handleConnect(Session session) {
		this.session = session;
		LRWebSocketHandler.add(this);
	}

	// called when the connection closed
	@OnWebSocketClose
	public void handleClose(int statusCode, String reason) {
		LRWebSocketHandler.remove(this);
	}

	// called when a message received from the browser
	@OnWebSocketMessage
	public void handleMessage(String message) {
		try {
			if (_protocol.isHello(message)) {
				sendMessage(_protocol.hello());
			}
		} catch (Exception exc) {
			LOG.log(Level.SEVERE, exc.getMessage(), exc);
		}
	}

	public void notifyChange(String path) throws Exception {
		String msg = _protocol.reload(path);
		sendMessage(msg);
	}

	// sends message to browser
	private void sendMessage(String message) {
		try {
			if (session.isOpen()) {
				session.getRemote().sendString(message);
			}
		} catch (IOException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	// called in case of an error
	@OnWebSocketError
	public void handleError(Throwable error) {
		error.printStackTrace();
		LRWebSocketHandler.remove(this);
	}

}