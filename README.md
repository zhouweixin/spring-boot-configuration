# SpringBoot 配置的加载

SpringBoot配置及环境变量的加载提供许多便利的方式，接下来一起来学习一下吧!

本章内容的源码按实战过程采用小步提交，可以按提交的节点一步一步来学习，仓库地址：https://github.com/zhouweixin/spring-boot-configuration。

环境：
* java: 1.8.0_265
* gradle: 6.6.1

## 1 准备

用你喜欢的方式创建一个SpringBoot工程，并写一个hello的接口，及相应的集成测试，进行实验吧！

hello接口代码
```shell script
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello world!";
    }
}
```

hello集成测试代码
```shell script
@SpringBootTest
@AutoConfigureMockMvc
class HelloControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void hello() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/hello"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("Hello world!")));
    }
}
```