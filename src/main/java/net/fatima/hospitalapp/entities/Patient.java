package net.fatima.hospitalapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Entity
@Table(name = "PATINETS")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString
@Data
@Builder
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Size(min = 4, max = 50)
    @NotEmpty(message = "Le nom est obligatoire")
    private String nom;
    @NotEmpty(message = "Le prénom est obligatoire")
    private String prenom;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private Date dateNaissance;
    @DecimalMin("100")
    private int score;
    private boolean malade;
}
// start.spring.io pour creer un fichier avec les depondances