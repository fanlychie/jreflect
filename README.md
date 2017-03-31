# jreflect

Java 基础反射操作工具包

# 下载依赖

```xml
<repositories>
    <repository>
        <id>github-maven-repo</id>
        <url>https://raw.github.com/fanlychie/maven-repo/releases</url>
    </repository>
</repositories>

<dependency>
    <groupId>org.fanlychie</groupId>
    <artifactId>jreflect</artifactId>
    <version>1.2.0</version>
</dependency>
```

# 样例

设现有一 POJO 类：

```java
public class User {

    private int age;

    private String name;

    private static boolean defaultRememberMe = true;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static boolean isDefaultRememberMe() {
        return defaultRememberMe;
    }

    public static void setDefaultRememberMe(boolean defaultRememberMe) {
        User.defaultRememberMe = defaultRememberMe;
    }
    
}
```

# FieldDescriptor

提供动态操作对象属性或类属性的方法：

**设置对象属性的值**

```java
User user = new User();
FieldDescriptor descriptor = new FieldDescriptor(User.class);
descriptor.setValueByName(user, "name", "fanlychie");
```

或

```java
descriptor.setValueByType(user, "fanlychie");
```

**获取对象属性的值**

```java
String name = descriptor.getValueByName(user, "name");
```

或

```java
String name = descriptor.getValueByType(user, String.class);
```

**操作类属性(静态字段)**

```java
FieldDescriptor descriptor = new FieldDescriptor(User.class);
descriptor.accessibleStatic(true);
boolean defaultRememberMe = descriptor.getValueByName(null, "defaultRememberMe");
```

或

```java
boolean defaultRememberMe = descriptor.getValueByType(null, boolean.class);
```

# BeanDescriptor

提供**全局的**动态操作对象或类的方法：

**设置对象属性的值**

```java
User user = new User();
BeanDescriptor descriptor = new BeanDescriptor(user);
descriptor.setValueByName("name", "fanlychie");
```

或

```java
descriptor.setValueByType("fanlychie");
```

**获取对象属性的值**

```java
String name = descriptor.getValueByName("name");
```

或

```java
String name = descriptor.getValueByType(String.class);
```

**操作类属性(静态字段)**

```java
BeanDescriptor descriptor = new BeanDescriptor(User.class);
boolean defaultRememberMe = descriptor.getValueByName("defaultRememberMe");
```

或

```java
boolean defaultRememberMe = descriptor.getValueByType(boolean.class);
```

**动态创建类的实例**

```java
BeanDescriptor descriptor = new BeanDescriptor(User.class);
User user = descriptor.newInstance();
```

**方法调用**

```java
BeanDescriptor descriptor = new BeanDescriptor(User.class);
User user = descriptor.newInstance();
descriptor.invokeMethod("setName", "fanlychie");
```

或

```java
User user = new User();
BeanDescriptor descriptor = new BeanDescriptor(user);
descriptor.invokeMethod("setName", "fanlychie");
```

# BeanIntrospector

提供**全局的**动态操作对象 getter/setter 方法[不支持操作类方法(静态方法)]：

**调用Setter方法**

```java
User user = new User();
BeanIntrospector introspector = new BeanIntrospector(user);
introspector.invokeSetterMethod("name", "fanlychie");
```

**调用Getter方法**

```java
String name = introspector.invokeGetterMethod("name");
```

**将对象转换为Map表示**

```java
Map<String, Object> map = BeanIntrospector.convertObjectToMap(user);
```

**将Map转换为对象表示**

```java
User user = BeanIntrospector.convertMapToObject(map, User.class);
```

# ConstructorDescriptor

提供**全局的**操作类的构造器的方法：

```java
User user = new ConstructorDescriptor<User>(User.class).newInstance();
```

# MethodDescriptor

提供**全局的**操作类或对象函数的方法：

```java
User user = new User();
MethodDescriptor descriptor = new MethodDescriptor(user);
descriptor.invokeMethod("setName", "fanlychie");
```