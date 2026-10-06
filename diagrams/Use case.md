# Use case 

![Use case diagram](Use%20case.svg)

---

## View Screenings

**Actor:**

- User

**Goal:** Show the user a list of available screenings.

### Main scenario

1. The user requests the list of screenings.
2. The system loads the screenings.
3. The system displays the list of screenings to the user.

---

## View Reservations

**Actor:**

- User

**Goal:** Show the user a list of their reservations.

**Preconditions:**

- The user is logged in.

### Main scenario

1. The user requests the list of their reservations.
2. The system loads the reservations.
3. The system displays the list of reservations to the user.

---

## Create Reservation

**Actor:**

- User

**Goal:** Reserve a seat.

**Preconditions:**

- The user is logged in.

### Main scenario

1. The user selects a screening.
2. The user selects a seat.
3. The system verifies that the seat is `FREE`.
4. The system sets the seat to `PENDING`.
5. The system displays the reservation result to the user.

### Alternative scenarios

**3a. The seat has been `PENDING` for more than 5 minutes**

1. The system sets the seat to `PENDING` again for the new user.
2. The system resets the timer.
3. Continue with step 5.

**3b. The seat has been `PENDING` for less than 5 minutes**

1. The system rejects the reservation.
2. Continue with step 5.

**3c. The seat is `RESERVED`**

1. The system rejects the reservation.
2. Continue with step 5.

---

## Pay Reservation

**Actor:**

- User

**Goal:** Pay for a reservation so that the seat is confirmed.

**Preconditions:**

- The user is logged in.

### Main scenario

1. The user selects a reservation.
2. The user requests payment.
3. The system verifies that the reservation exists.
4. The system verifies that the reservation is in the `PENDING` state.
5. The system verifies that the reservation has not expired.
6. The system processes the payment through an external payment gateway.
7. The payment succeeds and the system confirms the reservation.
8. The system sets the seat to `RESERVED`.
9. The system displays the payment result to the user.

### Alternative scenarios

**3a. The reservation does not exist**

1. The system rejects the payment.
2. Continue with step 9.

**4a. The reservation is not in the `PENDING` state**

1. The system rejects the payment.
2. Continue with step 9.

**5a. The reservation has expired**

1. The system cancels the reservation.
2. The system sets the seat to `FREE`.
3. The system rejects the payment.
4. Continue with step 9.

**7a. The payment failed**

1. The system rejects the payment.
2. Continue with step 9.

---

## Cancel Reservation

**Actor:**

- User

**Goal:** Cancel an existing reservation.

**Preconditions:**

- The user is logged in.

### Main scenario

1. The user selects a reservation.
2. The user requests cancellation of the reservation.
3. The system verifies that the reservation exists.
4. The system verifies that the reservation can be cancelled.
5. The system cancels the reservation.
6. The system sets the seat to `FREE`.
7. The system displays the cancellation result to the user.

### Alternative scenarios

**3a. The reservation does not exist**

1. The system rejects the cancellation.
2. Continue with step 7.

**4a. The reservation cannot be cancelled**

1. The system rejects the cancellation.
2. Continue with step 7.

---

## Manage Reservations

**Actor:**

- Admin

**Goal:** Allow the admin to view reservations and cancel them.

### Main scenario

1. The admin requests the list of reservations.
2. The system loads the reservations.
3. The system displays the list of reservations to the admin.
4. The admin selects a reservation.
5. The admin requests cancellation of the reservation.
6. The system verifies that the reservation exists.
7. The system cancels the reservation.
8. The system sets the seat to `FREE`.
9. The system displays the cancellation result to the admin.

### Alternative scenarios

**6a. The reservation does not exist**

1. The system rejects the cancellation.
2. Continue with step 9.
