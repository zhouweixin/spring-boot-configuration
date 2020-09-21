# SpringBoot 配置的加载

SpringBoot配置及环境变量的加载提供许多便利的方式，接下来一起来学习一下吧!

本章内容的源码按实战过程采用小步提交，可以按提交的节点一步一步来学习，仓库地址：https://github.com/zhouweixin/spring-boot-configuration。



本文将要介绍

1. SpringBoot配置的加载
2. SrpingBoot环境变量的加载
3. 测试时动态设置配置
4. 启动时动态设置配置



环境：
* java: 1.8.0_265
* gradle: 6.6.1

## 1 准备

用你喜欢的方式创建一个SpringBoot工程，并写一个hello的接口，及相应的集成测试，进行实验吧！

### 1.1 hello接口代码

HelloController.java
```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello world!";
    }
}
```

### 1.2 hello集成测试代码

HelloControllerTest.java
```java
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

### 2.1 介绍

@Value通过直接注解在属性上为属性设置

如下所示，为name设置值为tangseng

HelloController.java
```java
@Value("tangseng")
private String name;
```

### 2.2 加载配置文件

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

### 2.3 数据类型转换

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

### 2.4 默认值

写到这里，你肯定认为@Value注解的功能就结束了

然而，并没有，@Value还可以设置默认值

即，假如配置文件中没有配置该属性，也可以有默认值兜底的

默认值的设置格式如下所示

HelloController.java
```java
@Value("${value.double:2.22}")
private double valueDouble;
```

### 2.5 时间转换

这次，你一定又一次认为@Value的学习结束了，但是想再分享@Value对时间的处理，因为实际项目中经常会配置超时时间等类似的时间，比较实用

假如配置文件里配置了`timeout=60`，你认为是60s呢还是60ms，或是60m，是不是有点不清楚呢？

因此，多是配置成`timeout=60s`, 利用@DurationUnit进行单位的转换

还是看个例子比较直观些

首先配置一个`10分钟`

application.properties
```properties
value.time=10m
```

然后用`秒`去解析，看看结果是否正确，这里悄悄告诉你，结果依然是正确的，转成了`600s`

HelloController.java
```java
@Value("${value.time}")
@DurationUnit(ChronoUnit.SECONDS)
private Duration time;
```

### 2.6 集成测试

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
            .andExpect(MockMvcResultMatchers.jsonPath("$.time", Matchers.is("600s")));
}
```

当然也可以用请求查看一下结果
```shell
$ curl http://localhost:8080/helloValue
{"valueString":"sunwukong","name":"tangseng","valueDouble":2.22,"time":"600s","valueInt":1,"valueFloat":1.11,"valueBool":true}
```

## 3 注解@ConfigurationProperties

### 3.1 介绍

@ConfigurationProperties实现了更加丰富的功能，但是该属性需要配置@ConfigurationPropertiesScan使用

即，首先需要将@ConfigurationPropertiesScan注解到启动类上，即XxxApplication.java

然后便可以利用@ConfigurationProperties上

@ConfigurationProperties是用来注解类上，用来批量从配置文件中加载数据

比如，配置中有如下属性

application.properties
```properties
student.name=xiaoming
student.email=123456@qq.com
student.age=18
```

便可以定义Student类，并将@ConfigurationProperties注解其上

注意：属性名需要和配置文件里对应的名字相同，你肯定观察到了

Student.java
```java
@ConfigurationProperties("student")
public class Student {
    private String name;
    private String email;
    private int age;
    
    // ... 省略setter, getter方法， setter方法必须有
}
```

### 3.2 加载集合数据

@ConfigurationProperties除了可以读单值数据，也可以读List和Map数据

比如，配置文件里有如下配置

application.properties
```properties
# class.list
student.friends[0]=zhubajie
student.friends[1]=shaheshang

# class.map
student.parent.father=tangseng
student.parent.mother=nverguoguowang
```

只需要在Student类中再添加两个属性即可，不要忘记setter和getter方法哟

Student.java
```java
private List<String> friends;
private Map<String, String> parent;
```

添加getStudent接口

HelloController.java
```java
@GetMapping("/getStudent")
public Student getStudent() {
    return student;
}
```

### 3.3 集成测试

HelloControllerTest.java
```java
@Test
void getStudent() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/getStudent"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("xiaoming")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("123456@qq.com")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.age", Matchers.is(18)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.friends[0]", Matchers.is("zhubajie")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.friends[1]", Matchers.is("shaheshang")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.parent.father", Matchers.is("tangseng")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.parent.mother", Matchers.is("nverguoguowang")));
}
```

直接求观测也是可以的
```shell
$ curl http://localhost:8080/getStudent
{"name":"xiaoming","email":"123456@qq.com","age":18,"friends":["zhubajie","shaheshang"],"parent":{"father":"tangseng","mother":"nverguoguowang"}}
```

## 4 环境变量Environment

### 4.1 介绍

在实际项目场景中，环境变量也是一种动态配置的有效方案

比如，在本地加载local的环境变量，在dev加载dev的环境变量

SpringBoot对于环境变量的加载比较简单，只需要Environment类即可，但是需要注意该类的包，不要导错了
```java
import org.springframework.core.env.Environment;
```

使用过程，首先注入

HelloController.java
```java
@Autowired
private Environment env;
```

然后你便可以通过getProperty获取环境变量了
```java
result.put("JAVA_HOME", env.getProperty("JAVA_HOME"));
result.put("GRADLE_HOME", env.getProperty("GRADLE_HOME"));
result.put("NO_ENV", env.getProperty("NO_ENV", "no env variable"));
```

是不是特别简单呢，其实就是这么简单，下面就不多验证了

## 5 测试动态指定配置

### 5.1 介绍

下面介绍测试时动态修改配置

你在项目中是不是经常碰到集成测试和运行属性不一样的值的，本章就是解决该问题的

接下来介绍3种方法

### 5.2 注解@ActiveProfiles

该注解的使用比较简单，可以动态指定加载的配置文件

首先，复制一份`application.properties`，命名为`application-dev.properties`

修改一部分值

application-dev.properties
```properties
# simple value
value.int=2
value.float=2.22
value.string=zhubajie
value.bool=false
value.time=1s
```

接着，复制一份`HelloControllerTest.java`，命名为`ActiveProfilesTest.java`

在类上添加注解`@ActiveProfiles("local")`，就完成了该测试类动态加载配置文件的目的

ActiveProfilesTest.java
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ActiveProfilesTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void helloValue() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/helloValue"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueInt", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueFloat", Matchers.is(2.22)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueString", Matchers.is("zhubajie")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueBool", Matchers.is(false)));
    }
}
```

该注解有个缺陷，指定配置文件的命名必须是固定的格式`application-xxx.properties`

而对于外部的配置文件或者其它格式命名的却无法加载

### 5.3 注解@TestPropertySource

**属性locations**

该注解恰好是用来解决@ActiveProfiles的不足的

其locations属性可以加载任意的配置文件，只要存在

示例

TestPropertyResourceTest.java
```java
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = {"/a.properties"})
class TestPropertyResourceTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void helloValue() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/helloValue"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueInt", Matchers.is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueFloat", Matchers.is(3.33)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueString", Matchers.is("shaheshang")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueBool", Matchers.is(true)));
    }
}
```

a.properties
```properties
# simple value
value.int=3
value.float=3.33
value.string=shaheshang
value.bool=true
```

该属性的使用就是这么简单

然而，当仅需要指定很少属性的时候，通过指定配置文件的方式显得过于笨重

**属性properties**

locations属性对于指定部分属性就很直接了，而且其优先级高于属性locations

示例

TestPropertyResource2Test.java
```java
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"value.int=10", "value.float=10.10"}, locations = {"/a.properties"})
class TestPropertyResource2Test {
    @Autowired
    MockMvc mockMvc;

    @Test
    void helloValue() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/helloValue"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueInt", Matchers.is(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueFloat", Matchers.is(10.10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueString", Matchers.is("shaheshang")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueBool", Matchers.is(true)));
    }
}
```

## 6 启动时动态指定配置

你肯定了解过spring启动的方法

1. 通过开发工具（idea, eclipse）启动
2. java命令启动
3. gradle命令启动
4. maven命令启动
5. 。。。



通过开发工具启动指定配置相对比较简单，如下图中的红框所示，可以方便的设置

1. Environment variables：环境变量
2. Active profiles：配置文件
3. Override parameters：参数



### 6.1 通过idea设置

![image-20200921215130704](/Users/zhouweixin/Library/Application Support/typora-user-images/image-20200921215130704.png)

设置完后，通过浏览器访问接口`http://localhost:8080/helloValue`的结果如下

```json
{
    "NO_ENV": "no env variable",
    "valueString": "sunwukong",
    "name": "tangseng",
    "JAVA_HOME": "test_java_home",
    "valueDouble": 2.22,
    "time": "600s",
    "valueInt": 1111,
    "valueFloat": 1.11,
    "valueBool": true,
    "GRADLE_HOME": "/Users/zhouweixin/.sdkman/candidates/gradle/current"
}
```



通常开发完成后会部署到linux系统中，这时通过命令指定配置则尤其显得重要



### 6.2 通过java命令设置

在学习之前先学会打包

* 用maven工程的可以用`mvn package`打包

* 用gradle工程的可以用`gradle bootJar`打包



笔者采用的gradle打包，生成文件：`build/libs/configuration-0.0.1-SNAPSHOT.jar`

```shell
$ gradle bootJar            

BUILD SUCCESSFUL in 676ms
3 actionable tasks: 1 executed, 2 up-to-date
```



该jar文件拷贝到任何机器上都可以运行，前提是该机器安装了jre，启动命令也比较简单

```
java -jar build/libs/configuration-0.0.1-SNAPSHOT.jar
```



通过浏览器访问接口`http://localhost:8080/helloValue`的结果如下，加载的是`application.properties`

```json
{
    "NO_ENV": "no env variable",
    "valueString": "sunwukong",
    "name": "tangseng",
    "JAVA_HOME": "/Users/zhouweixin/.sdkman/candidates/java/current",
    "valueDouble": 2.22,
    "time": "600s",
    "valueInt": 1,
    "valueFloat": 1.11,
    "valueBool": true,
    "GRADLE_HOME": "/Users/zhouweixin/.sdkman/candidates/gradle/current"
}
```



指定`application-local.properties`也比较容易

```shell
java -jar build/libs/configuration-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```



仅设置部分属性的方式

```java
java -jar build/libs/configuration-0.0.1-SNAPSHOT.jar --value.int=1000 --value.string=zhangsanfeng
```



## 总结

本文粗略地记录了常用的

1. SpringBoot配置的加载
2. SrpingBoot环境变量的加载
3. 测试时动态设置配置
4. 启动时动态设置配置



本文仅为了抛砖引玉，好学的你一起来努力吧！



错误及不全面的地方多谢批评指正！