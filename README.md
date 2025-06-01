# Partie 1 - Gestion des Patients

---

## 1. Entité Patient

```java
package net.fatima.hospitalapp.entities;

import jakarta.persistence.*;
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
    private String nom;

    private String prenom;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private Date dateNaissance;

    @DecimalMin("100")
    private int score;

    private boolean malade;
}
```

**Description :**
Cette classe représente l’entité `Patient` avec ses attributs principaux.

* `@Entity` : indique une table en base.
* `@Table(name = "PATINETS")` : nom de la table.
* Validation sur `nom` (non vide, taille entre 4 et 50).
* `dateNaissance` stockée en type DATE.
* `score` minimum 100.
* Lombok pour générer getters/setters, constructeurs, etc.

---

## 2. Repository Patient

```java
package net.fatima.hospitalapp.repository;

import net.fatima.hospitalapp.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Page<Patient> findByNomContains(String keyword, Pageable pageable);

    @Query("select p from Patient p where p.nom like :x")
    Page<Patient> chercher(@Param("x") String keyword, Pageable pageable);
}
```

**Description :**
Interface pour accéder aux données `Patient` dans la base.

* Méthode paginée pour chercher par nom.
* Requête JPQL personnalisée pour recherche avec `like`.

---

## 3. Controller Patient

```java
package net.fatima.hospitalapp.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.fatima.hospitalapp.entities.Patient;
import net.fatima.hospitalapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/index")
    public String index(Model model,
                        @RequestParam(name="page",defaultValue="0") int p,
                        @RequestParam(name = "size",defaultValue ="4" ) int s,
                        @RequestParam(name = "keyword",defaultValue ="" ) String kw) {

        Page<Patient> pagePatients = patientRepository.findByNomContains(kw, PageRequest.of(p, s));
        model.addAttribute("ListPatients", pagePatients.getContent());
        model.addAttribute("pages", new int[pagePatients.getTotalPages()]);
        model.addAttribute("currentPage", p);
        model.addAttribute("keyword", kw);
        return "patients";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/deletePatient")
    public String delete(@RequestParam(name = "id") Long id,
                         @RequestParam(name = "keyword", defaultValue = "") String keyword,
                         @RequestParam(name = "page", defaultValue = "0") int page) {
        patientRepository.deleteById(id);
        return "redirect:/user/index?page=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/patients")
    @ResponseBody
    public List<Patient> listPatients() {
        return patientRepository.findAll();
    }

    @GetMapping("/admin/formPatients")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String formPatients(Model model) {
        model.addAttribute("patient", new Patient());
        return "formPatients";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(path = "/admin/save")
    public String save(Model model, @Valid Patient patient, BindingResult bindingResult,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String keyword) {
        if (bindingResult.hasErrors()) return "formPatients";
        patientRepository.save(patient);
        return "redirect:/index?page=" + page + "&keyword=" + keyword;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/editPatient")
    public String editPatients(Model model, Long id, String keyword, int page) {
        Patient patient = patientRepository.findById(id).orElse(null);
        if (patient == null) throw new RuntimeException("Patient not found");
        model.addAttribute("patient", patient);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        return "editPatients";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/user/index";
    }
}
```

**Description :**
Contrôleur qui gère toutes les opérations CRUD sur les patients.

* Page d’accueil avec pagination et recherche.
* Suppression sécurisée par rôle `ADMIN`.
* Formulaire ajout/modification (non montré ici, à venir).
* Accès JSON pour liste complète.
* Redirection de `/` vers `/user/index`.

---

## 4. Vue Thymeleaf `patients.html`
Bien sûr ! Voici un résumé clair et concis avec le **titre**, la **description** courte, le **code essentiel**, et la description de la **capture d’écran** à ajouter dans ton doc ou présentation :

---

### 1. Liste des Patients

**Description :**
Affiche la liste paginée des patients avec leurs informations principales (id, nom, prénom, date de naissance, score, malade) et des actions (éditer, supprimer).

**Code :**

```html
<table>
  <thead>
    <tr><th>Id</th><th>Nom</th><th>Prénom</th><th>Date</th><th>Score</th><th>Malade</th><th>Actions</th></tr>
  </thead>
  <tbody>
    <tr th:each="p : ${ListPatients}">
      <td th:text="${p.id}"></td>
      <td th:text="${p.nom}"></td>
      <td th:text="${p.prenom}"></td>
      <td th:text="${#dates.format(p.dateNaissance, 'dd/MM/yyyy')}"></td>
      <td th:text="${p.score}"></td>
      <td th:text="${p.malade ? 'Oui' : 'Non'}"></td>
      <td>
        <a th:href="@{/admin/editPatient(id=${p.id})}">Edit</a>
        <a onclick="return confirm('Supprimer ce patient ?')" th:href="@{/admin/deletePatient(id=${p.id})}">Delete</a>
      </td>
    </tr>
  </tbody>
</table>
```

![Recherche](img/img.png)


---

### 2. Recherche des Patients

**Description :**
Formulaire simple permettant de chercher les patients par nom, avec un champ texte et un bouton de recherche.

**Code :**

```html
<form method="get" th:action="@{/user/index}">
  <input type="text" name="keyword" placeholder="Recherche par nom..." th:value="${keyword}">
  <button type="submit">Rechercher</button>
</form>
```
![List des patient](imgg_1.png)

---

### 3. Suppression d’un Patient

**Description :**
Lien de suppression avec popup de confirmation. En backend, suppression du patient et redirection vers la liste.

**Code front :**

```html
<a onclick="return confirm('Supprimer ce patient ?')" th:href="@{/admin/deletePatient(id=${p.id})}">Delete</a>
```

**Code backend (Spring Boot) :**

```java
@GetMapping("/admin/deletePatient")
public String deletePatient(@RequestParam Long id) {
    patientRepository.deleteById(id);
    return "redirect:/user/index";
}
```
![suppression d'un patient](img/img_2.png)


**Description :**
Page web pour afficher la liste paginée des patients.

* Recherche par mot-clé.
* Tableau avec données des patients.
* Boutons Edit/Delete .
* Utilisation de Bootstrap.
* Pagination avec boutons numérotés.
---

