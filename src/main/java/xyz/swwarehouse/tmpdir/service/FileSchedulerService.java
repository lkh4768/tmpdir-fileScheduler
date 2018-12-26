package xyz.swwarehouse.tmpdir.service;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import xyz.swwarehouse.tmpdir.entity.FileInfo;
import xyz.swwarehouse.tmpdir.repository.FileInfoRepository;

@Service
public class FileSchedulerService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileSchedulerService.class);

	private final FileInfoRepository fileRepository;

	@Value("${tmpdir.file.root-path}")
	private Path rootPath;

	@Autowired
	public FileSchedulerService(FileInfoRepository fileInfoRepository) {
		this.fileRepository = fileInfoRepository;
	}

	public List<FileInfo> getExpiredFileInfo() {
		return fileRepository.findByExpireTimeLessThan(new Date());
	}

	public void deleteFileInfos(List<FileInfo> fileInfos) {
		for(FileInfo fileInfo: fileInfos) {
			fileRepository.deleteById(fileInfo.getId());	
		}
	}

	public void deleteFiles(List<FileInfo> fileInfos) {
		int size = fileInfos.size();
		for (int i = 0; i < size; i++) {
			Path baseDir = this.rootPath.resolve(fileInfos.get(i).getId());
			StorageService.deleteDir(baseDir);
			LOGGER.info("Deleted expired file({}) in storage", baseDir.toAbsolutePath());
		}
	}
}
