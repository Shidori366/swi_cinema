package cz.swi;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import cz.swi.configuration.AppConfig;
import cz.swi.shared.dto.CreateReservationRequestDto;
import cz.swi.shared.dto.ReservationDto;
import cz.swi.shared.dto.ScreeningDto;
import cz.swi.shared.dto.SeatDto;
import cz.swi.shared.enums.ReservationStatus;
import cz.swi.utils.HttpClientWrapper;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Main {

    private static String currentEmail;

    public static void main(String[] args) {
        try {
            Screen screen = new TerminalScreen(
                    new DefaultTerminalFactory().createTerminal()
            );

            screen.startScreen();
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

            showLogin(gui);
            screen.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showLogin(MultiWindowTextGUI gui) {
        BasicWindow window = new BasicWindow("Login");

        Panel panel = new Panel();
        panel.setLayoutManager(
                new LinearLayout(Direction.VERTICAL)
        );

        panel.addComponent(
                new Label("Enter your email:")
        );

        TextBox emailBox = new TextBox();
        panel.addComponent(emailBox);

        panel.addComponent(new EmptySpace());

        panel.addComponent(
                new Button("Login", () -> {
                    String email = emailBox.getText().trim();

                    if (email.isEmpty()) {
                        showError(
                                gui,
                                "Email cannot be empty."
                        );
                        return;
                    }

                    currentEmail = email;

                    window.close();

                    showMainMenu(gui);
                })
        );

        panel.addComponent(
                new Button("Exit", window::close)
        );

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void showMainMenu(MultiWindowTextGUI gui) {
        BasicWindow window = new BasicWindow("Cinema");
        Panel panel = new Panel();

        panel.setLayoutManager(
                new LinearLayout(Direction.VERTICAL)
        );

        panel.addComponent(
                new Label("Cinema System")
        );

        panel.addComponent(
                new Label(
                        "Logged in as: " + currentEmail
                )
        );

        panel.addComponent(new EmptySpace());

        panel.addComponent(
                new Button(
                        "1. Screenings",
                        () -> showScreenings(gui)
                )
        );

        panel.addComponent(
                new Button(
                        "2. Reservations",
                        () -> showReservations(gui)
                )
        );

        panel.addComponent(
                new Button(
                        "3. Exit",
                        window::close
                )
        );

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void showScreenings(MultiWindowTextGUI gui) {
        BasicWindow window = new BasicWindow("Screenings");

        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label("Available screenings"));
        panel.addComponent(new EmptySpace());

        try {
            ScreeningDto[] screenings = HttpClientWrapper.getRequest(
                    AppConfig.getBaseUrl() + "screenings",
                    Map.of(),
                    ScreeningDto[].class
            );

            for (ScreeningDto screening : screenings) {
                panel.addComponent(new Button(
                        screening.movie().name()
                                + " | "
                                + screening.time()
                                + " | Room "
                                + screening.roomId(),
                        () -> showSeats(gui, screening)
                ));
            }

        } catch (Exception e) {
            panel.addComponent(new Label(
                    "Failed to load screenings: " + e.getMessage()
            ));
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Back", window::close));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void showReservations(MultiWindowTextGUI gui) {
        BasicWindow window = new BasicWindow("My Reservations");

        Panel panel = new Panel();
        panel.setLayoutManager(
                new LinearLayout(Direction.VERTICAL)
        );

        panel.addComponent(
                new Label("Reservations for: " + currentEmail)
        );

        panel.addComponent(new EmptySpace());

        try {
            ReservationDto[] reservations =
                    HttpClientWrapper.getRequest(
                            AppConfig.getBaseUrl() + "reservations",
                            Map.of(
                                    "email",
                                    currentEmail
                            ),
                            ReservationDto[].class
                    );

            if (reservations.length == 0) {
                panel.addComponent(
                        new Label("No reservations found.")
                );
            }

            for (ReservationDto reservation : reservations) {
                panel.addComponent(
                        new Button(
                                "Reservation #" + reservation.id()
                                        + " | "
                                        + reservation.status(),
                                () -> {
                                    boolean changed = showReservationDetails(gui, reservation);

                                    if (changed) {
                                        window.close();
                                        showReservations(gui);
                                    }
                                }
                        )
                );
            }

        } catch (Exception e) {
            panel.addComponent(
                    new Label(
                            "Failed to load reservations: "
                                    + e.getMessage()
                    )
            );
        }

        panel.addComponent(
                new Button("Back", window::close)
        );

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static boolean showReservationDetails(MultiWindowTextGUI gui, ReservationDto reservation) {
        BasicWindow window = new BasicWindow("Reservation #" + reservation.id());

        AtomicBoolean changed = new AtomicBoolean(false);

        Panel panel = new Panel();
        panel.setLayoutManager(
                new LinearLayout(Direction.VERTICAL)
        );

        panel.addComponent(
                new Label("Screening: " + reservation.screeningId())
        );

        panel.addComponent(
                new Label("Seats: " + reservation.seatIds())
        );

        panel.addComponent(
                new Label("Email: " + reservation.contactEmail())
        );

        panel.addComponent(
                new Label("Status: " + reservation.status())
        );

        panel.addComponent(new EmptySpace());

        if (reservation.status() == ReservationStatus.PENDING) {
            panel.addComponent(
                    new Button("Pay", () -> {

                        boolean paid =
                                showPaymentForm(gui, reservation);

                        if (paid) {
                            changed.set(true);
                            window.close();
                        }
                    })
            );
        }

        panel.addComponent(
                new Button("Back", window::close)
        );

        window.setComponent(panel);
        gui.addWindowAndWait(window);

        return changed.get();
    }

    private static void showSeats(MultiWindowTextGUI gui, ScreeningDto screening) {
        BasicWindow window = new BasicWindow("Select Seats");

        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label(screening.movie().name()));
        panel.addComponent(new Label("Room " + screening.roomId()));
        panel.addComponent(new EmptySpace());

        List<Long> selectedSeats = new ArrayList<>();

        try {
            SeatDto[] allSeats = HttpClientWrapper.getRequest(
                    AppConfig.getBaseUrl()
                            + "screenings/"
                            + screening.id()
                            + "/seats",
                    Map.of(),
                    SeatDto[].class
            );

            SeatDto[] availableSeats = HttpClientWrapper.getRequest(
                    AppConfig.getBaseUrl()
                            + "screenings/"
                            + screening.id()
                            + "/seats/available",
                    Map.of(),
                    SeatDto[].class
            );

            Set<Long> availableIds = Arrays.stream(availableSeats)
                    .map(SeatDto::id)
                    .collect(Collectors.toSet());

            panel.addComponent(new Label("        SCREEN"));
            panel.addComponent(new EmptySpace());

            Panel seatPanel = new Panel();
            seatPanel.setLayoutManager(new GridLayout(5));

            for (SeatDto seat : allSeats) {
                if (availableIds.contains(seat.id())) {
                    Button seatButton = new Button(
                            "[" + seat.id() + "]"
                    );

                    seatButton.addListener(button -> {
                        if (selectedSeats.contains(seat.id())) {
                            selectedSeats.remove(seat.id());

                            seatButton.setLabel(
                                    "[" + seat.id() + "]"
                            );
                        } else {
                            selectedSeats.add(seat.id());

                            seatButton.setLabel(
                                    "[*" + seat.id() + "*]"
                            );
                        }
                    });

                    seatPanel.addComponent(seatButton);

                } else {
                    seatPanel.addComponent(
                            new Label("[X]")
                    );
                }
            }

            panel.addComponent(seatPanel);

            panel.addComponent(new EmptySpace());

            panel.addComponent(
                    new Label("[X] Taken   [*] Selected")
            );

            panel.addComponent(new EmptySpace());

            panel.addComponent(new Button("Continue", () -> {
                if (selectedSeats.isEmpty()) {
                    showError(gui, "Select at least one seat.");
                    return;
                }

                window.close();
                createReservation(gui, screening.id(), selectedSeats);
            }));

        } catch (Exception e) {
            panel.addComponent(
                    new Label("Failed to load seats: " + e.getMessage())
            );
        }

        panel.addComponent(new Button("Back", window::close));

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static void createReservation(MultiWindowTextGUI gui, Long screeningId, List<Long> seatIds) {
        try {
            CreateReservationRequestDto request =
                    new CreateReservationRequestDto(
                            screeningId,
                            seatIds,
                            currentEmail
                    );

            ReservationDto reservation =
                    HttpClientWrapper.postRequest(
                            AppConfig.getBaseUrl() + "reservations",
                            Map.of(),
                            request,
                            ReservationDto.class
                    );

            showReservationCreated(
                    gui,
                    reservation
            );

        } catch (Exception e) {
            showError(
                    gui,
                    e.getMessage()
            );
        }
    }

    private static void showReservationCreated(MultiWindowTextGUI gui, ReservationDto reservation) {
        BasicWindow window =
                new BasicWindow("Reservation created");

        Panel panel = new Panel();
        panel.setLayoutManager(
                new LinearLayout(Direction.VERTICAL)
        );

        panel.addComponent(
                new Label(
                        "Reservation ID: "
                                + reservation.id()
                )
        );

        panel.addComponent(
                new Label(
                        "Status: "
                                + reservation.status()
                )
        );

        panel.addComponent(
                new Label(
                        "Seats: "
                                + reservation.seatIds()
                )
        );

        panel.addComponent(
                new Label(
                        "Email: "
                                + reservation.contactEmail()
                )
        );

        panel.addComponent(new EmptySpace());

        panel.addComponent(
                new Button("Continue to payment", () -> {
                    window.close();
                    showPaymentForm(gui, reservation);
                })
        );

        panel.addComponent(
                new Button("Close", window::close)
        );

        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }

    private static boolean showPaymentForm(MultiWindowTextGUI gui, ReservationDto reservation) {
        BasicWindow window = new BasicWindow("Payment");

        AtomicBoolean paymentSuccessful = new AtomicBoolean(false);

        Panel panel = new Panel();
        panel.setLayoutManager(
                new LinearLayout(Direction.VERTICAL)
        );

        panel.addComponent(
                new Label("Reservation #" + reservation.id())
        );

        panel.addComponent(
                new Label("Seats: " + reservation.seatIds())
        );

        panel.addComponent(new EmptySpace());

        panel.addComponent(new Label("Card holder:"));
        TextBox cardHolderBox = new TextBox();
        panel.addComponent(cardHolderBox);

        panel.addComponent(new Label("Card number:"));
        TextBox cardNumberBox = new TextBox();
        panel.addComponent(cardNumberBox);

        panel.addComponent(new Label("Expiry date:"));
        TextBox expiryBox = new TextBox();
        panel.addComponent(expiryBox);

        panel.addComponent(new Label("CVV:"));
        TextBox cvvBox = new TextBox();
        panel.addComponent(cvvBox);

        panel.addComponent(new EmptySpace());

        panel.addComponent(
                new Button("Pay", () -> {

                    if (cardHolderBox.getText().isBlank()
                            || cardNumberBox.getText().isBlank()
                            || expiryBox.getText().isBlank()
                            || cvvBox.getText().isBlank()) {

                        showError(
                                gui,
                                "Fill in all payment details."
                        );

                        return;
                    }

                    try {
                        ReservationDto confirmed =
                                HttpClientWrapper.postRequestNoBody(
                                        AppConfig.getBaseUrl()
                                                + "reservations/"
                                                + reservation.id()
                                                + "/confirm",
                                        Map.of(),
                                        ReservationDto.class
                                );

                        paymentSuccessful.set(true);

                        window.close();

                        new MessageDialogBuilder()
                                .setTitle("Payment successful")
                                .setText(
                                        "Reservation #"
                                                + confirmed.id()
                                                + " confirmed."
                                )
                                .addButton(MessageDialogButton.OK)
                                .build()
                                .showDialog(gui);

                    } catch (Exception e) {
                        showError(gui, e.getMessage());
                    }
                })
        );

        panel.addComponent(
                new Button("Back", window::close)
        );

        window.setComponent(panel);
        gui.addWindowAndWait(window);

        return paymentSuccessful.get();
    }

    private static void confirmReservation(MultiWindowTextGUI gui, ReservationDto reservation, BasicWindow paymentWindow) {
        try {
            ReservationDto confirmed =
                    HttpClientWrapper.postRequestNoBody(
                            AppConfig.getBaseUrl()
                                    + "reservations/"
                                    + reservation.id()
                                    + "/confirm",
                            Map.of(),
                            ReservationDto.class
                    );

            paymentWindow.close();

            new MessageDialogBuilder()
                    .setTitle("Payment successful")
                    .setText(
                            "Reservation #"
                                    + confirmed.id()
                                    + " confirmed.\nStatus: "
                                    + confirmed.status()
                    )
                    .addButton(
                            MessageDialogButton.OK
                    )
                    .build()
                    .showDialog(gui);

        } catch (Exception e) {
            showError(
                    gui,
                    e.getMessage()
            );
        }
    }

    private static void showError(MultiWindowTextGUI gui, String message) {
        new MessageDialogBuilder()
                .setTitle("Error")
                .setText(message)
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(gui);
    }
}
