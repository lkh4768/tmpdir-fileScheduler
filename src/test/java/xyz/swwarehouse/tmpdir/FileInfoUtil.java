package xyz.swwarehouse.tmpdir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import xyz.swwarehouse.tmpdir.entity.FileInfo;
import xyz.swwarehouse.tmpdir.repository.FileInfoRepository;

public class FileInfoUtil {
	private int expireTermDay;
	private Path rootPath;
	private FileInfoRepository fileInfoRepo;

	public FileInfoUtil(FileInfoRepository fileInfoRepo, Path rootPath, int expireTermDay) {
		this.fileInfoRepo = fileInfoRepo;
		this.rootPath = rootPath;
		this.expireTermDay = expireTermDay;
	}

	public FileInfo createInvaildFileInfo() {
		return createFileInfo();
	}

	public FileInfo[] createAndSaveExpiredFileInfo(final int cnt) throws IOException {
		FileInfo[] fileInfos = new FileInfo[cnt];
		for (int i = 0; i < cnt; i++) {
			fileInfos[i] = createFileInfo(createExpiredSubmissionTime());
			saveFileInfo(fileInfos[i], i + 1);
		}
		return fileInfos;
	}

	public FileInfo[] createAndSaveFileInfos(final int cnt) throws IOException {
		FileInfo[] fileInfos = new FileInfo[cnt];
		for (int i = 0; i < cnt; i++) {
			fileInfos[i] = createFileInfo();
			saveFileInfo(fileInfos[i], i + 1);
		}
		return fileInfos;
	}

	private void saveFileInfo(final FileInfo fileInfo, final int fileCount) throws IOException {
		for (int i = 0; i < fileCount; i++)
			storeFile(fileInfo, i);
		fileInfoRepo.save(fileInfo);
	}

	private FileInfo createFileInfo() {
		FileInfo fileInfo = new FileInfo();
		fileInfo.setId(createId());
		fileInfo.setSubmissionTime(createSubmissionTime());
		fileInfo.setExpireTime(createExpireTime(fileInfo));
		return fileInfo;
	}

	private FileInfo createFileInfo(Date submissionTime) {
		FileInfo fileInfo = createFileInfo();
		fileInfo.setSubmissionTime(submissionTime);
		fileInfo.setExpireTime(createExpireTime(fileInfo));
		return fileInfo;
	}

	private String createId() {
		return UUID.randomUUID().toString();
	}

	private Date createSubmissionTime() {
		return new Date();
	}

	private Date createExpiredSubmissionTime() {
		return createAfterTime(new Date(), -(expireTermDay + 1));
	}

	private Date createExpireTime(FileInfo fileInfo) {
		return createAfterTime(fileInfo.getSubmissionTime(), expireTermDay);
	}

	private Date createAfterTime(Date startTime, int termDay) {
		Calendar c = Calendar.getInstance();
		c.setTime(startTime);
		c.add(Calendar.DATE, termDay);
		return c.getTime();
	}

	private void storeFile(FileInfo fileInfo, int idx) throws IOException {
		Path baseDirPath = createDir(fileInfo.getId());
		InputStream filecontent = new ByteArrayInputStream(fileInfo.getId().getBytes(StandardCharsets.UTF_8.name()));
		Path filePath = baseDirPath.resolve(fileInfo.getId() + idx);
		if (!filePath.toFile().exists()) {
			Files.copy(filecontent, filePath, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private Path createDir(final String id) throws IOException {
		Path storeFilePath = this.rootPath.resolve(id);
		if (!this.rootPath.toFile().exists())
			Files.createDirectory(this.rootPath);
		if (!storeFilePath.toFile().exists())
			Files.createDirectory(storeFilePath);
		return storeFilePath;
	}
	
	public boolean isExistsFile(final FileInfo fileInfo) {
		Path baseDir = this.rootPath.resolve(fileInfo.getId());
		return Files.exists(baseDir);
	}
}
