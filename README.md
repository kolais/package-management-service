# Product Package Service

The following repository contains a sample backend RESTful web service for managing packages consisting of one or more
products.

## How to run it locally

In order to run the service locally please clone this Git repository or
download [an archive](https://github.com/kolais/package-management-service/archive/refs/heads/main.zip).

Assuming you have a properly installed and configured JDK 17 or newer, please run the following from the root directory
of the repository (or an expanded ZIP archive you've just downloaded):

for macOS/Linux:

```shell
PRODUCTSERVICE_API_AUTH_USERNAME=<username> PRODUCTSERVICE_API_AUTH_PASSWORD=<password> ./gradlew bootRun
```

_(where `<username` and `password` - known values for product service API authetication)_

Product Package service is accessible by default at https://localhost:8080 from the host where service is started.
If the port 8080 is already in use, it is possible to configure service to bind to a different port by
specifying port number in run command line (`<port>` is a desired port number in this example):

```shell
SERVER_PORT=<port> PRODUCTSERVICE_API_AUTH_USERNAME=<username> PRODUCTSERVICE_API_AUTH_PASSWORD=<password> ./gradlew bootRun
```

Alternatively, it's possible to put the desired port number in a file `src/main/resources/application.yml` in a `server`
section:

```yaml
server:
  port: <port>
```

## API Documentation

Product Package service implements five API endpoints:

```text
POST   /api/v1/packages      - creates a new package of products
GET    /api/v1/packages/<id> - read package data for package with ID <id>
PUT    /api/v1/packages/<id> - updates package data for package with ID <id>
DELETE /api/v1/packages/<id> - deleted a package with ID <id>

GET    /api/v1/packages      - lists all packages currently stored in service memory
```

`Create` and `update` endpoints accept JSON body with the following structure:

```json
{
  "name": "Package Name",
  "description": "Package Description",
  "products": [
    {
      "id": "VqKb4tyj9V6i",
      "quantity": 5
    }
  ]
}
```

_Please note: field name is mandatory non-empty string, also at least one product must be defined._

`Create`, `read` and `update` endpoints return the JSON object with the following structure:

```json
{
  "id": "KQvUjOirjpr5",
  "name": "Package Name",
  "description": "Package Description",
  "products": [
    {
      "id": "VqKb4tyj9V6i",
      "name": "Shield",
      "quantity": 5,
      "price": 5745.00,
      "currency": "USD"
    }
  ],
  "price": 5745.00,
  "currency": "USD"
}
```

`List all` endpoint returns an array of these JSON objects.

It is possible to see package and individual product price information in different currency using latest conversion
rates fetched at https://frankfurter.app/. In order to do so please add query string `?currency=<code>` to an endpoint
that
returns package data. At present, the following currency codes are supported:

```json
{
  "AUD": "Australian Dollar",
  "BGN": "Bulgarian Lev",
  "BRL": "Brazilian Real",
  "CAD": "Canadian Dollar",
  "CHF": "Swiss Franc",
  "CNY": "Chinese Renminbi Yuan",
  "CZK": "Czech Koruna",
  "DKK": "Danish Krone",
  "EUR": "Euro",
  "GBP": "British Pound",
  "HKD": "Hong Kong Dollar",
  "HUF": "Hungarian Forint",
  "IDR": "Indonesian Rupiah",
  "ILS": "Israeli New Sheqel",
  "INR": "Indian Rupee",
  "ISK": "Icelandic Króna",
  "JPY": "Japanese Yen",
  "KRW": "South Korean Won",
  "MXN": "Mexican Peso",
  "MYR": "Malaysian Ringgit",
  "NOK": "Norwegian Krone",
  "NZD": "New Zealand Dollar",
  "PHP": "Philippine Peso",
  "PLN": "Polish Złoty",
  "RON": "Romanian Leu",
  "SEK": "Swedish Krona",
  "SGD": "Singapore Dollar",
  "THB": "Thai Baht",
  "TRY": "Turkish Lira",
  "USD": "United States Dollar",
  "ZAR": "South African Rand"
}
```

For more information please check out Swagger UI for the service - it is available
at http://localhost:8080/swagger-ui/index.html (service needs to run in
order to serve this page).

## Considerations, limitations and omissions

1. At the time of this writing, there is no package data persistence implemented. It is possible to refactor code by
   extracting public methods of `PackageRepository` into an interface and subsequently create an implementation that
   would store packages in a RDBMS or NoSQL DB.
2. `List all` endpoint returns all available packages which is obviously not very scalable.
3. Currency conversion and product service lookups are cached in order to improve performance and also to limit rate of
   repeat requests. Cache parameters are defined in `src/main/resources/application.yml`:

```yaml
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=50,expireAfterAccess=10m
```

4. Unit test coverage is rather non-existent for non-controller code. Only "happy path" is tested at present.
5. There are no instructions for Windows users on how to run Package Management service locally for which I apologise.
   Sadly I have no means to test this on Windows, and therefore I refrained from publishing unverified instructions.
