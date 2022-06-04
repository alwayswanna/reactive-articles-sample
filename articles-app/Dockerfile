FROM openjdk:17.0
COPY target/article-app-*.jar article-app.jar
EXPOSE 8085
CMD java $JAVA_MEM $JAVA_EXT -jar /article-app.jar