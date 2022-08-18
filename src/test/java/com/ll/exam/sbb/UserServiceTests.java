package com.ll.exam.sbb;

import com.ll.exam.sbb.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    UserService userService;

    @Test
    public void 회원이_가입된다() {
        userService.create("user", "user@gmail.com", "user123");
    }
}
