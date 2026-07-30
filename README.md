BACKEND

Generate the target folder by running the following command:

```bash
mvn clean install -DskipTests
```

After generating the target folder with the SNAPSHOT build, start Docker by running:

```bash
docker-compose up --build
```

FRONT-END

Run the following command:

```bash
ng serve
```

After starting both the backend and frontend, access:

http://localhost:4200/

You can log in using:

User:

```
funcionario
```

Password:

```
senha123
```

or:

User:

```
chefe
```

Password:

```
senha123
```
