package org.iplatform.example.service.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.iplatform.example.util.domain.Attach;
import org.iplatform.example.util.domain.AttachQuery;

@Mapper
public interface AttachMapper {

	@Select("<script>select * from attach</script>")
	List<Attach> attachList(AttachQuery query) throws SQLException;

	@Select("<script>select * from attach where fileid = #{fileId, jdbcType = VARCHAR}</script>")
	Attach attach(@Param("fileId") String fileId);

	@Insert("insert into attach (fileid, filename, filesize, uploadman) "
			+ "values (#{fileId, jdbcType = VARCHAR}, #{fileName, jdbcType = VARCHAR}, #{fileSize, jdbcType = NUMERIC},  #{uploadMan, jdbcType = VARCHAR})")
	void insert(Attach attach);

	@Update("<script>update attach <set> " + "filename=#{fileName, jdbcType = VARCHAR}, "
			+ "filesize=#{filesize, jdbcType = NUMERIC}, " + "uploadman=#{uploadMan, jdbcType = NUMERIC} " + "</set> "
			+ "where fileId=#{fileId, jdbcType = VARCHAR}</script>")
	void update(Attach attach);

	@Delete("<script>delete from attach where fileid = #{fileId, jdbcType = VARCHAR}</script>")
	void delete(@Param("fileId") String fileId);

}
