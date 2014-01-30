package net_alchim31_livereload;


import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Main {

  public static void main(String[] args) throws Exception {

    try (FileSystem fs = FileSystems.getDefault()) {

      String path = ".";
      String port = "35729";
      String[] exclusions = null;

      for (int i = 0; i < args.length; i++) {
        if (hasOption("-h", args, i, false)) {
          printHelp();
          return;
        }
        if (hasOption("-d", args, i, true)) {
          path = getOption(args, i);
        }
        if (hasOption("-p", args, i, true)) {
          port = getOption(args, i);
        }
        if (hasOption("-e", args, i, true)) {
          String exclusionStr = getOption(args, i);
          if (exclusionStr != null) {
            exclusions = exclusionStr.split(",");
          }
        }

      }

      System.out.println("Using path: " + path);
      System.out.println("Using port: " + port);
      System.out.println("Exclude files matching: " + exclusions);

      Path docroot = fs.getPath(path);

      LRServer server = new LRServer(Integer.parseInt(port), docroot);
      LRServer.setExclusions(exclusions);
      server.run();
    }
  }

  private static void printHelp() {
    System.out.println();
    System.out.println("Usage: " + Main.class.getName());
    System.out.println();
    System.out.println("-h\tPrints this help message");
    System.out.println("-d\tSpecify the top level directory to watch for changes");
    System.out.println("-p\tSpecify an alternate port from the default Live Reload port");
    System.out.println("-e\tA comma separated list of Java regex patterns to exclude from triggering a refresh");
    System.out.println();
  }


  private static boolean hasOption(String flag, String[] args, int i, boolean hasArgument) {
    if (i < args.length && args[i].equals(flag)) {
      if (!hasArgument) {
        return true;
      } else if (i + 1 < args.length) {
        return true;
      }
    }
    return false;
  }

  private static String getOption(String[] args, int i) {
    if (i + 1 < args.length) {
      return args[i + 1];
    }
    return null;
  }
}
