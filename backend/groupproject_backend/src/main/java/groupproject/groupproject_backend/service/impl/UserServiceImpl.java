package groupproject.groupproject_backend.service.impl;

import groupproject.groupproject_backend.domain.User;
import groupproject.groupproject_backend.repository.UserRepository;
import groupproject.groupproject_backend.repository.converter.UserConverter;
import groupproject.groupproject_backend.service.UserService;
import groupproject.groupproject_backend.service.exception.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public void setVirtualMoney(Long userId, int amount) {
        User user = UserConverter.toDomain(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));


        user.setVirtualMoney(amount);

        userRepository.save(UserConverter.toEntity(user));
    }


    public User findUserById(Long id) {
        return UserConverter.toDomain(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id)));
    }

    public Boolean increaseVirtualMoney(Long userId, int amount) {
        try{
            User user = UserConverter.toDomain(userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found")));

            user.setVirtualMoney(user.getVirtualMoney() + amount);

            userRepository.save(UserConverter.toEntity(user));
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public Boolean decreaseVirtualMoney(Long userId, int amount) {
        try{
            User user = UserConverter.toDomain(userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found")));

            if (user.getVirtualMoney() < amount) {
                throw new InsufficientBalanceException("Not enough virtual money");
            }

            user.setVirtualMoney(user.getVirtualMoney() - amount);

            userRepository.save(UserConverter.toEntity(user));
            return true;
        }catch (Exception e) {
            return false;
        }
    }
}
