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

You will also need to create an account on [OpenAI](https://openai.com/) and create an [API key](https://platform.openai.com/account/api-keys).
Export the API key as an environment variable named `OPENAI_API_KEY`:
```bash
export OPENAI_API_KEY="sk-..."
```

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

At the moment, the only supported tables are `category`, `film`, `actor`, `inventory`, `rental` and `payment`.

## Data Preview
`category`:
```
+-----------+---------+--------------------------+
|category_id|name     |last_update               |
+-----------+---------+--------------------------+
|1          |Action   |2006-02-15 09:46:27.000000|
|2          |Animation|2006-02-15 09:46:27.000000|
|3          |Children |2006-02-15 09:46:27.000000|
+-----------+---------+--------------------------+
```

`film`:
```
+-------+----------------+----------------------------------------------------------------------------------------------------+------------+-----------+--------------------+---------------+-----------+------+----------------+------+--------------------------+
|film_id|title           |description                                                                                         |release_year|language_id|original_language_id|rental_duration|rental_rate|length|replacement_cost|rating|last_update               |
+-------+----------------+----------------------------------------------------------------------------------------------------+------------+-----------+--------------------+---------------+-----------+------+----------------+------+--------------------------+
|1      |ACADEMY DINOSAUR|A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies    |2006        |1          |null                |6              |0.99       |86    |20.99           |PG    |2007-09-10 17:46:03.905795|
|2      |ACE GOLDFINGER  |A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China|2006        |1          |null                |3              |4.99       |48    |12.99           |G     |2007-09-10 17:46:03.905795|
|3      |ADAPTATION HOLES|A Astounding Reflection of a Lumberjack And a Car who must Sink a Lumberjack in A Baloon Factory    |2006        |1          |null                |7              |2.99       |50    |18.99           |NC-17 |2007-09-10 17:46:03.905795|
+-------+----------------+----------------------------------------------------------------------------------------------------+------------+-----------+--------------------+---------------+-----------+------+----------------+------+--------------------------+
```

`actor`:
```
+--------+----------+---------+--------------------------+
|actor_id|first_name|last_name|last_update               |
+--------+----------+---------+--------------------------+
|1       |PENELOPE  |GUINESS  |2006-02-15 09:34:33.000000|
|2       |NICK      |WAHLBERG |2006-02-15 09:34:33.000000|
|3       |ED        |CHASE    |2006-02-15 09:34:33.000000|
+--------+----------+---------+--------------------------+
```

`inventory`:
```
+------------+-------+--------+--------------------------+
|inventory_id|film_id|store_id|last_update               |
+------------+-------+--------+--------------------------+
|1           |1      |1       |2006-02-15 10:09:17.000000|
|2           |1      |1       |2006-02-15 10:09:17.000000|
|3           |1      |1       |2006-02-15 10:09:17.000000|
+------------+-------+--------+--------------------------+
```

`rental`:
```
+---------+--------------------------+------------+-----------+--------------------------+--------+--------------------------+
|rental_id|rental_date               |inventory_id|customer_id|return_date               |staff_id|last_update               |
+---------+--------------------------+------------+-----------+--------------------------+--------+--------------------------+
|2        |2005-05-24 22:54:33.000000|1525        |459        |2005-05-28 19:40:33.000000|1       |2006-02-16 02:30:53.000000|
|3        |2005-05-24 23:03:39.000000|1711        |408        |2005-06-01 22:12:39.000000|1       |2006-02-16 02:30:53.000000|
|4        |2005-05-24 23:04:41.000000|2452        |333        |2005-06-03 01:43:41.000000|2       |2006-02-16 02:30:53.000000|
+---------+--------------------------+------------+-----------+--------------------------+--------+--------------------------+
```

`payment`:
```
+----------+-----------+--------+---------+------+--------------------------+
|payment_id|customer_id|staff_id|rental_id|amount|payment_date              |
+----------+-----------+--------+---------+------+--------------------------+
|16050     |269        |2       |7        |1.99  |2007-01-24 21:40:19.996577|
|16051     |269        |1       |98       |0.99  |2007-01-25 15:16:50.996577|
|16052     |269        |2       |678      |6.99  |2007-01-28 21:44:14.996577|
+----------+-----------+--------+---------+------+--------------------------+
```
