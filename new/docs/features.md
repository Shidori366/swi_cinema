# Features

## Feature: Implement fallback when a screening is canceled
state: `New` Assigned to: `_`

### Task
Implement a fallback mechanism for situations where a movie screening is canceled.

Customers with an existing reservation should be notified about the cancellation via email. If alternative screenings are available, customers should be offered the possibility to transfer their reservation to another screening of the same movie or get a refund.

The system should handle situations where no alternative screening is available and provide the customer with clear information about their available options.

## Feature: Implement proper error messages for the user
state: `New` Assigned to: `_`

### Task
Improve the way application errors are communicated to the user. Currently, although the backend handles exceptions, the frontend displays only a generic Internal Server Error message in a popup.

Implement proper exception-to-error-message mapping and display meaningful, user-friendly error messages that clearly explain what went wrong and, where applicable, how the user can resolve the issue.

Improve the visual presentation of error messages by using appropriate UI components, such as inline validation messages, notification banners, or contextual error messages, instead of relying solely on generic popups.

Technical exception details should remain in backend logs and should not be exposed to the user.