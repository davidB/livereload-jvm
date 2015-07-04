package net_alchim31_livereload;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author dwayne
 * @see http://docs.oracle.com/javase/tutorial/essential/io/notification.html
 */
public class Watcher implements Runnable {
	private static final Logger LOG = Logger.getLogger(Watcher.class.getName());
	private final WatchService _watcher;
	private final Map<WatchKey, Path> _keys;
	private final Path _docroot;
	private final AtomicBoolean _running = new AtomicBoolean(false);

	public LRWebSocketHandler listener = null;
	private List<Pattern> _patterns;

	public Watcher(Path docroot) throws Exception {
		_docroot = docroot;
		this._watcher = docroot.getFileSystem().newWatchService();
		this._keys = new HashMap<WatchKey, Path>();

		// System.out.format("Scanning %s ...\n", _docroot);
		registerAll(_docroot);
	}

	private void notify(String path) throws Exception {
		if (_patterns != null) {
			for (Pattern p : _patterns) {
				LOG.finer("Testing pattern: " + p + " against string: " + path);
				if (p.matcher(path).matches()) {
					LOG.fine("Skipping file: " + path + " thanks to pattern: " + p);
					return;
				}
			}
		}
		LOG.fine("File " + path + " changed, triggering refresh");
		LRWebSocketHandler l = listener;
		if (l != null) {
			l.notifyChange(path);
		}
	}

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(_watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		_keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	void start() throws Exception {
		if (_running.compareAndSet(false, true)) {
			Thread t = new Thread(this);
			t.setDaemon(true);
			t.start();
		}
	}

	void stop() throws Exception {
		_running.set(false);
		_watcher.close();
	}

	/**
	 * Process all events for keys queued to the watcher
	 * 
	 * @throws Exception
	 */
	@Override
	public void run() {
		try {
			while (_running.get()) {

				// wait for key to be signalled
				WatchKey key = _watcher.take();

				Path dir = _keys.get(key);
				if (dir == null) {
					LOG.log(Level.SEVERE, String.format("WatchKey '%s'not recognized!!", key));
					continue;
				}

				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();

					// TBD - provide example of how OVERFLOW event is handled
					if (kind == OVERFLOW) {
						continue;
					}

					// Context for directory entry event is the file name of
					// entry
					WatchEvent<Path> ev = cast(event);
					Path name = ev.context();
					Path child = dir.resolve(name);

					// System.out.format("%s: %s ++ %s\n", event.kind().name(),
					// name, _docroot.relativize(child));
					if (kind == ENTRY_MODIFY) {
						notify(_docroot.relativize(child).toString());
					} else if (kind == ENTRY_CREATE) {
						// if directory is created, and watching recursively,
						// then
						// register it and its sub-directories
						if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
							registerAll(child);
						}
					}
				}

				// reset key and remove from set if directory no longer
				// accessible
				boolean valid = key.reset();
				if (!valid) {
					_keys.remove(key);

					// all directories are inaccessible
					if (_keys.isEmpty()) {
						break;
					}
				}
			}
		} catch (InterruptedException | ClosedWatchServiceException exc) {
			// stop
		} catch (Exception exc) {
			LOG.log(Level.SEVERE, exc.getMessage(), exc);
		} finally {
			_running.set(false);
		}
	}

	public void set_patterns(List<Pattern> _patterns) {
		this._patterns = _patterns;
	}

	public List<Pattern> get_patterns() {
		return _patterns;
	}
}
