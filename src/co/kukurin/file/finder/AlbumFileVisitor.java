package co.kukurin.file.finder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import co.kukurin.xml.items.Playlist;

public class AlbumFileVisitor implements FileVisitor<Path> {

	private Set<String> pathsToInclude;
	private Playlist createdPlaylist;
	
	public AlbumFileVisitor(String defaultSeparatedPaths, String playlistTitle) {
		convertToList(defaultSeparatedPaths);
		this.createdPlaylist = new Playlist(playlistTitle);
	}
	
	private void convertToList(String defaultSeparatedPaths) {
		String pathSep = File.pathSeparator;
		String[] tokens = defaultSeparatedPaths.split(pathSep);
		pathsToInclude = new HashSet<String>(Arrays.asList(tokens));
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		// TODO check extension
		// TODO check multiple levels of parents
		
		if(file.getParent() != null
				&& pathsToInclude.contains(file.getParent().toString())) {
			createdPlaylist.add(file.toString());
		}
		
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		throw new IOException("Visit failed!: " + file);
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
	
	public Playlist getCreatedPlaylist() {
		return createdPlaylist;
	}
}
