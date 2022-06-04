FROM openjdk:17.0
COPY target/article-checker-*.jar article-checker.jar
EXPOSE 8090
CMD java $JAVA_MEM $JAVA_EXT -jar /article-checker.jar