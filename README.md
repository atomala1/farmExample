# The Farm


:pig2: :turkey:


### Overview


This project represents a simple API for managing barns and their animal inhabitants on a hypothetical farm. There are
a number of rules and restrictions about how animals should be organized within the farm's array of colored barns. These
specifications are outlined in detail within the comments surrounding `AnimalService`.


When you're finished you should have an implementation for each of the methods within the `AnimalService` interface,
which is located at `com.logicgate.farm.service`. The implementing class, `AnimalServiceImpl`, has already been created
next to the interface and lined with TODOs. In addition to adding code to the service class, you may want to add some
additional methods to both the `FarmRepository` and `AnimalRepository` which can be located at
`com.logicgate.farm.repository`. While the service implementation can be completed successfully without doing so
and relying exclusively on the repositories' inherited methods (e.g. `findAll()`, `getOne()`, `save()`, and `delete()`)
your solution may be cleaner and/or more efficient by adding repository methods. Your solution should not need any type of nested iteration. 


### Rules and Testing


The project has some pre-built tests that can serve to check your work. Feel free to add additional tests if it helps 
you work through the problem. You're also welcome to modify the existing tests, but you may lose the ability to reliably
validate your solution. If you're stuck or confused about any of the requirements it can be helpful to analyze the test 
as a means to "work backwards" to a solution. To run the tests, simply execute `./gradlew cleanTest test` from the 
project root.

_You must:_
- use streams

_You may:_

- add any additional methods or classes to project files
- add external libraries to the project

_You may not:_

- modify entity classes
- reformat the files 


### Summary


The primary goal of this exercise is to demonstrate your ability to problem solve and write well-organized code. If you
are new to Spring Boot, Gradle or Java you may have to browse documentation for syntax, but the high-level concepts of
the exercise are intended to be platform agnostic. If you have a brilliant way to solve the challenge, but can't quite
figure out how to fit your solution into the framework please leave behind comments that highlight what you want to do
and where you are blocked along the way. 


### Resources


The following documentation may be helpful as you dig into the case.

- [Spring Boot](https://projects.spring.io/spring-boot/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Data JPA Repositories](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.repositories)
- [Accessing Data With JPA](https://spring.io/guides/gs/accessing-data-jpa/)
- [Barn Docs](https://en.wikipedia.org/wiki/Barn)
- [Goat Docs](https://twitter.com/EverythingGoats)


---


:goat:
