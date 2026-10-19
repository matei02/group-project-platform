package groupproject.groupproject_backend.controller;

import groupproject.groupproject_backend.controller.converter.UserConverter;
import groupproject.groupproject_backend.controller.dto.user.GetUserResponse;
import groupproject.groupproject_backend.domain.User;
import groupproject.groupproject_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;


    @PutMapping("/{userId}/virtual-money")
    public ResponseEntity<Void> setVirtualMoney(@PathVariable Long userId, @RequestParam int amount) {
        try {
            userService.setVirtualMoney(userId, amount);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(UserConverter.toResponse(userService.findUserById(id)));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{userId}/increase/virtual-money")
    public ResponseEntity<Boolean> increaseVirtualMoney(@PathVariable Long userId, @RequestParam int amount) {
        try {
            Boolean response = userService.increaseVirtualMoney(userId, amount);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{userId}/decrease/virtual-money")
    public ResponseEntity<Boolean> decreaseVirtualMoney(@PathVariable Long userId, @RequestParam int amount) {
        try {
            Boolean response = userService.decreaseVirtualMoney(userId, amount);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
