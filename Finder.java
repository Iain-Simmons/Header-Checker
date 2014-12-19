import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

class Finder extends SimpleFileVisitor<Path> {

	private final PathMatcher matcher;
	private int numMatches = 0;
	private List<Path> suspectFiles = new ArrayList<>();

	Finder(String pattern) {
		matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
	}

	void find(Path file) {
		Path name = file.getFileName();
		if (name != null && matcher.matches(name)) {
			if (!itLooksLikeFileHasAHeader(file)) {
				doStuff(file);
			}
			numMatches++;
			;
		}
	}

	private void doStuff(Path file) {
		suspectFiles.add(file);
	}

	private boolean itLooksLikeFileHasAHeader(Path file) {
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line;
			while ((line = reader.readLine()) != null) {
				boolean contains = line.trim().startsWith("/*");
				if (contains) {
					return true;
				}
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
		return false;
	}

	void done() {
		System.out.println("Scanned " + numMatches
				+ " files. The following files may not have headers:");
		for (Path filePath : suspectFiles) {
			System.out.println("  - " + filePath);
		}
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		find(file);
		return CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		find(dir);
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		System.err.println(exc);
		return CONTINUE;
	}
}