package com.ll.exam.sbb;

import com.ll.exam.sbb.user.UserRepository;
import com.ll.exam.sbb.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;


    @BeforeEach
    public void TearDown() {
        userRepository.deleteAll();
    }

    @DisplayName("회원 가입됨")
    @Test
    public void 회원이_가입된다() {
        userService.create("user", "user@gmail.com", "user123");
    }
}
