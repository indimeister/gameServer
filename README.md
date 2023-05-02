# REST Example

This repository includes a basic REST API built with h2 and SpringBoot framework and for demonstration purposes NIMGame.

## Run and Test

To run the application type

```
mvn spring-boot:run
```

To execute unit and acceptance tests


```
mvn test
```

For unit tests and acceptance tests JUnit and REST Assured frameworks are used.

## Endpoints

**Important:** `Content-Type: application/json` header must be present to use API.

The most common HTTP status codes are returned when there is an error.

### Add a Game
Request
```
/api/play [POST]
Content-Type: application/json
```
When succeeed 200 Status code and created game object are returned.

Response Example
```
{
    "id": 1,
    "heapSize": 13,
    "maxMatches": 3,
    "numberMatch": 1,
    "playerTurn": false,
    "over": false,
    "winner": "NONE"
}
```

### Get all Game

```
/api/play [GET]
```

Gets all games with given id.

###  Get a Game

```
/api/play/{id} [GET]
```
Gets a game with given id.

### Update play
Request
```
/api/play/{id} [PATCH]
{
  "player": "PLAYER",
  "numMatchPlayer": 1
}

```
When succeeed 200 Status code and updated game object are returned.

Response Example
```
{
    "id": 1,
    "heapSize": 13,
    "maxMatches": 3,
    "numberMatch": 1,
    "playerTurn": false,
    "over": false,
    "winner": "PLAYER"
}
```

### Update play guess
Request
```
/api/play/guess/{id} [PATCH]
{
  "player": "PLAYER",
  "numberGuess": 10
}

```
When succeeed 200 Status code and updated game object are returned.

Response Example
```
{
    "id": 1,
    "heapSize": 13,
    "maxMatches": 3,
    "numberMatch": 1,
    "playerTurn": false,
    "over": false,
    "winner": "PLAYER"
}
```

###  Delete a Game

```
/api/play/{id} [DELETE]
```
Delete a game with given id.