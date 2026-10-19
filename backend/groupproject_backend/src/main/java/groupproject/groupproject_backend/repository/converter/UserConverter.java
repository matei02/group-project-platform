package groupproject.groupproject_backend.repository.converter;

import groupproject.groupproject_backend.domain.User;
import groupproject.groupproject_backend.repository.entity.UserEntity;

public class UserConverter {

    public static UserEntity toEntity(User user){
        return UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .virtualMoney(user.getVirtualMoney())
                .build();
    }

    public static User toDomain(UserEntity user){
        return User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .virtualMoney(user.getVirtualMoney())
                .build();
    }
}
