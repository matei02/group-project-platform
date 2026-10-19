package groupproject.groupproject_backend.service.impl;

import groupproject.groupproject_backend.domain.User;
import groupproject.groupproject_backend.repository.UserRepository;
import groupproject.groupproject_backend.repository.converter.UserConverter;
import groupproject.groupproject_backend.repository.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    public void testIncreaseVirtualMoney_Success() {
        // Arrange
        Long userId = 1L;
        int initialAmount = 200;
        int increaseAmount = 100;
        int expectedFinalAmount = 300;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("TestUser");
        userEntity.setVirtualMoney(initialAmount);

        User user = new User();
        user.setId(userId);
        user.setUsername("TestUser");
        user.setVirtualMoney(initialAmount);

        when(UserConverter.toDomain(userEntity)).thenReturn(user);


        // Mock repository to return real entity
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        // Act
        Boolean result = userService.increaseVirtualMoney(userId, increaseAmount);

        // Assert
        assertTrue(result);
        assertEquals(expectedFinalAmount, user.getVirtualMoney());
    }



    @Test
    public void testIncreaseVirtualMoney_UserNotFound() {
        // Arrange
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Boolean result = userService.increaseVirtualMoney(userId, 50);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void testDecreaseVirtualMoney_Success() {
        // Arrange
        Long userId = 1L;
        int decreaseAmount = 50;
        User user = new User();
        user.setId(userId);
        user.setUsername("TestUser");
        user.setVirtualMoney(200);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        mockStatic(UserConverter.class);
        when(UserConverter.toDomain(userEntity)).thenReturn(user);
        when(UserConverter.toEntity(user)).thenReturn(userEntity);

        // Act
        Boolean result = userService.decreaseVirtualMoney(userId, decreaseAmount);

        // Assert
        assertTrue(result);
        assertEquals(150, user.getVirtualMoney());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void testDecreaseVirtualMoney_UserNotFound() {
        // Arrange
        Long userId = 3L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Boolean result = userService.decreaseVirtualMoney(userId, 20);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}