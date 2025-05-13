package org.demo.gmdemo;

import org.demo.gmdemo.dto.User;
import org.demo.gmdemo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GmDemoApplication  implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;


    public static void main(String[] args) {
        SpringApplication.run(GmDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
/*

        User user = new User("Kanika");
        User saved = userRepository.save(user);

        System.out.println("Inserted user: " + saved.getId() + ", " + saved.getName());

        System.out.println("All users:");
        userRepository.findAll().forEach(u -> System.out.println(u.getId() + ": " + u.getName()));


        System.out.println("Inserted user: " + user.getName()); */
    }
}
