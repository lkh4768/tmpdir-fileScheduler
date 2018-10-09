package xyz.swwarehouse.tmpdir.repository;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.mongodb.repository.MongoRepository;

import xyz.swwarehouse.tmpdir.entity.FileInfo;

public interface FileInfoRepository extends MongoRepository<FileInfo, String> {
	ArrayList<FileInfo> findByExpireTimeLessThan(Date time);
	long deleteById(String id);
}
