Welcome to the CraftPatch documentation, this is currently a work in progress.

# Getting started

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

# Introduction to patches

Before you start writing patches, it is important to develop an understanding of the basic concepts they allow them to work. This section gives a brief introduction to
these concepts. Even if you are familiar with all of the information presented here I recommend at least skim reading the first sections as they introduce the example case
I will be using to demostrate how patches are applied, and some peculiar corners of Java and the JVM which are leveraged heavily by CraftPatch.

## 1. What's a patch?

In order to think about how patches work, I will present a canned example where we will be looking at a class `MyClass`, which contains an `ownText` field and a `checkCode` method:

```java
package com.mypackage;

class MyClass {
    String ownText = "abc";

    void checkCode(String text) {
        return ownText.equals(text);
    }
}
```

In patch jargon `MyClass` is the **target class**, it is the class which the patch will be applied to. A patch contains **transformations** that will modify the bytecode of the class in stages, and are able to add, remove and modify constructors, methods and fields.

Patches get applied by a `PatchApplier`, that depending on its implementation, will load the class (from disk), run each transformation, build the resulting bytecode and, if necessary, replace the already loaded class bytecode.

## 2. Targeting methods

In this case, we want to apply the patch to a single method (`checkCode`), the **target method**. CraftPatch is also capable of transforming all the methods a class contains (this will be explored in the following sections).

Apart from knowing the target method name, the patch also needs to know about the method params[<sup>1</sup>](#nb1), and there are 3 ways of specifying them:

- By passing the method descriptor[<sup>2</sup>](#nb2) (as defined in the JVM specification).
- By passing the parameter type classes. The downside to this approach is that the classes will get loaded so the slower class redefinition strategy will get used. If `null`, it will fallback to the previous approach.
- By passing the parameter type class names. This is the solution to the previous approach downside, which will make the patching process more flexible and faster (as it will use the **class loading** strategy). Will fallback to the previous approach if the classes cannot be found by the current [ClassLoader](https://docs.oracle.com/javase/8/docs/api/java/lang/ClassLoader.html) or `null`.

If every approach fails, the patch won't be applied and a `PatchApplyException` will be thrown.

> <a name="nb1"><sup>1</sup></a> CraftPatch will fallback to a name-based search if none of the options above were passed, but specifying the target method's signature is faster and less error prone.
>
> <a name="nb2"><sup>2</sup></a> A method's **signature** is its set of parameters and *its return type*. For example, for the method:
> ```java
> public ThingType getNearestThing(boolean lookup, int x, int y, double radius) {}
> ```
> the signature would be
> ```
> (boolean,int,int,double)com.mypackage.ThingType
> ```
> note that we put the parameters in parentheses and the return type on the end. In practice to save space, [a more compact syntax is used](https://www.murrayc.com/permalink/1998/03/13/the-java-class-file-format/#TypeDescriptors) and in bytecode the above signature would look like this:
> ```
> (ZIID)Lcom/mypackage/ThingType;
> ```

### 3. Creating our first patch

So now that we know the ways to specify a **target method** let's look at how we declare a patch class with `MyClass` as its **target class** and `checkCode` as its **target method**:

```java
Patch patch = new SimplePatch("com.mypackage.MyClass", "checkCode", String.class);
```

Yes it really is that simple. Using the `SimplePatch(String targetClass, String methodName, Class[] methodParamTypes...)` constructor specifies the **target class** we want to apply it to and a way to find the method we want to target.

Now, take a look at the code above again. Don't you think it would be much more mantainable to pass `MyClass.class.getName()` as the first parameter? By default, CraftPatch will happily patch any class, however if a [ClassLoader](https://docs.oracle.com/javase/8/docs/api/java/lang/ClassLoader.html) loaded the class before (or you aren't sure) applying the patch will result in a `PatchApplyException`. Before continuing, ask yourself the following questions:

- What are the implications of loading a class?

- Is it even possible to redefine the bytecode of an already loaded class?

After all, the bytecode is only stored in disk and accessing the bytecode of a class in memory would present security issues, right? The answer to the two questions is:

- Once a class gets loaded by the JVM class loader, it's no longer possible to redefine it using the default **class loading** strategy the `PatchApplier` uses, which will cause problems if you're trying to modify it with a patch.

- Fortunately, Java has an [Instrumentation API](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/Instrumentation.html) which gives us the option to modify the bytecode from an already loaded class. If a patch needs to modify an already loaded class, you will need to specify you want to use the **class redefinition** strategy explained below.

Also, don't worry about security, the attached agent is governed by the same security context applicable for Java classes and respective classloaders.

### Class loading strategy

A faster but more restrictive method of applying patches. CraftPatch will happily compile any class using this strategy, however if a [`ClassLoader`](https://docs.oracle.com/javase/8/docs/api/java/lang/ClassLoader.html) loaded the class applying the patch will result in a `PatchApplyException`.

This strategy reads the source `.class` file contained in your `.jar` file, applies all the patch transformations and finally loads the transformed class.

### Class redefinition strategy

A slower alternative that will (generally speaking) patch any class. It works by [attaching a Java agent](https://www.javacodegeeks.com/2015/09/java-agents.html) to the JVM process to grab an [Instrumentation](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/Instrumentation.html) instance (this process will usually take 1 to 2 seconds), creating the needed class definitions from the patch and finally calling [`Instrumentation#redefineClasses()`](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/Instrumentation.html#redefineClasses-java.lang.instrument.ClassDefinition...-).

## 4. Applying the patch

If we were to include this patch in our runtime right now and run our application, the patch wouldn't be applied and absolutely anything would be changed, this is because _(1)_ we haven't haven't created a `PatchApplier` that creates the new bytecode and transforms the **target class** and, _(2)_ we haven't actually added any transformations to the patch. Let's take a look at how we can achieve objective **1** above, and actually apply the patch:

```java
PatchApplier applier = new PatchApplier();

try {
    applier.applyPatch(patch);
} catch (PatchApplyException e) {
    e.printStackTrace();
}
```

That's it! Any transformations added to the patch will get applied sequentially to the **target class** when the patch is applied.

If the **target class** is loaded, you will need to call the same method with the `redefine` boolean flag set to `true` to use the **class redefinition** strategy:

```java
try {
    applier.applyPatch(patch, true);
} catch (PatchApplyException e) {
    e.printStackTrace();
}
```

## 5. Transformations 101

Since we've taken care of the first objective and can now successfully apply our patch to the **target class**, let's take a look at the second objective using an example:

- Let us modify the value returned by the _field access_ of `ownText` on the `checkCode` method to return `"def"` instead of `"abc"`.

We'll begin by creating a `FieldAccessTransform` instance, which takes an **expression filter** (a `Predicate<FieldAccess>`) and has a method to override the result returned by the access:

```java
FieldAccessTransform transform =
                new FieldAccessTransform(fieldAccess -> fieldAccess.getFieldName().equals("ownText"))
                        .setResult("\"def\"");
```

There are some useful things to make a note of here:

- Firstly, it's crucial to note that the **expression filter** will determine whether a bytecode instruction needs to be transformed or not. Most (if not all) the built-in transformations also have a constructor without a filter, but their use is discouraged as one of the key principles of transformations is that they should follow the [Single responsibility principle](https://en.wikipedia.org/wiki/Single_responsibility_principle). In this case we just check if the instruction is referencing a field that's called `ownText`. Other useful methods such as [`FieldAccess#getClassName()`](http://www.javassist.org/html/javassist/expr/FieldAccess.html#getClassName--) are available too!

- We overrided the field access result by calling the `#setResult` method, which takes a code fragment with source text written in Java. CraftPatch includes a simple Java compiler courtesy of [Javassist](http://www.javassist.org) for processing source text. We just changed the return value from `"abc"` to `"def"`, but please note you can also call a external method such as `this.getClass().getSimpleName()`, in which case we would call `.setResult("this.getClass().getSimpleName()")`.

Calling the following code after the patch gets applied would return `true`:

```java
MyClass myClass = new MyClass();

myClass.checkCode("def"); // true, even if `ownText` is "abc"
```

> You will need to become familiar with Javassist [special variables](http://www.javassist.org/tutorial/tutorial2.html#intro) if you plan to use _transforms_.

All transformations have `replace()`, `prepend()`, `append()` and `insert()` methods that receive a `String` object representing a statement or a block. A statement is a single control structure like `if` and `while` or an expression ending with a semicolon (`;`). A block is a set of statements surrounded by braces `{ }`. Hence each of the following lines is an example of a valid statement or block:

```java
System.out.println("Hello world"); // Statement
{ System.out.println("Hello world"); } // Block
if (i < 0) { i = -i; } // Statement
```

Of course, statements or blocks can refer to fields and methods and declare local variables. If you read the Javasist tutorial page, you will remember identifiers starting with `$` have [special meaning](http://www.javassist.org/tutorial/tutorial2.html#intro).

Every transformation uses the builder pattern so you can chain method calls. For example, this is useful if you plan to append source code **and** modify a cast when using a [CastTransform](https://jitpack.io/com/github/hugmanrique/CraftPatch/master-SNAPSHOT/javadoc/me/hugmanrique/craftpatch/transform/expr/CastTransform.html), which you would create as follows:

```java
Transformation castTransform =
                new CastTransform(cast -> {
                    try {
                        return cast.getType().getName().equals("List");
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .setCastClass("ArrayList")
                .append("if (!($1 instanceof java.util.ArrayList)) { $1 = new java.util.ArrayList($1); }");
```

Here's a list of all the built-in transformations (we also encourage you to go through each transformation test and check the example implementations):

- Expression transformations
    - [`CastTransform`](https://jitpack.io/com/github/hugmanrique/CraftPatch/master-SNAPSHOT/javadoc/me/hugmanrique/craftpatch/transform/expr/CastTransform.html)
    - [`CatchTransform`](https://jitpack.io/com/github/hugmanrique/CraftPatch/master-SNAPSHOT/javadoc/me/hugmanrique/craftpatch/transform/expr/CatchTransform.html)
    - [`FieldAccessTransform`](https://jitpack.io/com/github/hugmanrique/CraftPatch/master-SNAPSHOT/javadoc/me/hugmanrique/craftpatch/transform/expr/FieldAccessTransform.html)
    - [`InstanceofTransform`](https://jitpack.io/com/github/hugmanrique/CraftPatch/master-SNAPSHOT/javadoc/me/hugmanrique/craftpatch/transform/expr/InstanceofTransform.html)
    - [`MethodCallTransform`](https://jitpack.io/com/github/hugmanrique/CraftPatch/master-SNAPSHOT/javadoc/me/hugmanrique/craftpatch/transform/expr/MethodCallTransform.html)
    - [`NewArrayTransform`](https://jitpack.io/com/github/hugmanrique/CraftPatch/master-SNAPSHOT/javadoc/me/hugmanrique/craftpatch/transform/expr/NewArrayTransform.html)
    - [`NewExprTransform`](https://jitpack.io/com/github/hugmanrique/CraftPatch/master-SNAPSHOT/javadoc/me/hugmanrique/craftpatch/transform/expr/NewExprTransform.html)
- Method transformations
    - [`MethodTransform`](https://jitpack.io/com/github/hugmanrique/CraftPatch/master-SNAPSHOT/javadoc/me/hugmanrique/craftpatch/transform/method/MethodTransform.html)
    - [`CopyMethodTransform`](https://jitpack.io/com/github/hugmanrique/CraftPatch/master-SNAPSHOT/javadoc/me/hugmanrique/craftpatch/transform/method/CopyMethodTransform.html)

## 4. Creating your own transformations

Coming soon


<!--As we've seen, each patch contains multiple transformations which will alter the target's bytecode once the patch gets applied, but why not keep that logic on the patch itself?

The rationale behind this separation is the conciseness and flexibility of a separate class which can target a single kind of bytecode instruction.

As you've seen, each patch can contain multiple transformations which alter the target's bytecode, but why not keep that logic on the patch itself? Transformations are concise ways of targetting a single kind of bytecode instruction such as a method call (`INVOKESTATIC`), a cast (`CHECKCAST`)... and modifying its behaviour without affecting any other instructions on the same method.

Transformations are heavily tied to [Javassist](http://www.javassist.org/) expressions, `CtMethod`s, `CtField`s and `CtConstructors`. CraftPatch provides a transformation implementation with useful methods for each instruction type which you can find on the [`transform.expr` package](https://jitpack.io/com/github/hugmanrique/CraftPatch/master-SNAPSHOT/javadoc/me/hugmanrique/craftpatch/transform/expr/package-summary.html).-->

<!--Let's first start with an already implemented transformation and then see how we can create our own.

Let's first look at the `checkCode` method's body of our imaginary class:

```java
void checkCode(String text) {
    return ownText.equals(text);
}
```

Even though this method seems simple, it contains the following instructions:

- `ALOAD_0`: loads a reference onto the stack from the first parameter (`text`)
- `GETFIELD #3`: accesses the `ownText` local field
- `ALOAD_1`: loads a reference onto the stack from the `ownText` variable
- `INVOKEVIRTUAL #4`: invokes the `String#equals(Object)` method, passing the `text` variable as a parameter
- `IRETURN`: return the result of the previous method invocation

In this example, we want to modify the `ownText` field access result and return `"def"` instead of `"abc"` (which would be the normal result without any bytecode redefinition applied). In contrast to the instructions above, using the built-in transformations is super user-friendly and only requires a couple lines of code:

```java
FieldAccessTransform transform = new FieldAccessTransform(access -> access.getFieldName().equals("ownText"))
                                            .setResult("\"def\"");
```

The `FieldAccessTransform` takes an (optional) filter (a `Predicate<FieldAccess>`) which will return `true` if we want to apply the transformation on that field access or not (basically skipping it). Next, we call the `FieldAccessTransform#setResult(String result)` which takes the raw Java source as an input that will get compiled when the patch is applied.

All the built-in transforms use the builder pattern so you can chain method calls. Also, all the **transformations** extend [`BaseTransform`](https://jitpack.io/com/github/hugmanrique/CraftPatch/master-SNAPSHOT/javadoc/me/hugmanrique/craftpatch/transform/BaseTransform.html), so all the transformations support complete instruction replacements, raw source prepends and appends and method invocation prepends and appends (_and you can pass raw Java source to them!_)

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

    void checkCode(String text) {
        return ownText.equals(text);
    }
}
```

Next, we will create a `Patch` that will hold all the transformations we want to apply:

```java
// String target, String methodName
// String target, String methodName, String methodDescription
// String target, String methodName, Class<?>[] methodParamTypes (we will be using this one)
Patch patch = new SimplePatch("pack.MyClass", "checkCode", String.class);
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

As you can see, all the methods a `Transformation` has expect raw Java source code, which `Javassist` will compile on the fly, so you don't have to learn the specifications of the Java bytecode. In this case we wouldn't need to pass a filter as we only have one field access in the `checkCode` method.

Finally, we can apply the `Patch` by calling the `CraftPatch#applyPatch` method:

```java
patcher.applyPatch(patch);

MyClass instance = new MyClass();

instance.checkCode("def"); // will return true
```

## More details

Every kind of bytecode operation has it's own `Transformation` which you can find on the `me.hugmanrique.craftpatch.transform` package. We also recommend you to take a look at the tests to see how each transformation works and what each method expects.

As the passed source code gets compiled by [Javassist](http://www.javassist.org/), there are special variables you can use to modify the methods in more advanced ways. Here's the tutorial page which specifies which variables each `Transformation` supports: [javassist.org/tutorial/tutorial2.html](http://www.javassist.org/tutorial/tutorial2.html)

A better reference page is also in the works.-->