Welcome to the CraftPatch documentation, this is currently a work in progress.

## Getting started

You can install CraftPatch using [`Maven`](https://maven.apache.org/) by adding the JitPack repository to your `pom.xml` file:

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

## Introduction to patches

Before you start writing patches, it is important to develop an understanding of the basic concepts they allow them to work. This section gives a brief introduction to
these concepts. Even if you are familiar with all of the information presented here I recommend at least skim reading the first sections as they introduce the example case
I will be using to demostrate how patches are applied, and some peculiar corners of Java and the JVM which are leveraged heavily by CraftPatch.

### 1. What's a patch?

In order to think about how patches work, I will present a canned example where we will be looking at a class `MyClass`, which contains a `myMethod` method and a `ownText` field:

```java
package pack;

class MyClass {
    String ownText = "abc";

    void myMethod(String text) {
        return ownText.equals(text);
    }
}
```

In patch jargon `MyClass` is the **target class**, it is the class which the patch will be applied to. A patch contains **transformations** that will modify the bytecode of the class in stages, and are able to add, remove and modify constructors, methods and fields.

A `PatchApplier` is in charge of loading the class (from disk), applying each transformation and building the resulting bytecode. At this point, you may ask yourself the following questions:

- What are the practical implications of loading a class in terms of what kinds of transformations can be applied?

- Is it even possible to redefine the bytecode of an already loaded class?

After all, the bytecode is only stored in disk and accessing the bytecode of a class in memory would present security issues, right?

In order to answer these questions, we need to talk about the Java [Instrumentation API](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/Instrumentation.html):

The Instrumentation API gives us the option to access a class that is loaded by the Java classloader from the JVM and modify its bytecode by inserting the resulting one from the patch.

So, if a patch needs to modify a class that was already loaded by the JVM, we will need to [attach an agent](https://www.javacodegeeks.com/2015/09/java-agents.html) dynamically, which is a process that usually takes 1 to 2 seconds, so it increments the time it takes for a patch to get fully applied.

Another limitation of this process is that your patch must only contain modification transformations. What this means is that the transformed class must retain the same schema i.e. constructor, methods and fields cannot be added, renamed or removed.

The patch applier has methods to specify which class redefinition strategy you want to use:

- The faster but more restrictive which you can only apply if the **target class** is not loaded.
- The slower alternative which (generally speaking) will always work.

## 2. Targetting methods

In this case, we will want to apply the patch to a single method (`myMethod`), the **target method**. CraftPatch is also capable of transforming all the methods a class contains, but this will be explored in the following sections.

Apart from knowing the target method name, the patch also needs to know about the method params[<sup>1</sup>](#nb1), and there are 3 ways of specifying them:

- By passing the method descriptor[<sup>2</sup>](#nb2) (as defined in the JVM specification), if `null` the patch will fallback to a name-based method search.
- By passing the parameter type classes. The downside to this approach is that the classes must be loaded and this will limit the kinds of transformations this patch can apply (as we will see in the next section). If `null`, it will fallback to the previous approach.
- By passing the parameter type class names. This is the solution to the previous approach downside, which will make the patching process more flexible and faster (as we will see in the next section), but the patch might not be able to find those classes if the current [ClassLoader](https://docs.oracle.com/javase/8/docs/api/java/lang/ClassLoader.html) cannot find the class by its name, in which case it will fallback to the previous strategy.

If every approach fails, the patch won't be applied and a `PatchApplyException` will be thrown.

> <a name="nb1"><sup>1</sup></a> CraftPatch is also able to find a method only by its name, but specifying the target method's signature is faster and less error prone.
> <a name="nb2"><sup>2</sup></a>

<!--Let's get started by creating a `PatchApplier` instance, which will be in charge of transforming your patches to bytecode and redefining the classes they target:

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

A better reference page is also in the works.-->