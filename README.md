# Data GPT
Using OpenAI / GPT-3 to crunch data

## What is this?
Using natural language processing, this project aims to make data analysis easier.
It is a work in progress, and is currently in the early stages of development.

## Building
To build this project, you will need to have the following installed:
- Java JDK 11

Building is done using Gradle. To build, run the following command:
```bash
./gradlew build
```

## Running
To run this project, you will need to have the following installed:
- Docker Compose

Running is done using Docker Compose. To run, run the following command:
```bash
docker-compose up --build
```

Then submit a POST request to `http://localhost:8080/answer` with the following JSON body, replacing the question with your own:
```json
{
  "question": "Which film category was the most popular?"
}
```
Example using cURL:
```bash
curl -s -X POST \
  http://localhost:8080/answer \
  -H 'Content-Type: application/json' \
  -d '{ "question": "Which film category was the most popular?" }' | jq
```
Which will return a response similar to the following:
```json
{
  "question": "Which film category was the most popular?",
  "answer": "Sports was the most popular film category, with a count of 74.",
  "result": [
    {
      "category": "Sports",
      "count": 74
    }
  ],
  "bestQuery": "\nSELECT c.name AS category, COUNT(fc.category_id) AS count\nFROM category c\nINNER JOIN film_category fc\n    ON c.category_id = fc.category_id\nGROUP BY c.name\nORDER BY count DESC\nLIMIT 1;"
}
```

At the moment, the only supported tables are `category` and `film`.