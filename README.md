# 🐸 reactive-articles-sample 🐸


Group of microservices that has communication using RabbitMQ, as well as authorization according to the OAuth2 standard.

-----

## article-checker

Checks for the presence of exception words in articles. The list of exception words is defined in application.yaml.
(Does not have any web layer.)
</br>
URI: http://127.0.0.1:8090 

-----
## article-common

Common module with models.

-----
## article-app

Implements an API for creating, modifying, and deleting articles.
</br>
URI: http://127.0.0.1:8085/swagger-ui.html

-----
## oauth2-manager

Implements a user management mechanism on the authorization server.
</br>
URI: http://127.0.0.1:8077/swagger-ui.html

-----
## oauth-security-server

User authorization server, issuer of JWT tokens.
</br>
URI: http://localhost:9001/

