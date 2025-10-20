package glting.server.users.repository;

import glting.server.users.entity.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    List<UserEntity> findAll();

    UserEntity findById(@Param("userSeq") Long userSeq);

    void insertUser(UserEntity user);
}
