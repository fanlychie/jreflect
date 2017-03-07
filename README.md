# reflection

Java 基础反射操作工具包

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

**设置字段的值**

```java
User user = new User();
FieldDescriptor descriptor = new FieldDescriptor(User.class);
descriptor.setValueByName(user, "name", "fanlychie");
```

或

```java
descriptor.setValueByType(user, "fanlychie");
```

**获取字段的值**

```java
String name = descriptor.getValueByName(user, "name");
```

或

```java
String name = descriptor.getValueByType(user, String.class);
```