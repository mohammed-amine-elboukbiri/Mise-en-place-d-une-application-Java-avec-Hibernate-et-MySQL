# TP — Persistance des Données avec Hibernate & MySQL : Configuration, Entités JPA et Tests

## 🎯 Objectif

Configurer une application Java avec **Hibernate** et **MySQL** pour gérer la persistance des données. Ce TP couvre l'ensemble du cycle : configuration des dépendances Maven, création des entités JPA, mise en place des services, et validation via des tests.

---

## 🛠️ Technologies utilisées

| Technologie | Rôle |
|-------------|------|
| **Java 17+** | Langage principal |
| **Maven** | Gestion des dépendances et build |
| **Hibernate 6+** | ORM (Object-Relational Mapping) |
| **JPA (Jakarta Persistence)** | API standard de persistance |
| **MySQL** | Base de données relationnelle |
| **JUnit** | Tests unitaires et d'intégration |

---

## 📁 Structure du projet


---

## 📦 Configuration Maven (`pom.xml`)

```xml
<dependencies>
    <!-- Hibernate Core -->
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>6.4.0.Final</version>
    </dependency>

    <!-- Connecteur MySQL -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.3.0</version>
    </dependency>

    <!-- JUnit pour les tests -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## ⚙️ Configuration Hibernate (`persistence.xml`)

Fichier à placer dans `src/main/resources/META-INF/` :

```xml
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.0">
    <persistence-unit name="monTP" transaction-type="RESOURCE_LOCAL">

        <properties>
            <!-- Connexion MySQL -->
            <property name="jakarta.persistence.jdbc.driver"   value="com.mysql.cj.jdbc.Driver"/>
            <property name="jakarta.persistence.jdbc.url"      value="jdbc:mysql://localhost:3306/tp_hibernate"/>
            <property name="jakarta.persistence.jdbc.user"     value="root"/>
            <property name="jakarta.persistence.jdbc.password" value=""/>

            <!-- Comportement Hibernate -->
            <property name="hibernate.dialect"              value="org.hibernate.dialect.MySQLDialect"/>
            <property name="hibernate.hbm2ddl.auto"         value="update"/>
            <property name="hibernate.show_sql"             value="true"/>
            <property name="hibernate.format_sql"           value="true"/>
        </properties>

    </persistence-unit>
</persistence>
```

### Valeurs de `hbm2ddl.auto`

| Valeur | Comportement |
|--------|-------------|
| `create` | Recrée le schéma à chaque démarrage |
| `create-drop` | Crée au démarrage, supprime à l'arrêt |
| `update` | Met à jour le schéma sans perte de données |
| `validate` | Valide le schéma sans modification |

---

## 🗃️ Création d'une entité JPA

```java
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false)
    private LocalDate dateNaissance;

    private boolean malade;

    private int score;

    // Getters & Setters
}
```

### Annotations JPA essentielles

| Annotation | Rôle |
|------------|------|
| `@Entity` | Déclare la classe comme entité persistante |
| `@Table` | Spécifie le nom de la table en base |
| `@Id` | Clé primaire |
| `@GeneratedValue` | Stratégie de génération de l'ID |
| `@Column` | Personnalise la colonne (nom, contraintes) |
| `@OneToMany` / `@ManyToOne` | Relations entre entités |

---

## 🔧 Couche Service

```java
public class PatientService {

    private EntityManagerFactory emf =
        Persistence.createEntityManagerFactory("monTP");

    // Créer
    public void save(Patient p) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();
        em.close();
    }

    // Lire
    public Patient findById(Long id) {
        EntityManager em = emf.createEntityManager();
        Patient p = em.find(Patient.class, id);
        em.close();
        return p;
    }

    // Lister
    public List<Patient> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Patient> list = em.createQuery("SELECT p FROM Patient p", Patient.class)
                               .getResultList();
        em.close();
        return list;
    }

    // Modifier
    public void update(Patient p) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(p);
        em.getTransaction().commit();
        em.close();
    }

    // Supprimer
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Patient p = em.find(Patient.class, id);
        if (p != null) em.remove(p);
        em.getTransaction().commit();
        em.close();
    }
}
```

---

## ✅ Tests

```java
public class PatientServiceTest {

    private PatientService service = new PatientService();

    @Test
    void testSaveAndFind() {
        Patient p = new Patient();
        p.setNom("Ahmed");
        p.setDateNaissance(LocalDate.of(1995, 3, 12));
        p.setMalade(false);
        p.setScore(85);

        service.save(p);

        assertNotNull(p.getId());
        Patient found = service.findById(p.getId());
        assertEquals("Ahmed", found.getNom());
    }

    @Test
    void testFindAll() {
        List<Patient> patients = service.findAll();
        assertFalse(patients.isEmpty());
    }
}
```

---

## 🚀 Lancer l'application

### 1. Créer la base de données MySQL

```sql
CREATE DATABASE tp_hibernate CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Compiler et exécuter

```bash
mvn clean install
mvn exec:java -Dexec.mainClass="ma.enset.Main"
```

### 3. Lancer les tests

```bash
mvn test
```

---

## 🔄 Cycle de vie d'un objet JPA

```
New (Transient)
      │
   persist()
      ↓
  Managed  ──── flush/commit ────→  Base de données
      │
   detach() / close()
      ↓
  Detached
      │
    merge()
      ↓
  Managed
      │
   remove()
      ↓
  Removed
```

---

## 📝 Points clés à retenir

- **Hibernate** est l'implémentation JPA la plus utilisée ; JPA est la spécification, Hibernate est l'outil.
- Toujours **ouvrir et fermer** l'`EntityManager` pour chaque opération afin d'éviter les fuites mémoire.
- Le fichier `persistence.xml` est le **point central** de configuration de la connexion et du comportement Hibernate.
- Utiliser `hbm2ddl.auto=update` en développement et `validate` en production.
- Les **transactions** sont obligatoires pour toute opération d'écriture (`persist`, `merge`, `remove`).