package net.fatima.hospitalapp;

import lombok.extern.slf4j.Slf4j;
import net.fatima.hospitalapp.entities.Patient;
import net.fatima.hospitalapp.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;

@Slf4j
@SpringBootApplication
public class HospitalAppApplication {

	public static void main(String[] args) {

		SpringApplication.run(HospitalAppApplication.class, args);

	}
	@Bean
	public CommandLineRunner start(PatientRepository repository, PatientRepository patientRepository) {
		return  args -> {
			Patient p1 = new Patient();// NoArgsConstructor
			p1 .setNom("Youssfi");
			p1 .setPrenom("Mohamed");
			p1 .setScore(100);
			p1 .setMalade(false);
			p1 .setDateNaissance(new Date());


			Patient p2 = new Patient(null,"ait lamine","fatima",new Date(),12000, false); //AllArgsConstructor

			Patient p3=Patient.builder() // Builder
					.nom("Amira")
					.dateNaissance(new Date())
					.malade(true)
					.build();

           /*
			patientRepository.save(p1 );
			patientRepository.save(p2);
			patientRepository.save(p3);
            */
			List<Patient> patients = patientRepository.findAll();
			patients.forEach(p->{
				System.out.println(p.toString());
			});

		};
	}

}
