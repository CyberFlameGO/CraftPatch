## Getting started

Install CraftPatch using [`Maven`]() by adding the JitPack repository to your `pom.xml` file:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Next, add the `CraftPatch` dependency:

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

You can also pass your own Javassist `ClassPool` object if you need to handle more complex class loading scenarios.

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

Next, we will create a `Patch` that will hold all the transformations we want to apply:

```java
// String target, String methodName
// String target, String methodName, String methodDescription
// String target, String methodName, Class<?>[] methodParamTypes (we will be using this one)
Patch patch = new SimplePatch("pack.MyClass", "myMethod", String.class);
```

> ~~**Important:** due to how class loading works in Java we can only transform non-loaded classes, which means you cannot use the referenced class (`MyClass`) before the patch gets applied by the `CraftPatch` instance we just created. Calling `MyClass.class.getName()` would load the class, so we need to specify the classname using a raw `String`.~~
> A tutorial on applying patches that redefine classes on runtime is coming soon.

Now, let's try to override the value returned by the `ownText` field access and return `"def"` instead:

```java
patch.addTransformation(
    new FieldAccessTransform(fieldAccess -> fieldAccess.getFieldName().equals("ownText"))
            .setResult("\"def\"")
);
```

As you can see, all the methods a `Transformation` has expect raw Java source code, which `Javassist` will compile on the fly, so you don't have to learn the specifications of the Java bytecode. In this case we wouldn't need to pass a filter as we only have one field access in the `myMethod` method.

Finally, we can apply the `Patch` by calling the `CraftPatch#applyPatch` method:

```java
patcher.applyPatch(patch);

MyClass instance = new MyClass();

instance.myMethod("def"); // will return true
```

## More details

Every kind of bytecode operation has it's own `Transformation` which you can find on the `me.hugmanrique.craftpatch.transform` package. We also recommend you to take a look at the tests to see how each transformation works and what each method expects.

As the passed source code gets compiled by [Javassist](http://www.javassist.org/), there are special variables you can use to modify the methods in more advanced ways. Here's the tutorial page which specifies which variables each `Transformation` supports: [javassist.org/tutorial/tutorial2.html](http://www.javassist.org/tutorial/tutorial2.html)

A better reference page is also in the works.