package com.yupi.yupicturebackend;

import cn.hutool.core.util.StrUtil;
import com.yupi.yupicturebackend.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
//@MapperScan("com.yupi.yupicturebackend.mapper")
class YuPictureBackendApplicationTests {
@Test
    void test() {
    StrUtil.format("{}你好，{}", "1");


}
//    @Test
//    void contextLoads() {
//    }
//    @Autowired
//    private UserController userController;

//    @Test
//    void testAuthCheck() {
//        // 测试会直接抛出NO_AUTH_ERROR
////        userController.login()
//        userController.getUserById(1L);
//    }

}
