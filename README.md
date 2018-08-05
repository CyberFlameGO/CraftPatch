# :hamburger: CraftPatch

[![jitpack][jitpack]][jitpack-url]
[![tests][tests]][tests-url]
[![license][license]][license-url]

**CraftPatch** is an easy to use Java instrumentation API that allows you to patch methods using transformations which can target specific invocations, fields accesses...

## Features

- Flexible and simple API built for all specific use cases
- Supports class redefinition via a lazily attached JVM agent
- High performance: transformations compile source code to bytecode on the fly using [Javassist](http://www.javassist.org/) (which uses your `javac` binary)
- First-class support for external transformations and patch classes

## Documentation

The main documentation for CraftPatch can be found in the [`docs/`](docs/README.md) directory.

Additional documentation for individual features can be found in the [Javadoc](https://jitpack.io/com/github/hugmanrique/CraftPatch/master-SNAPSHOT/javadoc/). For additional help, you can create an issue and I will try to respond to it as fast as I can.

## Building CraftPatch

CraftPatch uses [Maven](https://maven.apache.org/). To perform a build, execute

```bash
mvn package
```

from within the project root directory.

# License

[MIT](LICENSE) &copy; [Hugo Manrique](https://hugmanrique.me)

[jitpack]: https://jitpack.io/v/hugmanrique/CraftPatch.svg
[jitpack-url]: https://jitpack.io/#hugmanrique/CraftPatch
[tests]: https://img.shields.io/travis/hugmanrique/CraftPatch/master.svg
[tests-url]: https://travis-ci.org/hugmanrique/CraftPatch
[license]: https://img.shields.io/github/license/hugmanrique/CraftPatch.svg
[license-url]: LICENSE