package net_alchim31_livereload;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Main {

  public static void main(String[] args) throws Exception {
    try (FileSystem fs = FileSystems.getDefault()) {
      Path docroot = fs.getPath(".");
      int port = 35729;
      if (args.length > 0) {
        docroot = fs.getPath(args[0]);
      }
      if (args.length > 1) {
        port = Integer.parseInt(args[1], 10);
      }
      LRServer server = new LRServer(port, docroot);
      server.run();
    }
  }
}
