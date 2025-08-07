# Course Search Application

A Spring Boot application with Elasticsearch integration to provide course search functionality including full-text search, pagination, sorting etc

# Folder Structure

|-- project-root
	|-- elasticsearch
	    |-- docker-compose.yml

	|-- README.md

	|-- courseapp
	    |-- .idea
	    |-- courseapp
	    |-- .mvn
	    |-- src
                |-- main
	         |     |-- java
		|	|-- com.course_project.courseapp
		|	|     |-- config
		|	|     |	     |-- AppConfig
		|	|     |-- controller	
		|	|     |      |-- SearchController
		|	|     |-- dto
		|	|     |	     |-- CourseDocumentDto
		|	|     |	     |-- SearchResponse
		|	|     |-- entity
		|	|     |	     |-- CourseDocument
		|	|     |-- repository
		|	|     |      |-- CourseDocumentRepository
		|	|     |-- service
		|	|     |      |-- CourseDataLoader
		|	|     |-- CourseSearchService
		|	|     |	     |-- CourseSearchServiceImpl
		|	|     |-- CourseAppApplication
		|			 
	       |-- resources
		     |-- application
		     |-- sample-courses

					
🚀 Launch Elasticsearch
	To start Elasticsearch locally:

	1. Open terminal and navigate to the Elasticsearch Docker directory: cd elasticsearch/docker-compose
	2. Run Elasticsearch using Docker Compose: docker-compose up -d
	
	Verify that Elasticsearch is running:

	1. curl http://localhost:9200
	
	You should receive a JSON response confirming Elasticsearch is running on localhost:9200.

🧱 Build and Run Spring Boot Application
	
	Generate a Spring Boot project using Spring Initializer or include the following dependencies manually:

	1. Spring Web (for REST controllers)
	2. Spring Data Elasticsearch (or official Elasticsearch client)
	3. Lombok (to reduce boilerplate code)
	4. ModelMapper (for mapping between entities and DTOs)

	Steps to Build and Run

	Navigate to the Spring Boot project directory: cd courseapp/courseapp

	Build the application: ./mvnw clean package

	Run the application: ./mvnw spring-boot:run

⚙️ Application Configuration
	Add the following property inside application.properties (located in src/main/resources) to configure Elasticsearch connection: 	spring.elasticsearch.uris=http://localhost:9200

📦 Index Sample Data

	The application automatically reads a JSON file (sample-courses.json) containing 50+ course objects and indexes them into Elasticsearch upon startup.

	Trigger: Simply run the Spring Boot application.

	Verify: Open browser or use curl: curl http://localhost:9200/courses/_search?pretty

🔍 Testing & Verification of Search Controller

     The application exposes a search API at: GET /api/search

     Query Parameters
     Parameter:	              Description: 
```
  1. q	                      Keyword for full-text search (title, description)
  2. minAge/maxAge	      Filter by age range
  3. category	              Filter by course category (e.g., Math, Science)
  4. type	              Filter by type: ONE_TIME, COURSE, CLUB
  5. minPrice/maxPrice	      Filter by price range
  6. startDate	              Show courses on/after this date (ISO-8601 format)
  7. sort                     Sort by: upcoming (default), priceAsc, priceDesc
  8. page/size	              Pagination (page=0, size=10 by default)
```
Example 1: Full-text Search by Keyword

Request: curl "http://localhost:8080/api/search?q=robotics"

Expected Response:
```
{
  "total": 3,
  "courses": [
    {
      "id": 4,
      "title": "Robotics One-Time Workshop",
      "description": "Hands-on workshop building simple robots using kits.",
      "category": "Technology",
      "type": "ONE_TIME",
      "gradeRange": "6th-8th",
      "minAge": 11,
      "maxAge": 14,
      "price": 75.5,
      "nextSessionDate": "2025-06-20T10:00:00Z"
    }
  ],
  "error": null
}
```

Example 2: Filter by Category and Age Range, Sorted by Price Ascending

Request: curl "http://localhost:8080/api/search?category=Math&minAge=10&maxAge=15&sort=priceAsc&page=0&size=5"

Expected Response:
```
{
  "total": 3,
  "courses": [
    {
      "id": 21,
      "title": "Math Problem Solving Club",
      "description": "Enhance math skills through puzzles, competitions, and peer learning.",
      "category": "Math",
      "type": "CLUB",
      "gradeRange": "6th-9th",
      "minAge": 11,
      "maxAge": 15,
      "price": 50.0,
      "nextSessionDate": "2025-06-23T18:00:00Z"
    },
    ...
  ],
  "error": null
}
```
Example 3: Filter by Type and Price Range, Sorted by Price Descending

Request: curl "http://localhost:8080/api/search?type=ONE_TIME&minPrice=70&maxPrice=100&sort=priceDesc"

Expected Response:
```
{
  "total": 9,
  "courses": [
    {
      "id": 38,
      "title": "Photography Workshop",
      "description": "One-time intensive workshop on advanced digital photography techniques.",
      "category": "Art",
      "type": "ONE_TIME",
      "gradeRange": "9th-12th",
      "minAge": 14,
      "maxAge": 18,
      "price": 100.0,
      "nextSessionDate": "2025-06-29T13:00:00Z"
    },
    ...
  ],
  "error": null
}
```
Example 4: Start Date Filtering and Pagination

Request: curl "http://localhost:8080/api/search?category=Science&startDate=2025-07-01&page=1&size=3"

Expected Response :
```
{
  "total": 6,
  "courses": [
    {
      "id": 9,
      "title": "Physics Fundamentals",
      "description": "Explore the basics of physics including motion, forces, and energy.",
      "category": "Science",
      "type": "COURSE",
      "gradeRange": "8th-10th",
      "minAge": 13,
      "maxAge": 16,
      "price": 125.0,
      "nextSessionDate": "2025-07-08T14:00:00Z"
    },
    ...
  ],
  "error": null
}
```
🔍 Testing & Verification of Search Controller

     The application exposes a search API at: GET /api/search/suggest

     Query Parameters
     Parameter:	              Description:
```
  1. q	                      Text for AutoComplete(ex: Phy)
```
Example : Text Autocomplete

Request: curl "http://localhost:8080/api/search/suggest?q=Intro"

Expected Response : 
```
[
    "Intro to Environmental Studies",
    "Introduction to Algebra",
    "Introduction to Chemistry",
    "Introduction to Coding",
    "Introduction to Economics",
    "Introduction to Music Theory",
    "Introduction to Painting",
    "Introduction to Robotics"
]
```
Request: curl "http://localhost:8080/api/search/suggest?q=Robo"

Expected Response :
```
[
    "Robotics Club",
    "Robotics One-Time Workshop"
]
```
Request: curl "http://localhost:8080/api/search/suggest?q=Phy"

Expected Response :
```
[
    "Physics Fundamentals"
]
```
🔍 Testing & Verification of Fuzzy Functionality

     The application exposes a search API at: GET /api/search

     Query Parameters
     Parameter:	              Description:
```
  1. q	                      Keyword for fuzzy (title) or full-text search (title, description)
  2. minAge/maxAge	      Filter by age range
  3. category	              Filter by course category (e.g., Math, Science)
  4. type	              Filter by type: ONE_TIME, COURSE, CLUB
  5. minPrice/maxPrice	      Filter by price range
  6. startDate	              Show courses on/after this date (ISO-8601 format)
  7. sort                     Sort by: upcoming (default), priceAsc, priceDesc
  8. page/size	              Pagination (page=0, size=10 by default)
```

Example : Fuzzy Text

Request: curl "http://localhost:8080/api/search/?q=algbar"

Expected Response :
```
{
    "total": 1,
    "courses": [
      {
        "id": 1,
        "title": "Introduction to Algebra",
        "description": "Learn the basics of algebra including variables, equations, and functions.",
        "category": "Math",
        "type": "COURSE",
        "gradeRange": "4th-6th",
        "minAge": 9,
        "maxAge": 12,
        "price": 99.99,
        "nextSessionDate": "2025-06-10T15:00:00Z"
      }
    ],
    "error": null
}
```

Request: curl "http://localhost:8080/api/search/?q=crative"

Expected Response :
```
{
    "total": 5,
    "courses": [
      {
        "id": 33,
        "title": "Creative Science Experiments",
        "description": "Explore science through fun, hands-on experiments for young learners.",
        "category": "Science",
        "type": "COURSE",
        "gradeRange": "1st-4th",
        "minAge": 6,
        "maxAge": 9,
        "price": 85.0,
        "nextSessionDate": "2025-06-15T14:00:00Z"
     },
     ...
   ],
    "error": null
}
```



















