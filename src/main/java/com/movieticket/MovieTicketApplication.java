package com.movieticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MovieTicketApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieTicketApplication.class, args);
        System.out.println("=================================================");
        System.out.println("  Movie Ticket Management System is RUNNING!     ");
        System.out.println("  Access Web Application: http://localhost:8080 ");
        System.out.println("  Default Admin: admin@movieticket.com / admin123");
        System.out.println("  Default User:  user@movieticket.com / user123  ");
        System.out.println("=================================================");
    }
}
