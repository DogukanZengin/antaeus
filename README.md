## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month. Our database contains a few invoices for the different markets in which we operate. Your task is to build the logic that will schedule payment of those invoices on the first of the month. While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

## Instructions

Fork this repo with your solution. Ideally, we'd like to see your progression through commits, and don't forget to update the README.md to explain your thought process.

Please let us know how long the challenge takes you. We're not looking for how speedy or lengthy you are. It's just really to give us a clearer idea of what you've produced in the time you decided to take. Feel free to go as big or as small as you want.

## Developing

Requirements:
- \>= Java 11 environment

Open the project using your favorite text editor. If you are using IntelliJ, you can open the `build.gradle.kts` file and it is gonna setup the project in the IDE for you.

### Building

```
./gradlew build
```

### Running

There are 2 options for running Anteus. You either need libsqlite3 or docker. Docker is easier but requires some docker knowledge. We do recommend docker though.

*Running Natively*

Native java with sqlite (requires libsqlite3):

If you use homebrew on MacOS `brew install sqlite`.

```
./gradlew run
```

*Running through docker*

Install docker for your platform

```
docker build -t antaeus
docker run antaeus
```

### App Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── buildSrc
|  | gradle build scripts and project wide dependency declarations
|  └ src/main/kotlin/utils.kt 
|      Dependencies
|
├── pleo-antaeus-app
|       main() & initialization
|
├── pleo-antaeus-core
|       This is probably where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|       Module interfacing with the database. Contains the database 
|       models, mappings and access layer.
|
├── pleo-antaeus-models
|       Definition of the Internal and API models used throughout the
|       application.
|
└── pleo-antaeus-rest
        Entry point for HTTP REST API. This is where the routes are defined.
```

### Main Libraries and dependencies
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library
* [Sqlite3](https://sqlite.org/index.html) - Database storage engine

Happy hacking 😁!

---

## Antaeus - Dogukan's Journey

### Thought process

 It seems like an easy task, but actually there are lots of decisions to make when it comes to implementation. Normally, 
we need to **gather requirements** from stakeholders here, and then define the expected properties from this service. But since this is an abstract task, 
I will make some assumptions for the properties of the service. 

#### Assumptions
- Application security is out of this context, this will be a service that'll be called internally with no public API. But still I would 
secure this in a production environment since payments are involved.
- Invoice table can grow quickly if customer base grows fast, so We should be ready to scale
- We should be extremely careful about fault tolerance since charges involved.

 Considering these items, we need to focus on two properties for the service; scalability and reliability. Let's focus on them ⬇️

### Scalability

- Service should be able to scale horizontally when needed, so payment of the invoices should be done in parallel. Also invoice payments are not something that
should be done synchronously, user is not expecting a response, it's a batch process. We should go for asynchronous messaging.
- If we have a message broker, we can have multiple consumers that'll make the payments in parallel.

### Reliability

- Service should be reactive to pre-defined exceptions and unexpected errors. By using a message broker, messages with unexpected errors can be replayed or end up in DLQ
for further investigation. 

Asynchronous messaging creates two sub problems. We need a balance between producing and consuming messages so there is no bottleneck. Most of the time message production
is way faster than consumption. So one leader instance that produces the messages can be enough. If producing messages become a bottleneck in the future, database 
pagination can be introduced for multiple service instances.

So let's decide how we can move forward in actual implementation;

- We'll need a lightweight message broker and add it to our Docker setup. I normally use SNS/SQS from AWS, but for the sake of this project, RabbitMQ is ready
to handle more than we need.
- We need to have a scheduler setup and a leadership election system, in order to not have competing schedulers. 
I need to look for a scheduler library that supports distributed environment for Kotlin, maybe.
>Edit: Found this one -> https://github.com/justwrote/kjob - Lightweight and Supports multiple instances
- Do we need any database indexes? I would argue since this is just batch processing. 
- Why do we have a rest api for this service? That would help only maintenance purposes.
I wouldn't have a batch processing logic and public facing API share the same memory and CPU. If we are in a microservices environment,
we should separate this service, rest api could help manual invoice fixes only, maybe. Invoice table should be populated with messaging.

So; overall our architecture would look like this ⬇️

![alt text](diagrams/propsed_plan.jpg)

### Further potential Improvements
- A retry mechanism to the DLQ can be introduced


I maybe wasn't able to apply best practies regarding Kotlin and Gradle. I've been using Spring and Maven 
for a while and need to sharpen my skills :)