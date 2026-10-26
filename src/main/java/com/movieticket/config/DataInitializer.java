package com.movieticket.config;

import com.movieticket.entity.*;
import com.movieticket.repository.*;
import com.movieticket.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CinemaRepository cinemaRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;

    public DataInitializer(UserRepository userRepository,
                           MovieRepository movieRepository,
                           CinemaRepository cinemaRepository,
                           ScreenRepository screenRepository,
                           SeatRepository seatRepository,
                           ShowtimeRepository showtimeRepository,
                           BookingRepository bookingRepository,
                           BookingSeatRepository bookingSeatRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.cinemaRepository = cinemaRepository;
        this.screenRepository = screenRepository;
        this.seatRepository = seatRepository;
        this.showtimeRepository = showtimeRepository;
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0 && movieRepository.count() > 0) {
            System.out.println(">>> Database already initialized. Skipping sample data seed.");
            return;
        }

        System.out.println(">>> Initializing sample data for Movie Ticket Management System...");

        // 1. Create Default Users
        User adminUser = new User(
                "Cinema Master Admin",
                "admin@movieticket.com",
                UserService.hashPassword("admin123"),
                "+1 555-019-2831",
                "ROLE_ADMIN"
        );
        userRepository.save(adminUser);

        User regularUser = new User(
                "Alex Morgan",
                "user@movieticket.com",
                UserService.hashPassword("user123"),
                "+1 555-018-9922",
                "ROLE_USER"
        );
        userRepository.save(regularUser);

        // 2. Create Sample Movies
        List<Movie> movies = new ArrayList<>();

        movies.add(new Movie(
                "Dune: Part Two",
                "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family. Facing a choice between the love of his life and the fate of the known universe.",
                "Sci-Fi, Adventure, Action",
                "English",
                166,
                8.8,
                31400,
                LocalDate.now().minusDays(15),
                "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1600&auto=format&fit=crop&q=80",
                "https://www.youtube.com/watch?v=Way9Dexny3w",
                "Denis Villeneuve",
                "Timothée Chalamet, Zendaya, Rebecca Ferguson, Javier Bardem",
                "UA 16+",
                "NOW_SHOWING"
        ));

        movies.add(new Movie(
                "Oppenheimer",
                "The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb during World War II, confronting the existential question of catastrophic consequence.",
                "Biography, Drama, History",
                "English",
                180,
                8.9,
                48200,
                LocalDate.now().minusDays(30),
                "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1600&auto=format&fit=crop&q=80",
                "https://www.youtube.com/watch?v=uYPbbksJxIg",
                "Christopher Nolan",
                "Cillian Murphy, Emily Blunt, Matt Damon, Robert Downey Jr.",
                "R 18+",
                "NOW_SHOWING"
        ));

        movies.add(new Movie(
                "Spider-Man: Across the Spider-Verse",
                "Miles Morales catapults across the Multiverse, where he encounters a team of Spider-People charged with protecting its very existence. But when heroes clash on how to handle a new threat, Miles must redefine what it means to be a hero.",
                "Animation, Action, Adventure",
                "English",
                140,
                8.7,
                38900,
                LocalDate.now().minusDays(45),
                "https://images.unsplash.com/photo-1635805737707-575885ab0820?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1600&auto=format&fit=crop&q=80",
                "https://www.youtube.com/watch?v=cqGjhVJWtEg",
                "Joaquim Dos Santos, Kemp Powers",
                "Shameik Moore, Hailee Steinfeld, Oscar Isaac",
                "PG",
                "NOW_SHOWING"
        ));

        movies.add(new Movie(
                "Interstellar",
                "When Earth becomes uninhabitable in the future, a farmer and ex-NASA pilot, Joseph Cooper, is tasked to pilot a spacecraft, along with a team of researchers, to find a new planet for humans.",
                "Sci-Fi, Adventure, Drama",
                "English",
                169,
                8.7,
                65000,
                LocalDate.now().minusDays(60),
                "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=1600&auto=format&fit=crop&q=80",
                "https://www.youtube.com/watch?v=zSWdZVtXT7E",
                "Christopher Nolan",
                "Matthew McConaughey, Anne Hathaway, Jessica Chastain",
                "PG-13",
                "NOW_SHOWING"
        ));

        movies.add(new Movie(
                "The Batman",
                "When a sadistic serial killer begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.",
                "Action, Crime, Drama",
                "English",
                176,
                7.8,
                29800,
                LocalDate.now().minusDays(20),
                "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=1600&auto=format&fit=crop&q=80",
                "https://www.youtube.com/watch?v=mqqft2x_Aa4",
                "Matt Reeves",
                "Robert Pattinson, Zoë Kravitz, Jeffrey Wright, Colin Farrell",
                "UA 16+",
                "NOW_SHOWING"
        ));

        movies.add(new Movie(
                "Deadpool & Wolverine",
                "Wolverine is recovering from his injuries when he crosses paths with the loudmouth, unpredictable Deadpool. They team up to defeat a common enemy in an action-packed multiverse journey.",
                "Action, Comedy, Sci-Fi",
                "English",
                128,
                8.1,
                41200,
                LocalDate.now().minusDays(10),
                "https://images.unsplash.com/photo-1563089145-599997674d42?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=1600&auto=format&fit=crop&q=80",
                "https://www.youtube.com/watch?v=73_1biulkYk",
                "Shawn Levy",
                "Ryan Reynolds, Hugh Jackman, Emma Corrin",
                "A 18+",
                "NOW_SHOWING"
        ));

        movies.add(new Movie(
                "Gladiator II",
                "Years after witnessing the death of the revered hero Maximus at the hands of his uncle, Lucius must enter the Colosseum after his home is conquered by the tyrannical Emperors who now lead Rome.",
                "Action, Adventure, Drama",
                "English",
                148,
                8.4,
                1500,
                LocalDate.now().plusDays(25),
                "https://images.unsplash.com/photo-1533613220915-609f661a6fe1?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1600&auto=format&fit=crop&q=80",
                "https://www.youtube.com/watch?v=4rgYUipGJNo",
                "Ridley Scott",
                "Paul Mescal, Pedro Pascal, Denzel Washington",
                "UA 16+",
                "COMING_SOON"
        ));

        movies.add(new Movie(
                "Avatar: Fire and Ash",
                "Jake Sully and Neytiri travel to another region of Pandora, encountering the aggressive Ash People, a fiery Na'vi clan with unexpected motives and fierce culture.",
                "Action, Adventure, Sci-Fi",
                "English",
                192,
                9.1,
                980,
                LocalDate.now().plusDays(40),
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1600&auto=format&fit=crop&q=80",
                "https://www.youtube.com/watch?v=d9MyW72ELq0",
                "James Cameron",
                "Sam Worthington, Zoe Saldana, Sigourney Weaver",
                "UA 13+",
                "COMING_SOON"
        ));

        List<Movie> savedMovies = movieRepository.saveAll(movies);

        // 3. Create Sample Cinemas and Screens
        Cinema c1 = new Cinema(
                "PVR IMAX Grand Plaza",
                "Mumbai",
                "Ambience Mall, 4th Floor, Lower Parel",
                "+91 22 4920 8800",
                "IMAX Laser, Dolby Atmos 7.1, Luxe Recliners, Valet Parking"
        );
        Cinema c2 = new Cinema(
                "INOX Megaplex Nexus",
                "Bengaluru",
                "Nexus Mall, Koramangala 5th Block",
                "+91 80 4120 7711",
                "MX4D Dynamic, Insignia Gourmet Lounge, 4K Projection"
        );
        Cinema c3 = new Cinema(
                "Cinepolis VIP Downtown",
                "Delhi",
                "DLF Avenue, Saket District Centre",
                "+91 11 4050 6600",
                "VIP Leather Recliners, At-Seat Butler Service, Dolby Vision"
        );

        Cinema savedC1 = cinemaRepository.save(c1);
        Cinema savedC2 = cinemaRepository.save(c2);
        Cinema savedC3 = cinemaRepository.save(c3);

        // Screens
        Screen s1 = new Screen(savedC1, "Screen 1 - IMAX with Laser", "IMAX 3D", 8, 10);
        Screen s2 = new Screen(savedC1, "Screen 2 - Dolby Atmos", "Dolby Atmos", 6, 8);
        Screen s3 = new Screen(savedC2, "Screen 1 - 4DX Experience", "4DX", 6, 8);
        Screen s4 = new Screen(savedC3, "Screen 1 - VIP Club", "Standard 2D", 6, 8);

        Screen savedS1 = screenRepository.save(s1);
        Screen savedS2 = screenRepository.save(s2);
        Screen savedS3 = screenRepository.save(s3);
        Screen savedS4 = screenRepository.save(s4);

        // 4. Generate Seats for each screen
        generateSeatsForScreen(savedS1);
        generateSeatsForScreen(savedS2);
        generateSeatsForScreen(savedS3);
        generateSeatsForScreen(savedS4);

        // 5. Create Showtimes for the next 7 days
        LocalDate today = LocalDate.now();
        LocalTime[] slotTimes = {
                LocalTime.of(10, 30),
                LocalTime.of(14, 0),
                LocalTime.of(17, 45),
                LocalTime.of(21, 15)
        };

        List<Showtime> showtimesToSave = new ArrayList<>();

        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            LocalDate date = today.plusDays(dayOffset);

            // Movie 1: Dune in S1 & S3
            showtimesToSave.add(new Showtime(savedMovies.get(0), savedS1, date, slotTimes[0], slotTimes[0].plusMinutes(186), 350.0, "ACTIVE"));
            showtimesToSave.add(new Showtime(savedMovies.get(0), savedS1, date, slotTimes[2], slotTimes[2].plusMinutes(186), 380.0, "ACTIVE"));
            showtimesToSave.add(new Showtime(savedMovies.get(0), savedS3, date, slotTimes[1], slotTimes[1].plusMinutes(186), 320.0, "ACTIVE"));

            // Movie 2: Oppenheimer in S1 & S2
            showtimesToSave.add(new Showtime(savedMovies.get(1), savedS1, date, slotTimes[1], slotTimes[1].plusMinutes(200), 360.0, "ACTIVE"));
            showtimesToSave.add(new Showtime(savedMovies.get(1), savedS2, date, slotTimes[3], slotTimes[3].plusMinutes(200), 300.0, "ACTIVE"));

            // Movie 3: Spider-Man in S2 & S4
            showtimesToSave.add(new Showtime(savedMovies.get(2), savedS2, date, slotTimes[0], slotTimes[0].plusMinutes(160), 280.0, "ACTIVE"));
            showtimesToSave.add(new Showtime(savedMovies.get(2), savedS4, date, slotTimes[2], slotTimes[2].plusMinutes(160), 300.0, "ACTIVE"));

            // Movie 4: Interstellar in S1 & S4
            showtimesToSave.add(new Showtime(savedMovies.get(3), savedS1, date, slotTimes[3], slotTimes[3].plusMinutes(189), 350.0, "ACTIVE"));
            showtimesToSave.add(new Showtime(savedMovies.get(3), savedS4, date, slotTimes[1], slotTimes[1].plusMinutes(189), 300.0, "ACTIVE"));

            // Movie 5: The Batman in S2
            showtimesToSave.add(new Showtime(savedMovies.get(4), savedS2, date, slotTimes[1], slotTimes[1].plusMinutes(196), 260.0, "ACTIVE"));

            // Movie 6: Deadpool & Wolverine in S3
            showtimesToSave.add(new Showtime(savedMovies.get(5), savedS3, date, slotTimes[0], slotTimes[0].plusMinutes(148), 320.0, "ACTIVE"));
            showtimesToSave.add(new Showtime(savedMovies.get(5), savedS3, date, slotTimes[2], slotTimes[2].plusMinutes(148), 340.0, "ACTIVE"));
        }

        List<Showtime> savedShowtimes = showtimeRepository.saveAll(showtimesToSave);

        // 6. Create Initial Demo Bookings (e.g. for Dune today evening slot)
        if (!savedShowtimes.isEmpty()) {
            Showtime sampleShowtime = savedShowtimes.get(0);
            List<Seat> sampleSeats = seatRepository.findByScreenIdOrderBySeatRowAscSeatNumberAsc(sampleShowtime.getScreen().getId());

            if (sampleSeats.size() >= 4) {
                // Book seats D4 and D5 for demo
                Seat seat1 = sampleSeats.get(23); // around middle
                Seat seat2 = sampleSeats.get(24);

                double price1 = Math.round(sampleShowtime.getBasePrice() * seat1.getPriceMultiplier());
                double price2 = Math.round(sampleShowtime.getBasePrice() * seat2.getPriceMultiplier());
                double subtotal = price1 + price2;
                double tax = Math.round(subtotal * 0.18 * 100.0) / 100.0;
                double total = subtotal + tax;

                Booking demoBooking = new Booking(
                        "MT-948201",
                        regularUser,
                        regularUser.getFullName(),
                        regularUser.getEmail(),
                        regularUser.getPhone(),
                        sampleShowtime,
                        2,
                        subtotal,
                        tax,
                        total,
                        "CONFIRMED",
                        "CREDIT_CARD",
                        "TXN-DEMO-98214"
                );
                Booking savedDemoBooking = bookingRepository.save(demoBooking);

                bookingSeatRepository.save(new BookingSeat(savedDemoBooking, seat1, price1));
                bookingSeatRepository.save(new BookingSeat(savedDemoBooking, seat2, price2));
            }
        }

        System.out.println(">>> Sample data initialized successfully!");
        System.out.println(">>> Default Admin: admin@movieticket.com / admin123");
        System.out.println(">>> Default User:  user@movieticket.com / user123");
    }

    private void generateSeatsForScreen(Screen screen) {
        int rows = screen.getTotalRows();
        int cols = screen.getTotalCols();
        List<Seat> seats = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            char rowChar = (char) ('A' + r);
            String rowStr = String.valueOf(rowChar);

            // Tier distribution: Front rows = STANDARD, Middle rows = PREMIUM, Last rows = VIP
            String tier;
            double multiplier;

            if (r >= rows - 2) {
                tier = "VIP";
                multiplier = 1.75;
            } else if (r >= rows - 5) {
                tier = "PREMIUM";
                multiplier = 1.35;
            } else {
                tier = "STANDARD";
                multiplier = 1.0;
            }

            for (int c = 1; c <= cols; c++) {
                String seatCode = rowStr + c;
                seats.add(new Seat(screen, rowStr, c, seatCode, tier, multiplier));
            }
        }
        seatRepository.saveAll(seats);
    }
}
