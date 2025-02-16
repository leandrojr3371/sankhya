# Usa a imagem do OpenJDK 17
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho no container
WORKDIR /app

# Copia o JAR gerado pelo Maven
COPY target/sankhya-0.0.1-SNAPSHOT.jar sankhya-api.jar

# Expor a porta 8080
EXPOSE 8080

# Comando para rodar o serviço
ENTRYPOINT ["java", "-jar", "sankhya-api.jar"]
