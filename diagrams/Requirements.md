# Requirements

Use cases are described in [Use case.md](Use%20case.md).

---

## Functional requirements

| ID    | Requirement                                                                                                                  | Use case                                                 |
|-------|------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------|
| FR-01 | The system shall display a list of screenings to the user.                                                                   | [View Screenings](Use%20case.md#view-screenings)         |
| FR-02 | The system shall display the seats of a selected screening together with their state (`FREE`, `PENDING`, `RESERVED`).        | [Create Reservation](Use%20case.md#create-reservation)   |
| FR-03 | The system shall allow a logged-in user to reserve one or more seats for a selected screening.                               | [Create Reservation](Use%20case.md#create-reservation)   |
| FR-04 | The system shall set a newly reserved seat to `PENDING`.                                                                     | [Create Reservation](Use%20case.md#create-reservation)   |
| FR-05 | The system shall reject a reservation of a seat that is `RESERVED` or has been `PENDING` for less than 5 minutes.            | [Create Reservation](Use%20case.md#create-reservation)   |
| FR-06 | The system shall allow a seat that has been `PENDING` for more than 5 minutes to be reserved by another user.                | [Create Reservation](Use%20case.md#create-reservation)   |
| FR-07 | The system shall display a list of the logged-in user's reservations.                                                        | [View Reservations](Use%20case.md#view-reservations)     |
| FR-08 | The system shall allow the user to pay for a `PENDING` reservation that has not expired.                                     | [Pay Reservation](Use%20case.md#pay-reservation)         |
| FR-09 | After a successful payment, the system shall confirm the reservation and set its seats to `RESERVED`.                        | [Pay Reservation](Use%20case.md#pay-reservation)         |
| FR-10 | The system shall reject a payment for a reservation that does not exist, is not `PENDING` or has expired.                    | [Pay Reservation](Use%20case.md#pay-reservation)         |
| FR-11 | When a payment is attempted for an expired reservation, the system shall cancel the reservation and set its seats to `FREE`. | [Pay Reservation](Use%20case.md#pay-reservation)         |
| FR-12 | The system shall allow the user to cancel their reservation and set its seats to `FREE`.                                     | [Cancel Reservation](Use%20case.md#cancel-reservation)   |
| FR-13 | The system shall display a list of all reservations to the admin.                                                            | [Manage Reservations](Use%20case.md#manage-reservations) |
| FR-14 | The system shall allow the admin to cancel any reservation and set its seats to `FREE`.                                      | [Manage Reservations](Use%20case.md#manage-reservations) |
| FR-15 | The system shall display the result of every operation to the user, including the reason for a rejection.                    | All                                                      |

---

## Non-functional requirements

### Reliability

| ID     | Requirement                                                                                                                         |
|--------|-------------------------------------------------------------------------------------------------------------------------------------|
| NFR-01 | A seat must never be reserved by more than one reservation at a time, even when multiple users reserve the same seat concurrently.  |
| NFR-02 | An unpaid (`PENDING`) reservation shall hold its seats for 5 minutes. After that, the seats are released without any manual action. |

### Security

| ID     | Requirement                                                                                                                               |
|--------|-------------------------------------------------------------------------------------------------------------------------------------------|
| NFR-03 | The system shall identify the user through authentication, never through a value sent in the request (e.g. an email address).             |
| NFR-04 | A user shall only be able to view, pay for and cancel their own reservations.                                                             |
| NFR-05 | Admin functions shall only be accessible to users with the Admin role and shall be exposed separately from user functions (`/admin/...`). |
| NFR-06 | The system shall not process or store payment card data. Payments are handled by an external payment gateway.                             |

### Usability

| ID     | Requirement                                                                               |
|--------|-------------------------------------------------------------------------------------------|
| NFR-07 | For a `PENDING` reservation, the UI shall show the time at which the reservation expires. |
| NFR-08 | The seat selection shall visually distinguish available, selected and occupied seats.     |

### Constraints

| ID     | Requirement                                                                                                   |
|--------|---------------------------------------------------------------------------------------------------------------|
| NFR-09 | The system shall use a client–server architecture: a client communicates with the server through a REST API.  |
| NFR-10 | The server shall be implemented in Java with Spring Boot and shall use an SQLite database.                    |
| NFR-11 | All prices shall be in CZK.                                                                                   |
| NFR-12 | The client shall be a web application (Vue.js (typescript) single-page application) running in a web browser. |