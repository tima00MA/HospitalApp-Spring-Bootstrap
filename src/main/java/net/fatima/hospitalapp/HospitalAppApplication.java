package net.fatima.hospitalapp;

import lombok.extern.slf4j.Slf4j;
import net.fatima.hospitalapp.entities.Patient;
import net.fatima.hospitalapp.repository.PatientRepository;
import net.fatima.hospitalapp.security.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import java.util.Date;
import java.util.List;

@Slf4j

@SpringBootApplication
public class HospitalAppApplication {
        @Autowired
        private PatientRepository patientRepository;

        public static void main(String[] args) {
            SpringApplication.run(HospitalAppApplication.class, args);
        }
        //@Bean

        //  @Bean
        CommandLineRunner commandLineRunner(JdbcUserDetailsManager jdbcUserDetailsManager) {
            return args -> {
                UserDetails u1 =jdbcUserDetailsManager.loadUserByUsername("user11");
                if(u1==null)

                    jdbcUserDetailsManager.createUser(
                            User.withUsername("user11").password(new BCryptPasswordEncoder().encode("1234")).roles("USER").build());

                UserDetails u2 =jdbcUserDetailsManager.loadUserByUsername("user11");
                if(u2==null)
                    jdbcUserDetailsManager.createUser(
                            User.withUsername("user22").password(new BCryptPasswordEncoder().encode("1234")).roles("USER").build());
                UserDetails u3 =jdbcUserDetailsManager.loadUserByUsername("user11");
                if(u3==null)
                    jdbcUserDetailsManager.createUser(
                            User.withUsername("admin2").password(new BCryptPasswordEncoder().encode("1234")).roles("USER","ADMIN").build());


            };


        }


        //@Bean
        CommandLineRunner commandLineRunnerUserDetails(AccountService accountService) {
            return args -> {
                accountService.addNewRole("USER");
                accountService.addNewRole("ADMIN");
                accountService.addNewUser("user1","1234","user1@gmail.com","1234");
                accountService.addNewUser("user2","1234","user2@gmail.com","1234");
                accountService.addNewUser("admin","1234","admin@gmail.com","1234");

                accountService.addRoleToUser("user1","USER");
                accountService.addRoleToUser("user2","USER");
                accountService.addRoleToUser("admin","USER");
                accountService.addRoleToUser("admin","ADMIN");


            };
        }



        @Bean
        PasswordEncoder passwordEncoder(){
            return new BCryptPasswordEncoder();
        }
    }