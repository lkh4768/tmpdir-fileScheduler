package xyz.swwarehouse.tmpdir;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import xyz.swwarehouse.tmpdir.entity.FileInfo;
import xyz.swwarehouse.tmpdir.repository.FileInfoRepository;
import xyz.swwarehouse.tmpdir.service.FileSchedulerService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class FileSchedulerServiceTests {
	private static int FILE_CNT = 2;
	private static int EXPIRED_FILE_CNT = 3;
	private static FileInfo[] FILE_INFOS = new FileInfo[FILE_CNT];
	private static FileInfo[] EXPIRED_FILE_INFOS = new FileInfo[EXPIRED_FILE_CNT];;
	private static FileInfoUtil fileInfoUtil;

	@Value("${tmpdir.file.expire-term-day}")
	private int expireTermDay;
	@Value("${tmpdir.file.root-path}")
	private Path rootPath;

	@Autowired
	private FileSchedulerService fileSchedulerService;
	@Autowired
	private FileInfoRepository fileInfoRepo;

	@Before
	public void setUp() throws Exception {
		fileInfoRepo.deleteAll();
		fileInfoUtil = new FileInfoUtil(fileInfoRepo, rootPath, expireTermDay);
		FILE_INFOS = fileInfoUtil.createAndSaveFileInfos(FILE_CNT);
		EXPIRED_FILE_INFOS = fileInfoUtil.createAndSaveExpiredFileInfo(EXPIRED_FILE_CNT);
	}

	@Test
	public void testGetExpiredFileInfo() {
		List<FileInfo> expiredFileInfos = fileSchedulerService.getExpiredFileInfo();
		assertEquals(EXPIRED_FILE_CNT, expiredFileInfos.size());
		for (int i = 0; i < EXPIRED_FILE_CNT; i++) {
			boolean isEquals = true;
			for (int j = 0; j < expiredFileInfos.size(); j++) {
				if (EXPIRED_FILE_INFOS[i].equals(expiredFileInfos.get(j))) {
					isEquals = true;
					break;
				}
			}
			assertTrue(isEquals);
		}
	}
	
	@Test
	public void testDeleteFileInfos() {
		fileSchedulerService.deleteFileInfos(new ArrayList<FileInfo>(Arrays.asList(FILE_INFOS)));
		for(int i = 0; i < FILE_CNT; i++)
			assertNull(fileInfoRepo.findOne(FILE_INFOS[i].getId()));
	}
	
	@Test
	public void testDeleteFiles() {
		fileSchedulerService.deleteFiles(new ArrayList<FileInfo>(Arrays.asList(FILE_INFOS)));
		for(int i = 0; i < FILE_CNT; i++) {
			assertFalse(fileInfoUtil.isExistsFile(FILE_INFOS[i]));
		}
	}
}
