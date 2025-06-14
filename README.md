# OOP.ASG4.Milestone2

## 📌 Overview

This is updated Milestone 2 of the OOP Endterm assignment — a Java-based RESTful API that connects to a PostgreSQL database and enables full CRUD operations for **Passengers**, **Flights**, and **Reservations**.

The goal was to design a lightweight HTTP server without external frameworks, demonstrating real-world usage of:
- Java core networking (`com.sun.net.httpserver`)
- JDBC for SQL operations
- JSON parsing with `org.json`

## 🛠 Tech Stack

| Layer           | Tech                              |
|----------------|-----------------------------------|
| Language        | Java                              |
| HTTP Server     | `com.sun.net.httpserver.HttpServer` |
| Database        | PostgreSQL (`airressys`)          |
| JSON            | `org.json:json-20231013.jar`      |
| Driver          | `org.postgresql:postgresql-42.7.5.jar` |

## 📂 Project Structure

```
OOP.ENDTERM.Project_Defence/
│
├── Main.java
├── PassengerAPI.java       # Main HTTP server
├── lib/
│   ├── json-20231013.jar
│   └── postgresql-42.7.5.jar
└── README.md

````

## 🧠 Entities & Endpoints

Each entity has its own endpoint and supports full CRUD:

### 🔹 `/passenger`
| Method | Description                    | Payload (JSON)                                      |
|--------|--------------------------------|-----------------------------------------------------|
| GET    | Get all passengers             | -                                                   |
| POST   | Add new passenger              | `name`, `passport_number`, `nationality`, `date_of_birth` |
| PUT    | Update passenger's name        | `id`, `name`                                        |
| DELETE | Delete passenger by ID         | `id` (as query param)                               |

### 🔹 `/flight`
| Method | Description                    | Payload (JSON)                                      |
|--------|--------------------------------|-----------------------------------------------------|
| GET    | Get all flights                | -                                                   |
| POST   | Add new flight                 | `flight_number`, `origin`, `destination`, `departure_time` |
| PUT    | Update flight info             | `id`, `flight_number`, `origin`, `destination`, `departure_time` |
| DELETE | Delete flight by ID            | `id` (as query param)                               |

### 🔹 `/reservation`
| Method | Description                    | Payload (JSON)                                      |
|--------|--------------------------------|-----------------------------------------------------|
| GET    | Get all reservations           | -                                                   |
| POST   | Add new reservation            | `passenger_id`, `flight_id`, `seat_number`          |
| PUT    | Update reservation             | `id`, `passenger_id`, `flight_id`, `seat_number`    |
| DELETE | Delete reservation by ID       | `id` (as query param)                               |

## 🧾 Database Schema

> **Database:** `airressys`

### Passenger Table
```sql
CREATE TABLE Passenger (
  id SERIAL PRIMARY KEY,
  name TEXT,
  passport_number TEXT,
  nationality TEXT,
  date_of_birth DATE
);
````

### Flight Table

```sql
CREATE TABLE Flight (
  id SERIAL PRIMARY KEY,
  flight_number TEXT,
  origin TEXT,
  destination TEXT,
  departure_time TEXT
);
```

### Reservation Table

```sql
CREATE TABLE Reservation (
  id SERIAL PRIMARY KEY,
  passenger_id INT REFERENCES Passenger(id),
  flight_id INT REFERENCES Flight(id),
  seat_number TEXT
);
```

## ▶️ How to Run

1. Make sure PostgreSQL is running, and you've created the `airressys` database with the 3 tables above.
2. Place required `.jar` libraries in the `/lib` directory.
3. Compile and run:

```bash
javac -cp "lib/*" PassengerAPI.java
java -cp ".:lib/*" PassengerAPI
```

4. Server will run at `http://localhost:8080`

## 🧪 Example Requests

### Add Passenger

```bash
curl -X POST http://localhost:8080/passenger \
-H "Content-Type: application/json" \
-d '{"name":"Tom Hardy", "passport_number":"B9901234", "nationality":"British", "date_of_birth":"1980-09-15"}'
```

### Get All Flights

```bash
curl http://localhost:8080/flight
```

## 🎓 Final Notes

This milestone delivers a fully functional REST server that can handle real-world CRUD interactions between passengers, flights, and reservations.
It showcases manual HTTP handling, SQL integrity, and clean object-structured logic using only Java standard libraries.

## 🧑‍💻 Author

GitHub: [MnstrsParago](https://github.com/MnstrsParago)
Telegram: [@ser_bauyr](https://t.me/ser_bauyr)
Instagram: [@bdnr05](https://www.instagram.com/bdnr05/)
