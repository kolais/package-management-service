# Product Package Service

The following repository contains a sample backend RESTful web service for managing packages consisting of one or more
products.

## How to run it locally

In order to run the service locally please check out this Git repository or download an archive.

Assuming you have a properly installed and configured JDK 17 or newer, please run the following from the root directory
of the repository:

macOS/Linux:

```shell
PRODUCTSERVICE_API_AUTH_USERNAME=<username> PRODUCTSERVICE_API_AUTH_PASSWORD=<password> ./gradlew bootRun
```

*(where `<username` and `password` - known values for product service API authetication)*
