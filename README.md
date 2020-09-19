# SpringBoot 配置的加载

SpringBoot配置及环境变量的加载提供许多便利的方式，接下来一起来学习一下吧!

本章内容的源码按实战过程采用小步提交，可以按提交的节点一步一步来学习，仓库地址：https://github.com/zhouweixin/spring-boot-configuration。

环境：
* java: 1.8.0_265
* gradle: 6.6.1

## 1 准备

用你喜欢的方式创建一个SpringBoot工程，并写一个hello的接口，及相应的集成测试，进行实验吧！

hello接口代码

HelloController.java
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

HelloControllerTest.java
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

## 2 注解@Value

@Value通过直接注解在属性上为属性设置

如下所示，为name设置值为tangseng

HelloController.java
```java
@Value("tangseng")
private String name;
```

当然，上面的写法不涉及配置文件的读取，但是从配置文件加载数据也是同样简单

如下所示，在${}中用json的方式设置配置文件中设置的key值

HelloController.java
```java
@Value("${value.string}")
private String valueString;
```

配置文件的内容为

application.properties
```properties
value.string=sunwukong
```

当然，@Value的功能还远不止于此，它可以实现数据类型的转换

即，在配置文件中配置的所有内容是没有数据类型的，@Value会根据属性的类型，实现自动转换

如下所示，基本数据类型@Value注解都是可以正确转换的，使用起来有没有感觉很简单呢？

application.properties
```properties
value.int=1
value.float=1.11
value.string=sunwukong
value.bool=true
```

HelloController.java
```java
@Value("${value.int}")
private int valueInt;

@Value("${value.float}")
private float valueFloat;

@Value("${value.string}")
private String valueString;

@Value("${value.bool}")
private boolean valueBool;
```

写到这里，你肯定认为@Value注解的功能就结束了

然而，并没有，@Value还可以设置默认值

即，假如配置文件中没有配置该属性，也可以有默认值兜底的

默认值的设置格式如下所示

HelloController.java
```java
@Value("${value.double:2.22}")
private double valueDouble;
```

接下来，写个接口及集成测试，测试一下结果

HelloController.java
```java
@GetMapping("/helloValue")
public Object helloValue() {
    Map<String, Object> map = new HashMap<>();
    map.put("name", name);
    map.put("valueInt", valueInt);
    map.put("valueFloat", valueFloat);
    map.put("valueString", valueString);
    map.put("valueBool", valueBool);
    map.put("valueDouble", valueDouble);
    return map;
}
```

HelloControllerTest.java
```java
@Test
void helloValue() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/helloValue"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("tangseng")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.valueInt", Matchers.is(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.valueFloat", Matchers.is(1.11)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.valueString", Matchers.is("sunwukong")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.valueBool", Matchers.is(true)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.valueDouble", Matchers.is(2.22)))
    ;
}
```

当然也可以用请求查看一下结果
```shell script
$ curl http://localhost:8080/helloValue
{"valueString":"sunwukong","name":"tangseng","valueDouble":2.22,"valueInt":1,"valueFloat":1.11,"valueBool":true}
```