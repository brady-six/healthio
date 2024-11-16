# HealthIO

## Overview

HealthIO is a RESTful API built on the Spring framework. It provides four main services:

1. Users can perform CRUD operations on Workout records.
2. Users can perform CRUD operations on Meal records.
3. Users can obtain generated advice based on their recent Workout records.
4. Users can obtain generated advice based on their recent Meal records.

Services 1 and 2 work as you might expect, with the exception that endpoints for resource-specific GET requests
do not exist. Instead, resource collections are retrievable via HATEOAS-compliant paginated responses.

Services 3 and 4 take advantage of Spring's AI module. Internally,
a prompt is generated and then sent to OpenAI's chat API. The prompt includes the
User's query with additional Meal or Workout context. These services are
most useful when accessed via GUI, so one was built to demonstrate them.

Below is an example of how a User might POST Meals and get feedback on their data.
Note that curl is being used simply to avoid publishing a large GIF. Meals can also be created through
the user interface. (Authorization headers are omitted to improve clarity)

```bash
curl -X POST http://localhost:8080/api/v1/meals \
    -H "Content-Type: application/json" \
    -d '{
        "date": "2024-11-15T10:00:00Z",
        "foods": [
            {"name": "Apple", "calories": 95},
            {"name": "Banana", "calories": 105}
        ]
    }'

curl -X POST http://localhost:8080/api/v1/meals \
    -H "Content-Type: application/json" \
    -d '{
        "date": "2024-11-15T12:00:00Z",
        "foods": [
            {"name": "Chicken Breast", "calories": 200},
            {"name": "Broccoli", "calories": 55}
        ]
    }'

curl -X POST http://localhost:8080/api/v1/meals \
    -H "Content-Type: application/json" \
    -d '{
        "date": "2024-11-15T18:30:00Z",
        "foods": [
            {"name": "Steak", "calories": 700},
            {"name": "Mashed Potatoes", "calories": 250},
            {"name": "Salad", "calories": 100}
        ]
    }'
```

Then, the User can gain insights about their recent Meals:
<br />
![](https://raw.githubusercontent.com/brady-six/healthio/580316dc8ac9c083efa4988a0ac3f8df9279598e/src/main/resources/meal-demo.gif)

This functionality is identical for Workout records.

## Technologies Used

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
* [Spring Security](https://spring.io/projects/spring-security)
* [Spring AI](https://spring.io/projects/spring-ai)
* [Lombok](https://projectlombok.org/)

## Learning Points & Do Different

This project furthered my understanding of several fundamental software engineering concepts in addition to more niche
and framework-specific topics. Here's a non-exhaustive list of some learning points and discoveries:

* Spring Data JPA supports query methods--methods that define a database query based on their name.
* Spring HATEOAS has support for page assembly.
* Introduction to JSR-303
* Introduction to Lombok
* How to serve static web content with Spring Boot
* Spring profiles, including how to define profile-specific application.yml config
* How to stream a response and handle data streaming on the client.

If I were to start over, here are a few things I would do differently:

* Adopt better TDD practices. My one major hiccup in this project was my testing setup.
  Part way through, I decided to include Spring Boot's oauth client starter to enable
  an authentication flow directly from my API instead of later developing a full client.
  Although this isn't problematic in isolation, it affected my current JWT Authentication
  strategy and led to a full reset of my testing setup.
* Expand my Workout and Meal entities. Making my data models more complex (e.g., adding macro data to Meals)
  would be an easy way to improve the quality of the AI advice.

## Next Steps

I plan to better my understanding of
the [Spring IoC container and Beans](https://docs.spring.io/spring-framework/reference/core/beans/introduction.html)
--specifically, how I can
manage existing beans in a clean manner.

## Contact

[Brady Six](https://www.linkedin.com/in/brady-six) - bsixdev@gmail.com