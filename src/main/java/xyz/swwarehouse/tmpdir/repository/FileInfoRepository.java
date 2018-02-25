package xyz.swwarehouse.tmpdir.repository;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import xyz.swwarehouse.tmpdir.entity.FileInfo;

public interface FileInfoRepository extends CrudRepository<FileInfo, String> {
	ArrayList<FileInfo> findByExpireTimeLessThan(long time);
}
