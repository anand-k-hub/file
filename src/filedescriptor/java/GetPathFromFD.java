//$Id$
package filedescriptor.java;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetPathFromFD {
	private native int getFDid(FileDescriptor fd);
	public static void main(String[] args) throws Exception {
		FileOutputStream fout = new FileOutputStream(args[0]); // arg[0] - the file to test
		PathGetterFromFD pathGetter = new PathGetterFromFD(fout.getFD());
		System.out.println(pathGetter.getPathUsingReflection());
		fout.close();
	}

}

class PathGetterFromFD {
	/** constant variables **/
	private static final Logger LOGGER = Logger.getLogger(PathGetterFromFD.class.getName());
	
	private static final String FILE_DESCRIPTOR_ID = "fd";
	private static final String FILE_DESCRIPTOR_FOLDER_IN_LINUX = "/proc/self/fd/";

	/** working variables **/
	private final FileDescriptor fd;

	public PathGetterFromFD(final FileDescriptor fd) {
		this.fd = fd;
	}

	public Path getPathUsingReflection() {
		try {
			Field fdField = FileDescriptor.class.getDeclaredField("fd");
			fdField.setAccessible(true);
			int fd_id = fdField.getInt(fd);
			Path path = Paths.get(FILE_DESCRIPTOR_FOLDER_IN_LINUX + fd_id);
			return Files.readSymbolicLink(path);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Excetion while getting file path from file descriptor.", e);
		}
		return null;
	}

}
