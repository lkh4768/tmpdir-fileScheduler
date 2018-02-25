package xyz.swwarehouse.tmpdir.tasks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import xyz.swwarehouse.tmpdir.entity.FileInfo;
import xyz.swwarehouse.tmpdir.service.FileSchedulerService;

@Component
public class RemoveExpiredFileTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(RemoveExpiredFileTask.class);

	@Value("${tmpdir.scheduler.term}")
	public static final int term = 60000;

	private FileSchedulerService fileSchedulerService;

	@Autowired
	public RemoveExpiredFileTask(FileSchedulerService fileSchedulerService) {
		this.fileSchedulerService = fileSchedulerService;
	}

	@Scheduled(fixedDelay = term)
	public void removeExpiredFile() {
		List<FileInfo> expiredFileInfos = fileSchedulerService.getExpiredFileInfo();
		LOGGER.info("Delete expired file infos({}) in repo", expiredFileInfos);
		fileSchedulerService.deleteFileInfos(expiredFileInfos);
		LOGGER.info("Delete expired file infos({}) in storage", expiredFileInfos);
		fileSchedulerService.deleteFiles(expiredFileInfos);
	}
}
