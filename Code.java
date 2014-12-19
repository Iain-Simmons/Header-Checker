/**
 * Sample code that finds files that match the specified glob pattern.
 * For more information on what constitutes a glob pattern, see
 * http://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
 *
 * The file or directories that match the pattern are printed to
 * standard out.  The number of matches is also printed.
 *
 * When executing this application, you must put the glob pattern
 * in quotes, so the shell will not expand any wild cards:
 *              java Find . -name "*.java"
 */

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import static java.nio.file.FileVisitOption.*;
import java.util.*;


public class Code {

	public static void main(String[] args)
			throws IOException {
		
		Path startingDir = Paths.get("src");
		String pattern = "{*.scala,*.java}";
		
		System.out.println("Scanning directory "+startingDir);
		
		Finder finder = new Finder(pattern);
		Files.walkFileTree(startingDir, finder);
		finder.done();
	}
}