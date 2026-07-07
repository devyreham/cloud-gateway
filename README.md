README

API Gateway to open access for external apps or add external data into EAAS applications
What is this repository for?
Version 4.x

Api Gateway built on Spring Cloud Gateway. Configurations files are yaml files located ./config folder and can be deployed outside the application.

    route external urls to internal services
    allow customization that are not available in standard reverse proxy or load balancer.
    can integrate with various external tools
    Services are filtered by IP in application.yml "allips" list
    External users or apps need an access token from Keycloak

Runs better behind a SSL reverse proxy like nginx or haproxy: map the public url to localhost:9000

Deployment

When redeploying cloud-gateway most backend services will be unreachable. If you worry about high availability deploy on each server one by one

TODO: This could be automatized
How do I get set up?
Build

Minimal gradle version 8.x, Java 17+

gradle clean build -x test

You can run locally with profile using, and test with curl/postman

gradle bootRun --args='--spring.profiles.active=dev'

Test

Use spring boot 3.5.x and Junit5
Contribution guidelines

Code guidelines: use google-checkstyle definitions for IDE
Who do I talk to?

Khresterion
Code quality and test coverage

Run junit test gradle test

Generate the reports gradle jacocoTestReport sonarqube -Penvname=dev
Sonarqube scan with gradle

Call gradle with envname paramater to target a specific branch gradle sonarqube -Penvname=dev

gradle sonarqube -Penvname=rec
Bitbucket

commit message is enforced: use a valid Jira issue

Container
===

Building image
--

- (optional) login to Khresterion private repository
```
docker login harbor.khresterion.com/docker-repo -u [gradle.properties user] -p [gradle.properties password]
```

```
docker build -t localhost/cloud-gateway:[tag version] .
```

tag version: can be the same version as build.gradle

this build a local image, not published in docker repository


Running for dev
--

-Always bind port to localhost to avoid security issues
-this build a local image, not published in docker repository

```
example for dev profile
docker run --rm -p 127.0.0.1:9000:9000 -v [path to workspace]/cloud-gateway/config:config -e "SPRING_PROFILES_ACTIVE=dev" localhost/cloud-gateway:[tag version]

config folder: load application.yml from external folder
```

Stopping container
--

```
docker container list --all
docker container stop [container id]
``
