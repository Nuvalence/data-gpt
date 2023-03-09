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
  "answer": "Sports was the most popular film category, with 74 films.",
  "result": [
    {
      "category_name": "Sports",
      "num_films": 74
    }
  ],
  "bestQuery": "SELECT c.name AS category_name, COUNT(*) AS num_films\nFROM film_category fc\nJOIN category c\n  ON fc.category_id = c.category_id\nGROUP BY c.name\nORDER BY num_films DESC\nLIMIT 1;",
  "bestQueryExplanation": "The Postgres SQL query above is used to determine which film category was the most popular. This is done by joining the film_category and category tables to get the category names, and then doing a count of the number of films in each category. The result is then ordered by the number of films and limited to the top result. This query will return the most popular film category, as well as the number of films associated with it."
}
```

Or perhaps you want to know which actor had the largest change in film rentals in the database:
```json
{
  "question": "Which actor had the largest change year over year in film rentals between 2005 vs 2006?",
  "answer": "Gina DeGeneres had the largest change year over year in film rentals between 2005 vs 2006, with a difference of 731.",
  "result": [
    {
      "first_name": "GINA",
      "last_name": "DEGENERES",
      "difference": 731
    }
  ],
  "bestQuery": "\n\nSELECT actor.first_name, actor.last_name,\n\t(COUNT(CASE WHEN EXTRACT(YEAR FROM rental.rental_date) = 2005 THEN 1 ELSE NULL END) - \n\tCOUNT(CASE WHEN EXTRACT(YEAR FROM rental.rental_date) = 2006 THEN 1 ELSE NULL END)) AS difference\nFROM actor\nJOIN film_actor ON actor.actor_id = film_actor.actor_id\nJOIN film ON film_actor.film_id = film.film_id\nJOIN inventory ON film.film_id = inventory.film_id\nJOIN rental ON inventory.inventory_id = rental.inventory_id\nGROUP BY actor.actor_id, actor.first_name, actor.last_name\nORDER BY difference DESC\nLIMIT 1;",
  "bestQueryExplanation": "The above query counts how many film rentals each actor had in 2005 and 2006, and then subtracts the two numbers to find the difference in rentals between the two years. The query is then ordered in descending order by the difference in rentals and the top result is then returned. This allows us to find out which actor had the largest change year over year in film rentals between 2005 and 2006. \n\nThe query first selects the actor's first and last name from the actor table. It then joins the actor table to the film_actor table, the film table, the inventory table, and the rental table in order to get the rental data of each actor. \n\nThen, it uses a COUNT function to count how many rentals each actor had in 2005 and in 2006. Finally, the query subtracts the 2005 rentals from the 2006 rentals and orders the results in descending order. The top result is then returned."
}
```

You can also tailor the query explanation using different personas. For example, as a senior data analyst, you would use:
```bash
curl -s -X POST 'http://localhost:8080/answer' \
-H 'Content-Type: application/json' \
--data '{
    "question": "Find the top five actors who had the largest decrease year over year in film rentals between 2005 vs 2006.",
    "persona": "senior data analyst"
}'
```

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
