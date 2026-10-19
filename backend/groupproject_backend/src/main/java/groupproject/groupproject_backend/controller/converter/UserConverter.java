package groupproject.groupproject_backend.controller.converter;

import groupproject.groupproject_backend.controller.dto.user.GetUserResponse;
import groupproject.groupproject_backend.domain.User;

public class UserConverter {
    public static GetUserResponse toResponse(User user){
        return GetUserResponse.builder()
                .id(user.getId())
                .virtualMoney(user.getVirtualMoney())
                .username(user.getUsername())
                .build();
    }
}
