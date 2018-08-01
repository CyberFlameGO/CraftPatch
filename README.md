# :hamburger: CraftPatch

[![releases][releases]][releases-url]
[![jitpack][jitpack]][jitpack-url]
[![tests][tests]][tests-url]
[![license][license]][license-url]

An easy to use Java instrumentation API that allows you to patch methods using transformations which can target specific casts, fields accesses...

## Features

- Flexible and simple API built for all specific use cases
- High performance: transformations compile source code to bytecode on the fly using [Javassist](http://www.javassist.org/)
- First-class support for external transformations and patch classes

## Getting started

Install CraftPatch using [`Maven`](https://maven.apache.org/) by adding the JitPack repository to your `pom.xml` file:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Next, add the `craftpatch` dependency:

```xml
<dependency>
    <groupId>com.github.hugmanrique</groupId>
    <artifactId>CraftPatch</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```

You will need to have Java 8 version 45 or later (older versions _might_ work).

Let's get started by creating a `CraftPatch` instance, which will be in charge of applying the patches:

```java
private final CraftPatch patcher = new CraftPatch();
```

You can also pass a custom Javassist `ClassPool` object if you need to handle more complex class loading scenarios.

Next, we will create a `Patch` that will hold all the transformations we want to apply:

```java
// String target, String methodName
// String target, String methodName, String methodDescription
// String target, String methodName, Class<?>[] methodParamTypes (we will be using this one)
Patch patch = new SimplePatch("pack.MyClass", "myMethod", String.class);
```

Assume the class we want to transform is the following:

```java
package pack;

class MyClass {
    String ownText = "abc";

    void myMethod(String text) {
        return ownText.equals(text);
    }
}
```

**Important:** due to how class loading works in Java we can only transform non-loaded classes, which means you cannot use the referenced class (`MyClass`) before the patch gets applied by the `CraftPatch` instance we just created.

Now, let's try to override the value returned by the `ownText` field access and return `"def"` instead:

```java
patch.addTransformation(
    new FieldAccessTransform()
        .setResult("\"def\"")
);
```

As you can see, all the methods a `Transformation` has expect raw Java source code, `Javassist` will compile it on the fly so you don't have to leran the specifications of the Java bytecode.

Finally, we can apply the `Patch` by calling the `CraftPatch#applyPatch` method:

```java
patcher.applyPatch(patch);

MyClass instance = new MyClass();

instance.myMethod("def"); // will return true
```

# License

[MIT](LICENSE) &copy; [Hugo Manrique](https://hugmanrique.me)

[releases]: https://img.shields.io/github/downloads/hugmanrique/CraftPatch/total.svg
[releases-url]: https://github.com/hugmanrique/CraftPatch/releases
[jitpack]: https://jitpack.io/v/hugmanrique/CraftPatch.svg
[jitpack-url]: https://jitpack.io/#hugmanrique/CraftPatch
[tests]: https://img.shields.io/travis/hugmanrique/CraftPatch/master.svg
[tests-url]: https://travis-ci.org/hugmanrique/CraftPatch
[license]: https://img.shields.io/github/license/hugmanrique/CraftPatch.svg
[license-url]: LICENSE