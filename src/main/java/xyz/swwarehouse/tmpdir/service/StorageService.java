package xyz.swwarehouse.tmpdir.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StorageService {
	private static final Logger LOGGER = LoggerFactory.getLogger(StorageService.class);
	
	private StorageService() {
	}

	public static void deleteDir(Path dir) {
		Stream<Path> streamPath = null;
		try {
			streamPath = Files.walk(dir);
			streamPath.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		} catch (IOException e) {
			LOGGER.error("Delete dir failed({})", e);
		} finally {
			if (streamPath != null)
				streamPath.close();
		}
	}
}
