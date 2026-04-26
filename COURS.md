# Cours – Architecture backend Java Spring (TP Restaurant)

> Basé sur le projet "La Table de Margot". Chaque concept est illustré avec le code réel du TP.

---

## 1. Vue d'ensemble : l'architecture en couches

Une application Spring backend est découpée en **couches** qui ont chacune une responsabilité précise. Les requêtes HTTP traversent ces couches de haut en bas, et les réponses remontent dans l'autre sens.

```
┌─────────────────────────────────────────────┐
│           Client (Angular, Postman…)         │
└────────────────────┬────────────────────────┘
                     │ HTTP
┌────────────────────▼────────────────────────┐
│              CONTROLLER                      │  ← reçoit la requête HTTP
│         (@RestController)                    │     renvoie la réponse JSON
└────────────────────┬────────────────────────┘
                     │ appelle
┌────────────────────▼────────────────────────┐
│               SERVICE                        │  ← logique métier
│             (@Service)                       │     règles, validations
└────────────────────┬────────────────────────┘
                     │ appelle
┌────────────────────▼────────────────────────┐
│                  DAO                         │  ← accès base de données
│             (@Repository)                    │     SQL via JdbcTemplate
└────────────────────┬────────────────────────┘
                     │ SQL
┌────────────────────▼────────────────────────┐
│           Base de données H2                 │
│       (schema.sql + data.sql)                │
└─────────────────────────────────────────────┘
```

Les **DTO** sont les objets qui **transitent entre les couches** (surtout entre le Controller et l'extérieur).

---

## 2. DTO – Data Transfer Object

### Qu'est-ce que c'est ?

Un DTO est une **classe Java simple** dont le seul but est de **transporter des données** d'un endroit à un autre. Il n'a ni logique métier, ni accès à la base de données — juste des champs et leurs getters/setters.

### Pourquoi en a-t-on besoin ?

Sans DTO, on serait tenté d'exposer directement les objets de la base de données à l'API. C'est problématique :
- On exposerait des champs internes qu'on ne veut pas montrer (ex : mot de passe hashé)
- Le format de la BDD ne correspond pas toujours au format attendu par le frontend
- Le frontend envoie souvent des données partielles (juste les IDs), pas des objets complets

### Les deux types de DTO dans ce TP

**DTO de requête** — ce que le frontend *envoie* au backend :

```java
// Ce que le frontend envoie pour créer une commande client
public class CreerCommandeClientRequest {
    private Long clientId;           // juste l'ID, pas l'objet Client entier
    private List<LigneRequest> lignes;

    public static class LigneRequest {
        private Long platId;         // juste l'ID du plat
        private int quantite;
    }
}
```

**DTO de réponse** — ce que le backend *renvoie* au frontend :

```java
// Ce que le backend renvoie pour signaler un stock insuffisant
public class AlerteStockDto {
    private Long ingredientId;
    private String ingredientNom;
    private double stockActuel;
    private double seuilAlerte;
    private double quantiteACommander;
}
```

### Dans ce projet, les DTO à créer

| DTO | Sens | Usage |
|---|---|---|
| `CreerCommandeClientRequest` | Frontend → Backend | Créer une commande (clientId + lignes) |
| `CreerCommandeFournisseurRequest` | Frontend → Backend | Commander des ingrédients |
| `AlerteStockDto` | Backend → Frontend | Ingrédient en alerte de stock |
| `RecommandationDto` | Backend → Frontend | Fournisseur le moins cher + quantité conseillée |
| `PreparationResultDto` | Backend → Frontend | Résultat de la mise en préparation (commande + alertes) |

---

## 3. DAO – Data Access Object

### Qu'est-ce que c'est ?

Un DAO est la couche qui **parle à la base de données**. C'est lui qui contient les requêtes SQL. Le reste de l'application n'écrit jamais de SQL — il passe par le DAO.

### Pourquoi séparer le SQL dans une couche dédiée ?

- Si on change de base de données (H2 → PostgreSQL), on ne modifie que les DAO
- Le SQL est centralisé, facile à retrouver et à tester
- Le service reste lisible : il ne voit que des méthodes Java, pas du SQL

### L'outil : JdbcTemplate

Spring fournit `JdbcTemplate` qui simplifie l'exécution de SQL. Il gère automatiquement l'ouverture/fermeture des connexions et la conversion des résultats.

```java
@Repository  // ← dit à Spring "c'est un DAO, gère-le comme un bean"
public class ClientDao {

    private final JdbcTemplate jdbc;

    // Spring injecte automatiquement le JdbcTemplate configuré dans DatabaseConfig
    public ClientDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Récupérer tous les clients
    public List<Client> findAll() {
        return jdbc.query(
            "SELECT id, nom, prenom, email, telephone FROM CLIENT",
            (rs, rowNum) -> {          // ← RowMapper : convertit une ligne SQL en objet Java
                Client c = new Client();
                c.setId(rs.getLong("id"));
                c.setNom(rs.getString("nom"));
                c.setPrenom(rs.getString("prenom"));
                c.setEmail(rs.getString("email"));
                return c;
            }
        );
    }

    // Récupérer un client par son ID
    public Optional<Client> findById(Long id) {
        List<Client> results = jdbc.query(
            "SELECT id, nom, prenom, email, telephone FROM CLIENT WHERE id = ?",
            (rs, rowNum) -> { /* ... RowMapper ... */ },
            id  // ← remplace le ? (protection contre l'injection SQL)
        );
        return results.stream().findFirst();
    }

    // Insérer un client, retourner l'ID généré
    public Long save(Client client) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO CLIENT (nom, prenom, email, telephone) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, client.getNom());
            ps.setString(2, client.getPrenom());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getTelephone());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }
}
```

### Les 3 méthodes JdbcTemplate à retenir

| Méthode | Quand l'utiliser | Retourne |
|---|---|---|
| `jdbc.query(sql, rowMapper, args...)` | SELECT qui retourne plusieurs lignes | `List<T>` |
| `jdbc.queryForObject(sql, rowMapper, args...)` | SELECT qui retourne exactement 1 ligne | `T` (exception si 0 ou plusieurs) |
| `jdbc.update(sql, args...)` | INSERT, UPDATE, DELETE | nombre de lignes affectées |

### Dans ce projet, les DAO à créer

Un DAO par entité principale de la BDD :

| DAO | Table(s) principale(s) |
|---|---|
| `ClientDao` | `CLIENT` |
| `PlatDao` | `PLAT`, `PLAT_INGREDIENT` |
| `IngredientDao` | `INGREDIENT`, `FOURNISSEUR_INGREDIENT` |
| `FournisseurDao` | `FOURNISSEUR`, `FOURNISSEUR_INGREDIENT` |
| `CommandeClientDao` | `COMMANDE_CLIENT`, `LIGNE_COMMANDE_CLIENT` |
| `CommandeFournisseurDao` | `COMMANDE_FOURNISSEUR`, `LIGNE_COMMANDE_FOURNISSEUR` |

---

## 4. Service

### Qu'est-ce que c'est ?

Le service contient la **logique métier** : les règles propres au domaine du restaurant. C'est lui qui décide si une opération est autorisée, dans quel ordre appeler les DAO, et quoi faire en cas d'erreur.

### Ce qu'il fait / ne fait pas

| Le service FAIT | Le service ne fait PAS |
|---|---|
| Valider les règles métier | Écrire du SQL directement |
| Orchestrer plusieurs appels DAO | Construire des réponses HTTP |
| Lancer des exceptions métier | Se soucier du format JSON |
| Gérer les transactions | Lire les paramètres HTTP |

### Exemple : passer une commande en préparation

```java
@Service  // ← dit à Spring "c'est un service, gère-le comme un bean"
public class CommandeClientService {

    private final CommandeClientDao commandeDao;
    private final IngredientDao ingredientDao;

    public CommandeClientService(CommandeClientDao commandeDao, IngredientDao ingredientDao) {
        this.commandeDao = commandeDao;
        this.ingredientDao = ingredientDao;
    }

    @Transactional  // ← si une erreur survient au milieu, tout est annulé (rollback)
    public CommandeClient passerEnPreparation(Long commandeId) {
        // 1. Vérifier que la commande existe
        CommandeClient commande = commandeDao.findById(commandeId)
            .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable"));

        // 2. Vérifier la règle métier : on ne peut préparer que si EN_ATTENTE
        if (!"EN_ATTENTE".equals(commande.getStatut())) {
            throw new StatutInvalideException("La commande n'est pas EN_ATTENTE");
        }

        // 3. Vérifier le stock pour chaque ingrédient
        for (LigneCommande ligne : commande.getLignes()) {
            for (PlatIngredient pi : ligne.getPlat().getIngredients()) {
                double requis = pi.getQuantiteRequise() * ligne.getQuantite();
                if (pi.getIngredient().getStockActuel() < requis) {
                    throw new StockInsuffisantException(
                        "Stock insuffisant pour " + pi.getIngredient().getNom()
                    );
                }
            }
        }

        // 4. Décrémenter le stock
        for (LigneCommande ligne : commande.getLignes()) {
            for (PlatIngredient pi : ligne.getPlat().getIngredients()) {
                double consomme = pi.getQuantiteRequise() * ligne.getQuantite();
                ingredientDao.decrementerStock(pi.getIngredient().getId(), consomme);
            }
        }

        // 5. Mettre à jour le statut
        commandeDao.updateStatut(commandeId, "EN_PREPARATION");
        return commandeDao.findById(commandeId).get();
    }
}
```

### Dans ce projet, les services à créer

| Service | Responsabilités |
|---|---|
| `CommandeClientService` | Créer, préparer (consomme stock), servir, supprimer |
| `CommandeFournisseurService` | Créer, envoyer, recevoir (réapprovisionne stock), supprimer |
| `StockService` | Consommer/restituer le stock, détecter les alertes |

---

## 5. Controller REST

### Qu'est-ce que c'est ?

Le controller est le **point d'entrée HTTP**. Il reçoit les requêtes HTTP du frontend, délègue le travail au service, et renvoie une réponse HTTP avec le bon code de statut et le bon JSON.

Il ne contient **aucune logique métier** — juste du "câblage" HTTP ↔ Java.

### Les annotations essentielles

```java
@RestController               // combinaison de @Controller + @ResponseBody
@RequestMapping("/api/clients") // préfixe commun à tous les endpoints de ce controller
public class ClientController {

    private final ClientService service;  // injecté par Spring

    public ClientController(ClientService service) {
        this.service = service;
    }

    @GetMapping           // GET /api/clients
    public List<Client> listerClients() {
        return service.findAll();
    }

    @GetMapping("/{id}")  // GET /api/clients/42
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)                  // 200 OK
            .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    @PostMapping          // POST /api/clients
    @ResponseStatus(HttpStatus.CREATED)  // retourne 201 au lieu de 200
    public Client creerClient(@RequestBody Client client) {
        // @RequestBody : Spring désérialise automatiquement le JSON reçu en objet Java
        return service.save(client);
    }
}
```

### Les annotations à connaître

| Annotation | Rôle |
|---|---|
| `@RestController` | Déclare un controller REST (réponses en JSON automatiquement) |
| `@RequestMapping("/api/xxx")` | Préfixe de route pour tout le controller |
| `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` | Définit la méthode HTTP et la route |
| `@PathVariable` | Extrait une valeur depuis l'URL (`/clients/{id}`) |
| `@RequestParam` | Extrait un paramètre de query string (`?statut=EN_ATTENTE`) |
| `@RequestBody` | Désérialise le corps JSON de la requête en objet Java |
| `@ResponseStatus` | Définit le code HTTP de la réponse (201, 204…) |
| `ResponseEntity<T>` | Permet de contrôler finement le code HTTP et les headers |

### Les codes HTTP à retourner

| Situation | Code |
|---|---|
| Lecture réussie | `200 OK` |
| Création réussie | `201 Created` |
| Action réussie sans contenu | `204 No Content` |
| Ressource introuvable | `404 Not Found` |
| Données invalides / règle métier violée | `400 Bad Request` |

### Dans ce projet, les controllers à créer

| Controller | Route | Endpoints |
|---|---|---|
| `ClientController` | `/api/clients` | GET liste, POST créer |
| `PlatController` | `/api/plats` | GET liste, POST créer |
| `FournisseurController` | `/api/fournisseurs` | GET liste, POST créer, GET `/{id}/catalogue` |
| `IngredientController` | `/api/ingredients` | GET liste, GET `/alertes`, GET `/{id}/recommandation`, GET `/{id}/prix` |
| `CommandeClientController` | `/api/commandes/client` | GET liste, GET `/{id}`, POST, PUT `/{id}/preparer`, PUT `/{id}/servir`, DELETE `/{id}` |
| `CommandeFournisseurController` | `/api/commandes/fournisseur` | GET liste, GET `/{id}`, POST, PUT `/{id}/envoyer`, PUT `/{id}/recevoir`, DELETE `/{id}` |

---

## 6. Exceptions métier

### Pourquoi des exceptions personnalisées ?

Quand une règle métier est violée (stock insuffisant, mauvais statut…), on veut :
1. Stopper immédiatement l'opération
2. Renvoyer un message d'erreur clair au frontend
3. Associer le bon code HTTP (400, 404…)

Les exceptions Java standards (`IllegalArgumentException`, etc.) existent mais ne portent pas de code HTTP. On crée donc ses propres exceptions.

### Pattern classique

```java
// Exception de base avec code HTTP
public class AppException extends RuntimeException {
    private final int status;

    public AppException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }
}

// Exceptions spécialisées
public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String message) {
        super(404, message);
    }
}

public class StockInsuffisantException extends AppException {
    public StockInsuffisantException(String message) {
        super(400, message);
    }
}

public class StatutInvalideException extends AppException {
    public StatutInvalideException(String message) {
        super(400, message);
    }
}
```

### Intercepter les exceptions : le GlobalExceptionHandler

Sans handler, Spring renvoie une page d'erreur HTML générique. On veut du JSON propre.

```java
@RestControllerAdvice  // ← intercepte les exceptions de TOUS les controllers
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleAppException(AppException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", ex.getStatus());
        body.put("message", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }
}
```

Le frontend reçoit alors un JSON clair :
```json
{
  "status": 400,
  "message": "Stock insuffisant pour Oeufs"
}
```

---

## 7. L'injection de dépendances

### Le problème sans injection

Sans Spring, on devrait instancier manuellement tous les objets :

```java
// Cauchemar sans injection de dépendances
IngredientDao ingredientDao = new IngredientDao(jdbcTemplate);
CommandeClientDao commandeDao = new CommandeClientDao(jdbcTemplate);
CommandeClientService service = new CommandeClientService(commandeDao, ingredientDao);
CommandeClientController controller = new CommandeClientController(service);
```

### La solution : l'IoC Container de Spring

Spring lit les annotations (`@Repository`, `@Service`, `@RestController`) et crée et connecte automatiquement tous ces objets au démarrage. On appelle ça le **conteneur IoC** (Inversion of Control).

On déclare juste ce dont on a besoin dans le constructeur, Spring fait le reste :

```java
@Service
public class CommandeClientService {

    // Spring voit ce constructeur et injecte automatiquement un CommandeClientDao
    public CommandeClientService(CommandeClientDao dao) {
        this.dao = dao;
    }
}
```

### Le cycle de vie complet d'une requête HTTP

```
POST /api/commandes/client
{ "clientId": 1, "lignes": [{"platId": 2, "quantite": 3}] }

    │
    ▼
CommandeClientController.creerCommande(@RequestBody CreerCommandeClientRequest req)
    │  Spring désérialise le JSON en CreerCommandeClientRequest
    │
    ▼
CommandeClientService.creer(req)
    │  Vérifie que le client existe (appelle ClientDao)
    │  Vérifie que les plats existent (appelle PlatDao)
    │  Crée la commande et les lignes (appelle CommandeClientDao)
    │
    ▼
CommandeClientDao.save(commande)
    │  INSERT INTO COMMANDE_CLIENT ...
    │  INSERT INTO LIGNE_COMMANDE_CLIENT ...
    │
    ▼
Retourne la commande créée → service → controller → JSON 201 Created
```

---

## 8. Récapitulatif : qui fait quoi ?

| Couche | Annotation | Rôle | Ne doit PAS |
|---|---|---|---|
| **DTO** | aucune | Transporter des données | Avoir de la logique |
| **DAO** | `@Repository` | Requêtes SQL | Avoir de la logique métier |
| **Service** | `@Service` | Règles métier, orchestration | Écrire du SQL, construire du HTTP |
| **Controller** | `@RestController` | Recevoir HTTP, renvoyer JSON | Avoir de la logique métier |
| **Exception** | aucune | Signaler une erreur | — |
| **ExceptionHandler** | `@RestControllerAdvice` | Convertir exceptions → réponse HTTP | — |

---

## 9. Schéma de la base de données (rappel)

```
CLIENT ──< COMMANDE_CLIENT ──< LIGNE_COMMANDE_CLIENT >── PLAT
                                                            │
FOURNISSEUR ──< COMMANDE_FOURNISSEUR                  PLAT_INGREDIENT
      │        ──< LIGNE_COMMANDE_FOURNISSEUR >─── INGREDIENT
      └──< FOURNISSEUR_INGREDIENT >───────────────────────┘
```

Les tables de liaison importantes :
- `PLAT_INGREDIENT` : quelle quantité d'un ingrédient est nécessaire pour préparer 1 portion d'un plat
- `FOURNISSEUR_INGREDIENT` : à quel prix un fournisseur vend un ingrédient (catalogue)
- `LIGNE_COMMANDE_CLIENT` : quels plats et en quelle quantité dans une commande client
- `LIGNE_COMMANDE_FOURNISSEUR` : quels ingrédients commandés à un fournisseur
