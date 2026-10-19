package groupproject.groupproject_backend.service;

import groupproject.groupproject_backend.domain.User;

public interface UserService {
    void setVirtualMoney(Long userId, int amount);
    User findUserById(Long id);
    Boolean increaseVirtualMoney(Long userId, int amount);
    Boolean decreaseVirtualMoney(Long userId, int amount);
}
