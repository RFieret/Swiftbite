# Gebruik een Gradle-image met Java 17
FROM gradle:8.5-jdk21 AS test-runner

# Maak een app-map
WORKDIR /app

# Kopieer alle bestanden naar de container
COPY . .

# Optioneel: build dependencies vooraf, zonder tests uit te voeren
RUN gradle build -x test

# Standaardcommando: voer de tests uit (bij runtime, niet bij build!)
CMD ["gradle", "test"]