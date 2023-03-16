# Data GPT
Using OpenAI / GPT-3 to crunch data

## What is this?
Using natural language processing, this project aims to make data analysis easier.
It is a work in progress, and is currently in the early stages of development.

## Building and Running
To build and run this project, you will need to have the following installed:
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
  -d '{ "question": "Which product category was trending in 2018?" }' | jq
```
Which will return a response similar to the following:
```json
{
  "question": "Which product category was trending in 2018?",
  "answer": "The apparel category was the top trending product in 2018 with a total amount sold of 726.",
  "result": [
    {
      "product_category": "Apparel",
      "total_amount_sold": 726
    }
  ],
  "bestQuery": "\n\nSELECT p.category AS \"product_category\", COUNT(op.amount) AS \"total_amount_sold\"\nFROM products p\nJOIN articles a ON p.id = a.productid\nJOIN order_positions op ON a.id = op.articleid\nJOIN \"order\" o ON op.orderid = o.id\nWHERE o.ordertimestamp BETWEEN '2018-01-01' AND '2018-12-31'\nGROUP BY p.category\nORDER BY \"total_amount_sold\" DESC\nLIMIT 1;",
  "bestQueryExplanation": "Explanation:\n\nThe query is selecting two columns - \"product_category\" and \"total_amount_sold\" - from four different tables - products, articles, order_positions, and order. \n\nFirst, the tables are joined together using the following relationships: products.id = articles.productid, articles.id = order_positions.articleid, and order_positions.orderid = \"order\".id. This allows us to connect the product category to the amount sold in each order. \n\nNext, the query filters the orders based on their order timestamp, only including orders that were made between January 1st, 2018 and December 31st, 2018. \n\nThe results are then grouped by product category using the GROUP BY clause, so that each product category has its own row in the output. \n\nFinally, the results are sorted in descending order by \"total_amount_sold\" and the LIMIT clause is used to only show the first row, which represents the product category with the highest amount sold in 2018."
}
```

You can also tailor the query explanation using different personas. For example, as a senior data analyst, you would use:
```bash
curl -s -X POST 'http://localhost:8080/answer' \
-H 'Content-Type: application/json' \
-d '{
    "question": "Find the top five actors who had the largest decrease year over year in film rentals between 2005 vs 2006.",
    "persona": "senior data analyst"
}' | jq
```
