# TP 7 — Spring Boot & Swagger

## 📌 Objectif

Ce TP a pour objectif de construire une application backend complète avec **Spring Boot**, en suivant une architecture propre (Controller → Service → Repository → Entity), puis de documenter automatiquement les API REST avec **Swagger (OpenAPI)**.

---

##  Étape 1 : Génération du projet

Créer un projet Spring Boot via **Spring Initializr** avec les dépendances suivantes :

* Spring Web
* Spring Data JPA
* MySQL Driver
* Validation
* Spring Boot DevTools

Structure recommandée :

```
com.example.student_management
│
├── controller
├── service
├── repository
├── entity
└── StudentManagementApplication.java
```

---

##  Étape 2 : Configuration MySQL

Configurer la base de données dans `application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/studentdb
spring.datasource.username=root
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Créer la base :

```sql
CREATE DATABASE studentdb;
```

---

##  Étape 3 : Couche Modèle (Entity)

Créer l’entité `Student` :

```java
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String prenom;

    private LocalDate dateNaissance;

    // Getters et Setters
}
```

---

##  Étape 4 : Repository

Créer l’interface :

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Query("SELECT YEAR(s.dateNaissance), COUNT(s) FROM Student s GROUP BY YEAR(s.dateNaissance)")
    Collection<Object[]> findNbrStudentByYear();
}
```

---

##  Étape 5 : Service

Créer la logique métier :

```java
@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student save(Student student) {
        return repository.save(student);
    }

    public boolean delete(int id) {
        return repository.findById(id).map(s -> {
            repository.delete(s);
            return true;
        }).orElse(false);
    }

    public List<Student> findAll() {
        return repository.findAll();
    }

    public long countStudents() {
        return repository.count();
    }

    public Collection<?> findNbrStudentByYear() {
        return repository.findNbrStudentByYear();
    }
}
```

---

##  Étape 6 : Contrôleur REST

Exposer les endpoints :

```java
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<Student> save(@RequestBody Student student) {
        return new ResponseEntity<>(service.save(student), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        return service.delete(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Student>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.countStudents());
    }

    @GetMapping("/byYear")
    public ResponseEntity<Collection<?>> byYear() {
        return ResponseEntity.ok(service.findNbrStudentByYear());
    }
}
```

---

##  Étape 7 : Tests Unitaires (JUnit 5 + Mockito)

Tester le contrôleur sans base de données :

```java
@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentService service;

    @InjectMocks
    private StudentController controller;

    @Test
    void testCount() {
        when(service.countStudents()).thenReturn(5L);

        ResponseEntity<Long> response = controller.count();

        assertEquals(5L, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

---

##  Étape 8 : Intégration Swagger (OpenAPI)

Ajouter dépendance Maven :

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

---

###  Accès à Swagger UI

Lancer l’application puis ouvrir :

```
http://localhost:8080/swagger-ui.html
```

ou

```
http://localhost:8080/swagger-ui/index.html
```

---

##  Documentation automatique

Swagger permet de :

* Visualiser tous les endpoints
* Tester les requêtes directement depuis le navigateur
* Générer une documentation claire de l’API
* Voir les modèles JSON (Request/Response)

---

##  Exemple de test avec Swagger

### POST /students/save

```json
{
  "nom": "LACHGAR",
  "prenom": "Mohamed",
  "dateNaissance": "1985-09-01"
}
```

---

## Résumé

| Couche     | Rôle                   |
| ---------- | ---------------------- |
| Entity     | Représente les données |
| Repository | Accès base de données  |
| Service    | Logique métier         |
| Controller | API REST               |
| Swagger    | Documentation API      |
| Tests      | Validation du code     |

---

##  Conclusion

Ce TP permet de maîtriser :

* L’architecture Spring Boot (MVC + Service + Repository)
* L’intégration avec MySQL
* La création d’API REST professionnelles
* Les tests unitaires avec Mockito
* La documentation automatique avec Swagger

👉 Résultat : une API robuste, testée et documentée, prête pour intégration frontend ou déploiement.

---
