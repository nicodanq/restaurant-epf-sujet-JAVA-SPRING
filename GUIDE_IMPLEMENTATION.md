# Guide d'implémentation – Restaurant EPF

> Suis les étapes **dans l'ordre**. Chaque étape s'appuie sur la précédente.
> Les packages sont tous sous `fr.epf.restaurant`.

---

## Ordre des étapes

```
1. Modèles (entités Java)
2. DTOs
3. Exceptions
4. GlobalExceptionHandler
5. DAOs
6. Services
7. Controllers
8. Tests
```

---

## Étape 1 – Les modèles (entités Java)

Les modèles sont des classes Java qui **représentent les tables** de la BDD. Crée un package `model/`.

### `model/Client.java`
```java
package fr.epf.restaurant.model;

public class Client {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    // getters et setters pour chaque champ
}
```

### `model/Ingredient.java`
```java
package fr.epf.restaurant.model;

public class Ingredient {
    private Long id;
    private String nom;
    private String unite;
    private double stockActuel;
    private double seuilAlerte;

    // getters et setters
}
```

### `model/Plat.java`
```java
package fr.epf.restaurant.model;

import java.util.List;

public class Plat {
    private Long id;
    private String nom;
    private String description;
    private double prix;
    private List<PlatIngredient> ingredients; // chargé à la demande par le DAO

    // getters et setters
}
```

### `model/PlatIngredient.java`
> Représente une ligne de la table `PLAT_INGREDIENT` : quantité d'un ingrédient pour 1 portion d'un plat.

```java
package fr.epf.restaurant.model;

public class PlatIngredient {
    private Ingredient ingredient;
    private double quantiteRequise;

    // getters et setters
}
```

### `model/Fournisseur.java`
```java
package fr.epf.restaurant.model;

public class Fournisseur {
    private Long id;
    private String nom;
    private String contact;
    private String email;

    // getters et setters
}
```

### `model/CommandeClient.java`
```java
package fr.epf.restaurant.model;

import java.time.LocalDateTime;
import java.util.List;

public class CommandeClient {
    private Long id;
    private Client client;
    private LocalDateTime dateCommande;
    private String statut;           // "EN_ATTENTE", "EN_PREPARATION", "SERVIE"
    private List<LigneCommandeClient> lignes;

    // getters et setters
}
```

### `model/LigneCommandeClient.java`
```java
package fr.epf.restaurant.model;

public class LigneCommandeClient {
    private Long id;
    private Plat plat;
    private int quantite;

    // getters et setters
}
```

### `model/CommandeFournisseur.java`
```java
package fr.epf.restaurant.model;

import java.time.LocalDateTime;
import java.util.List;

public class CommandeFournisseur {
    private Long id;
    private Fournisseur fournisseur;
    private LocalDateTime dateCommande;
    private String statut;           // "EN_ATTENTE", "ENVOYEE", "RECUE"
    private List<LigneCommandeFournisseur> lignes;

    // getters et setters
}
```

### `model/LigneCommandeFournisseur.java`
```java
package fr.epf.restaurant.model;

public class LigneCommandeFournisseur {
    private Long id;
    private Ingredient ingredient;
    private double quantiteCommandee;
    private double prixUnitaire;

    // getters et setters
}
```

---

## Étape 2 – Les DTOs

Les DTOs sont dans le package `dto/`. Ce sont des classes simples, sans annotations Spring.

### `dto/CreerCommandeClientRequest.java`
> Reçu par le controller quand le frontend crée une commande.

```java
package fr.epf.restaurant.dto;

import java.util.List;

public class CreerCommandeClientRequest {
    private Long clientId;
    private List<LigneRequest> lignes;

    public static class LigneRequest {
        private Long platId;
        private int quantite;
        // getters et setters
    }

    // getters et setters
}
```

### `dto/CreerCommandeFournisseurRequest.java`
```java
package fr.epf.restaurant.dto;

import java.util.List;

public class CreerCommandeFournisseurRequest {
    private Long fournisseurId;
    private List<LigneRequest> lignes;

    public static class LigneRequest {
        private Long ingredientId;
        private double quantite;
        private double prixUnitaire;
        // getters et setters
    }

    // getters et setters
}
```

### `dto/AlerteStockDto.java`
> Envoyé au frontend pour signaler un ingrédient dont le stock est sous le seuil.

```java
package fr.epf.restaurant.dto;

public class AlerteStockDto {
    private Long ingredientId;
    private String ingredientNom;
    private double stockActuel;
    private double seuilAlerte;
    private double quantiteACommander;

    // constructeur, getters et setters
}
```

### `dto/RecommandationDto.java`
> Envoyé pour `GET /api/ingredients/{id}/recommandation`.

```java
package fr.epf.restaurant.dto;

public class RecommandationDto {
    private Long fournisseurId;
    private String fournisseurNom;
    private double prixUnitaire;
    private double quantiteRecommandee;

    // constructeur, getters et setters
}
```

### `dto/PreparationResultDto.java`
> Résultat de `PUT /api/commandes/client/{id}/preparer` : la commande + les alertes stock éventuelles.

```java
package fr.epf.restaurant.dto;

import fr.epf.restaurant.model.CommandeClient;
import java.util.List;

public class PreparationResultDto {
    private CommandeClient commande;
    private List<AlerteStockDto> alertes;

    // constructeur, getters et setters
}
```

---

## Étape 3 – Les exceptions

Package `exception/`.

### `exception/AppException.java`
> Classe de base. Porte le code HTTP pour que le handler puisse l'utiliser.

```java
package fr.epf.restaurant.exception;

public class AppException extends RuntimeException {
    private final int status;

    public AppException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
```

### `exception/ResourceNotFoundException.java`
```java
package fr.epf.restaurant.exception;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String message) {
        super(404, message);
    }
}
```

### `exception/StockInsuffisantException.java`
```java
package fr.epf.restaurant.exception;

public class StockInsuffisantException extends AppException {
    public StockInsuffisantException(String message) {
        super(400, message);
    }
}
```

### `exception/StatutInvalideException.java`
```java
package fr.epf.restaurant.exception;

public class StatutInvalideException extends AppException {
    public StatutInvalideException(String message) {
        super(400, message);
    }
}
```

---

## Étape 4 – GlobalExceptionHandler

Intercepte toutes les `AppException` lancées par les services et les convertit en JSON.  
À placer dans `controller/GlobalExceptionHandler.java`.

```java
package fr.epf.restaurant.controller;

import fr.epf.restaurant.exception.AppException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
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

---

## Étape 5 – Les DAOs

Package `dao/`. Chaque DAO est annoté `@Repository` et reçoit `JdbcTemplate` par injection de constructeur.

**Squelette commun :**
```java
@Repository
public class XxxDao {
    private final JdbcTemplate jdbc;

    public XxxDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
}
```

---

### `dao/ClientDao.java`

```java
@Repository
public class ClientDao {
    private final JdbcTemplate jdbc;
    public ClientDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    // Convertit une ligne SQL en objet Client
    private final RowMapper<Client> mapper = (rs, i) -> {
        Client c = new Client();
        c.setId(rs.getLong("id"));
        c.setNom(rs.getString("nom"));
        c.setPrenom(rs.getString("prenom"));
        c.setEmail(rs.getString("email"));
        c.setTelephone(rs.getString("telephone"));
        return c;
    };

    public List<Client> findAll() {
        return jdbc.query("SELECT * FROM CLIENT", mapper);
    }

    public Optional<Client> findById(Long id) {
        List<Client> r = jdbc.query("SELECT * FROM CLIENT WHERE id = ?", mapper, id);
        return r.stream().findFirst();
    }

    public Long save(Client client) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO CLIENT (nom, prenom, email, telephone) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, client.getNom());
            ps.setString(2, client.getPrenom());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getTelephone());
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }
}
```

---

### `dao/IngredientDao.java`

Méthodes à implémenter :

| Méthode | SQL |
|---|---|
| `findAll()` | `SELECT * FROM INGREDIENT` |
| `findById(Long id)` | `SELECT * FROM INGREDIENT WHERE id = ?` |
| `findSousAlerte()` | `SELECT * FROM INGREDIENT WHERE stock_actuel < seuil_alerte` |
| `decrementerStock(Long id, double qte)` | `UPDATE INGREDIENT SET stock_actuel = stock_actuel - ? WHERE id = ?` |
| `incrementerStock(Long id, double qte)` | `UPDATE INGREDIENT SET stock_actuel = stock_actuel + ? WHERE id = ?` |
| `findPrixParFournisseur(Long id)` | `SELECT fi.fournisseur_id, f.nom, fi.prix_unitaire FROM FOURNISSEUR_INGREDIENT fi JOIN FOURNISSEUR f ON f.id = fi.fournisseur_id WHERE fi.ingredient_id = ?` |
| `findFournisseurLeMoinsCher(Long id)` | `SELECT fi.fournisseur_id, f.nom, fi.prix_unitaire FROM FOURNISSEUR_INGREDIENT fi JOIN FOURNISSEUR f ON f.id = fi.fournisseur_id WHERE fi.ingredient_id = ? ORDER BY fi.prix_unitaire ASC LIMIT 1` |

> **Attention** pour `findSousAlerte()` : la condition est `stock_actuel < seuil_alerte` (strictement inférieur — exactement au seuil = pas en alerte).

---

### `dao/PlatDao.java`

Méthodes à implémenter :

| Méthode | SQL |
|---|---|
| `findAll()` | `SELECT * FROM PLAT` |
| `findById(Long id)` | `SELECT * FROM PLAT WHERE id = ?` |
| `findWithIngredients(Long id)` | Requête sur PLAT + jointure PLAT_INGREDIENT + INGREDIENT |
| `save(Plat plat)` | `INSERT INTO PLAT` |

> Pour `findWithIngredients` : récupère d'abord le plat avec `findById`, puis charge ses ingrédients avec :
> ```sql
> SELECT pi.quantite_requise, i.*
> FROM PLAT_INGREDIENT pi
> JOIN INGREDIENT i ON i.id = pi.ingredient_id
> WHERE pi.plat_id = ?
> ```

---

### `dao/FournisseurDao.java`

Méthodes à implémenter :

| Méthode | SQL |
|---|---|
| `findAll()` | `SELECT * FROM FOURNISSEUR` |
| `findById(Long id)` | `SELECT * FROM FOURNISSEUR WHERE id = ?` |
| `save(Fournisseur f)` | `INSERT INTO FOURNISSEUR` |
| `findCatalogue(Long id)` | Jointure `FOURNISSEUR_INGREDIENT` + `INGREDIENT` où `fournisseur_id = ?` |

> Pour `findCatalogue`, retourne une liste de Maps ou une classe interne avec : `ingredientId`, `ingredientNom`, `ingredientUnite`, `prixUnitaire`.

---

### `dao/CommandeClientDao.java`

C'est le DAO le plus complexe car une commande contient des lignes.

Méthodes à implémenter :

| Méthode | Description |
|---|---|
| `findAll(String statut)` | Liste toutes les commandes, filtre par statut si non null |
| `findById(Long id)` | Commande + ses lignes (avec plat) |
| `save(Long clientId)` | INSERT dans COMMANDE_CLIENT, retourne l'id généré |
| `addLigne(Long commandeId, Long platId, int quantite)` | INSERT dans LIGNE_COMMANDE_CLIENT |
| `updateStatut(Long id, String statut)` | UPDATE COMMANDE_CLIENT SET statut = ? WHERE id = ? |
| `delete(Long id)` | DELETE les lignes puis la commande |

**Astuce pour `findById` :** charger les lignes séparément avec la méthode `findLignesByCommandeId`.

```java
public Optional<CommandeClient> findById(Long id) {
    List<CommandeClient> r = jdbc.query(
        "SELECT cc.*, c.nom, c.prenom, c.email, c.telephone " +
        "FROM COMMANDE_CLIENT cc JOIN CLIENT c ON c.id = cc.client_id " +
        "WHERE cc.id = ?",
        (rs, i) -> {
            CommandeClient cmd = new CommandeClient();
            cmd.setId(rs.getLong("cc.id"));
            cmd.setStatut(rs.getString("statut"));
            cmd.setDateCommande(rs.getTimestamp("date_commande").toLocalDateTime());
            Client client = new Client();
            client.setId(rs.getLong("client_id"));
            client.setNom(rs.getString("nom"));
            // ... autres champs client
            cmd.setClient(client);
            cmd.setLignes(findLignesByCommandeId(id)); // charge les lignes
            return cmd;
        }, id);
    return r.stream().findFirst();
}

private List<LigneCommandeClient> findLignesByCommandeId(Long commandeId) {
    return jdbc.query(
        "SELECT lcc.*, p.nom, p.prix, p.description " +
        "FROM LIGNE_COMMANDE_CLIENT lcc JOIN PLAT p ON p.id = lcc.plat_id " +
        "WHERE lcc.commande_client_id = ?",
        (rs, i) -> {
            LigneCommandeClient ligne = new LigneCommandeClient();
            ligne.setId(rs.getLong("lcc.id")); // ou juste "id" si pas d'ambiguïté
            ligne.setQuantite(rs.getInt("quantite"));
            Plat plat = new Plat();
            plat.setId(rs.getLong("plat_id"));
            plat.setNom(rs.getString("nom"));
            plat.setPrix(rs.getDouble("prix"));
            ligne.setPlat(plat);
            return ligne;
        }, commandeId);
}
```

---

### `dao/CommandeFournisseurDao.java`

Même structure que `CommandeClientDao` mais pour les commandes fournisseur :

| Méthode | Description |
|---|---|
| `findAll(String statut)` | Liste avec filtre statut optionnel |
| `findById(Long id)` | Commande + lignes (avec ingredient) |
| `save(Long fournisseurId)` | INSERT COMMANDE_FOURNISSEUR |
| `addLigne(Long commandeId, Long ingredientId, double quantite, double prixUnitaire)` | INSERT LIGNE_COMMANDE_FOURNISSEUR |
| `updateStatut(Long id, String statut)` | UPDATE statut |
| `delete(Long id)` | DELETE lignes puis commande |

---

## Étape 6 – Les Services

Package `service/`. Chaque service est annoté `@Service`.

### `service/StockService.java`

```java
@Service
public class StockService {
    private final IngredientDao ingredientDao;

    public StockService(IngredientDao ingredientDao) {
        this.ingredientDao = ingredientDao;
    }

    public List<AlerteStockDto> getAlertes() {
        return ingredientDao.findSousAlerte().stream()
            .map(ing -> {
                AlerteStockDto dto = new AlerteStockDto();
                dto.setIngredientId(ing.getId());
                dto.setIngredientNom(ing.getNom());
                dto.setStockActuel(ing.getStockActuel());
                dto.setSeuilAlerte(ing.getSeuilAlerte());
                // quantité à commander = 2 * (seuil - stock) si stock < seuil
                dto.setQuantiteACommander(2 * (ing.getSeuilAlerte() - ing.getStockActuel()));
                return dto;
            })
            .collect(Collectors.toList());
    }

    public RecommandationDto getRecommandation(Long ingredientId) {
        Ingredient ing = ingredientDao.findById(ingredientId)
            .orElseThrow(() -> new ResourceNotFoundException("Ingrédient introuvable"));

        // Fournisseur le moins cher
        // (le DAO retourne déjà le moins cher via ORDER BY prix_unitaire ASC LIMIT 1)
        return ingredientDao.findFournisseurLeMoinsCher(ingredientId)
            .map(row -> {
                RecommandationDto dto = new RecommandationDto();
                dto.setFournisseurId((Long) row.get("fournisseur_id"));
                dto.setFournisseurNom((String) row.get("nom"));
                dto.setPrixUnitaire((Double) row.get("prix_unitaire"));

                // Calcul de la quantité recommandée
                double stock = ing.getStockActuel();
                double seuil = ing.getSeuilAlerte();
                double qte = (seuil > stock) ? 2 * (seuil - stock) : seuil;
                dto.setQuantiteRecommandee(qte);
                return dto;
            })
            .orElseThrow(() -> new ResourceNotFoundException("Aucun fournisseur pour cet ingrédient"));
    }
}
```

---

### `service/CommandeClientService.java`

```java
@Service
public class CommandeClientService {
    private final CommandeClientDao commandeDao;
    private final ClientDao clientDao;
    private final PlatDao platDao;
    private final IngredientDao ingredientDao;
    private final StockService stockService;

    // constructeur avec tous les DAOs

    public List<CommandeClient> findAll(String statut) {
        return commandeDao.findAll(statut);
    }

    public CommandeClient findById(Long id) {
        return commandeDao.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable : " + id));
    }

    @Transactional
    public CommandeClient creer(CreerCommandeClientRequest req) {
        // 1. Vérifier que le client existe
        clientDao.findById(req.getClientId())
            .orElseThrow(() -> new ResourceNotFoundException("Client introuvable : " + req.getClientId()));

        // 2. Créer la commande
        Long commandeId = commandeDao.save(req.getClientId());

        // 3. Ajouter les lignes
        for (CreerCommandeClientRequest.LigneRequest ligne : req.getLignes()) {
            platDao.findById(ligne.getPlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Plat introuvable : " + ligne.getPlatId()));
            commandeDao.addLigne(commandeId, ligne.getPlatId(), ligne.getQuantite());
        }

        return commandeDao.findById(commandeId).get();
    }

    @Transactional
    public PreparationResultDto passerEnPreparation(Long id) {
        CommandeClient commande = findById(id);

        // Vérifier le statut
        if (!"EN_ATTENTE".equals(commande.getStatut())) {
            throw new StatutInvalideException("La commande doit être EN_ATTENTE pour être préparée");
        }

        // Vérifier et consommer le stock
        for (LigneCommandeClient ligne : commande.getLignes()) {
            // Charger les ingrédients du plat avec leurs quantités requises
            Plat platComplet = platDao.findWithIngredients(ligne.getPlat().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Plat introuvable"));

            for (PlatIngredient pi : platComplet.getIngredients()) {
                double requis = pi.getQuantiteRequise() * ligne.getQuantite();
                if (pi.getIngredient().getStockActuel() < requis) {
                    throw new StockInsuffisantException(
                        "Stock insuffisant pour " + pi.getIngredient().getNom() +
                        " (requis: " + requis + ", disponible: " + pi.getIngredient().getStockActuel() + ")"
                    );
                }
            }

            // Décrémenter le stock
            for (PlatIngredient pi : platComplet.getIngredients()) {
                double consomme = pi.getQuantiteRequise() * ligne.getQuantite();
                ingredientDao.decrementerStock(pi.getIngredient().getId(), consomme);
            }
        }

        commandeDao.updateStatut(id, "EN_PREPARATION");

        PreparationResultDto result = new PreparationResultDto();
        result.setCommande(commandeDao.findById(id).get());
        result.setAlertes(stockService.getAlertes());
        return result;
    }

    @Transactional
    public CommandeClient servir(Long id) {
        CommandeClient commande = findById(id);
        if (!"EN_PREPARATION".equals(commande.getStatut())) {
            throw new StatutInvalideException("La commande doit être EN_PREPARATION pour être servie");
        }
        commandeDao.updateStatut(id, "SERVIE");
        return commandeDao.findById(id).get();
    }

    @Transactional
    public void supprimer(Long id) {
        findById(id); // vérifie l'existence
        commandeDao.delete(id);
    }
}
```

---

### `service/CommandeFournisseurService.java`

```java
@Service
public class CommandeFournisseurService {
    private final CommandeFournisseurDao commandeDao;
    private final FournisseurDao fournisseurDao;
    private final IngredientDao ingredientDao;

    // constructeur

    public List<CommandeFournisseur> findAll(String statut) {
        return commandeDao.findAll(statut);
    }

    public CommandeFournisseur findById(Long id) {
        return commandeDao.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Commande fournisseur introuvable : " + id));
    }

    @Transactional
    public CommandeFournisseur creer(CreerCommandeFournisseurRequest req) {
        fournisseurDao.findById(req.getFournisseurId())
            .orElseThrow(() -> new ResourceNotFoundException("Fournisseur introuvable"));

        Long commandeId = commandeDao.save(req.getFournisseurId());

        for (CreerCommandeFournisseurRequest.LigneRequest ligne : req.getLignes()) {
            ingredientDao.findById(ligne.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingrédient introuvable"));
            commandeDao.addLigne(commandeId, ligne.getIngredientId(),
                                 ligne.getQuantite(), ligne.getPrixUnitaire());
        }

        return commandeDao.findById(commandeId).get();
    }

    @Transactional
    public CommandeFournisseur envoyer(Long id) {
        CommandeFournisseur commande = findById(id);
        if (!"EN_ATTENTE".equals(commande.getStatut())) {
            throw new StatutInvalideException("La commande doit être EN_ATTENTE pour être envoyée");
        }
        commandeDao.updateStatut(id, "ENVOYEE");
        return commandeDao.findById(id).get();
    }

    @Transactional
    public CommandeFournisseur recevoir(Long id) {
        CommandeFournisseur commande = findById(id);
        if (!"ENVOYEE".equals(commande.getStatut())) {
            throw new StatutInvalideException("La commande doit être ENVOYEE pour être reçue");
        }

        // Réapprovisionner le stock pour chaque ligne
        for (LigneCommandeFournisseur ligne : commande.getLignes()) {
            ingredientDao.incrementerStock(
                ligne.getIngredient().getId(),
                ligne.getQuantiteCommandee()
            );
        }

        commandeDao.updateStatut(id, "RECUE");
        return commandeDao.findById(id).get();
    }

    @Transactional
    public void supprimer(Long id) {
        findById(id);
        commandeDao.delete(id);
    }
}
```

---

## Étape 7 – Les Controllers

Package `controller/`. Chaque controller est annoté `@RestController`.

### `controller/ClientController.java`

```java
@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientDao clientDao;
    public ClientController(ClientDao clientDao) { this.clientDao = clientDao; }

    @GetMapping
    public List<Client> lister() {
        return clientDao.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client creer(@RequestBody Client client) {
        Long id = clientDao.save(client);
        return clientDao.findById(id).get();
    }
}
```

### `controller/PlatController.java`

```java
@RestController
@RequestMapping("/api/plats")
public class PlatController {
    private final PlatDao platDao;
    public PlatController(PlatDao platDao) { this.platDao = platDao; }

    @GetMapping
    public List<Plat> lister() {
        return platDao.findAll();
    }

    @GetMapping("/{id}")
    public Plat getPlat(@PathVariable Long id) {
        return platDao.findWithIngredients(id)
            .orElseThrow(() -> new ResourceNotFoundException("Plat introuvable : " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Plat creer(@RequestBody Plat plat) {
        Long id = platDao.save(plat);
        return platDao.findById(id).get();
    }
}
```

### `controller/IngredientController.java`

```java
@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {
    private final IngredientDao ingredientDao;
    private final StockService stockService;

    public IngredientController(IngredientDao ingredientDao, StockService stockService) {
        this.ingredientDao = ingredientDao;
        this.stockService = stockService;
    }

    @GetMapping
    public List<Ingredient> lister() {
        return ingredientDao.findAll();
    }

    @GetMapping("/alertes")
    public List<AlerteStockDto> alertes() {
        return stockService.getAlertes();
    }

    @GetMapping("/{id}/recommandation")
    public RecommandationDto recommandation(@PathVariable Long id) {
        return stockService.getRecommandation(id);
    }

    @GetMapping("/{id}/prix")
    public List<Map<String, Object>> prix(@PathVariable Long id) {
        // Vérifie que l'ingrédient existe
        ingredientDao.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ingrédient introuvable : " + id));
        return ingredientDao.findPrixParFournisseur(id);
    }
}
```

### `controller/FournisseurController.java`

```java
@RestController
@RequestMapping("/api/fournisseurs")
public class FournisseurController {
    private final FournisseurDao fournisseurDao;
    public FournisseurController(FournisseurDao fournisseurDao) { this.fournisseurDao = fournisseurDao; }

    @GetMapping
    public List<Fournisseur> lister() {
        return fournisseurDao.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Fournisseur creer(@RequestBody Fournisseur fournisseur) {
        Long id = fournisseurDao.save(fournisseur);
        return fournisseurDao.findById(id).get();
    }

    @GetMapping("/{id}/catalogue")
    public List<Map<String, Object>> catalogue(@PathVariable Long id) {
        fournisseurDao.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fournisseur introuvable : " + id));
        return fournisseurDao.findCatalogue(id);
    }
}
```

### `controller/CommandeClientController.java`

```java
@RestController
@RequestMapping("/api/commandes/client")
public class CommandeClientController {
    private final CommandeClientService service;
    public CommandeClientController(CommandeClientService service) { this.service = service; }

    @GetMapping
    public List<CommandeClient> lister(@RequestParam(required = false) String statut) {
        return service.findAll(statut);
    }

    @GetMapping("/{id}")
    public CommandeClient getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommandeClient creer(@RequestBody CreerCommandeClientRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}/preparer")
    public PreparationResultDto preparer(@PathVariable Long id) {
        return service.passerEnPreparation(id);
    }

    @PutMapping("/{id}/servir")
    public CommandeClient servir(@PathVariable Long id) {
        return service.servir(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}
```

### `controller/CommandeFournisseurController.java`

```java
@RestController
@RequestMapping("/api/commandes/fournisseur")
public class CommandeFournisseurController {
    private final CommandeFournisseurService service;
    public CommandeFournisseurController(CommandeFournisseurService service) { this.service = service; }

    @GetMapping
    public List<CommandeFournisseur> lister(@RequestParam(required = false) String statut) {
        return service.findAll(statut);
    }

    @GetMapping("/{id}")
    public CommandeFournisseur getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommandeFournisseur creer(@RequestBody CreerCommandeFournisseurRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}/envoyer")
    public CommandeFournisseur envoyer(@PathVariable Long id) {
        return service.envoyer(id);
    }

    @PutMapping("/{id}/recevoir")
    public CommandeFournisseur recevoir(@PathVariable Long id) {
        return service.recevoir(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}
```

---

## Étape 8 – Les Tests JUnit 5

Les tests sont dans `src/test/`. `TestConfig` est déjà fourni — il recharge une BDD H2 fraîche avant chaque test.

**Minimum requis : 8 tests, dont 2 cas d'erreur (exception métier).**

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class CommandeClientServiceTest {

    @Autowired
    private CommandeClientService commandeClientService;

    @Autowired
    private IngredientDao ingredientDao;

    // ---- Cas nominaux ----

    @Test
    void creerCommande_clientExistant_retourneCommande() {
        CreerCommandeClientRequest req = new CreerCommandeClientRequest();
        req.setClientId(1L);  // Alice Dupont (dans data.sql)
        req.setLignes(List.of(ligne(1L, 1)));  // 1 Quiche Lorraine

        CommandeClient commande = commandeClientService.creer(req);

        assertNotNull(commande.getId());
        assertEquals("EN_ATTENTE", commande.getStatut());
        assertEquals(1, commande.getLignes().size());
    }

    @Test
    void preparerCommande_stockSuffisant_decrementeStock() {
        // Créer une commande
        CreerCommandeClientRequest req = new CreerCommandeClientRequest();
        req.setClientId(1L);
        req.setLignes(List.of(ligne(2L, 1)));  // 1 Omelette aux lardons

        CommandeClient commande = commandeClientService.creer(req);
        double stockAvant = ingredientDao.findById(2L).get().getStockActuel(); // oeufs

        commandeClientService.passerEnPreparation(commande.getId());

        double stockApres = ingredientDao.findById(2L).get().getStockActuel();
        assertEquals(stockAvant - 3.0, stockApres, 0.001); // 3 oeufs consommés
    }

    @Test
    void servir_commandeEnPreparation_statutSERVIE() {
        CreerCommandeClientRequest req = new CreerCommandeClientRequest();
        req.setClientId(2L);
        req.setLignes(List.of(ligne(2L, 1)));
        CommandeClient commande = commandeClientService.creer(req);
        commandeClientService.passerEnPreparation(commande.getId());

        CommandeClient servie = commandeClientService.servir(commande.getId());

        assertEquals("SERVIE", servie.getStatut());
    }

    @Test
    void findAll_filtreParStatut_retourneSeulementCeStatut() {
        // Crée et prépare une commande
        CreerCommandeClientRequest req = new CreerCommandeClientRequest();
        req.setClientId(1L);
        req.setLignes(List.of(ligne(2L, 1)));
        CommandeClient commande = commandeClientService.creer(req);
        commandeClientService.passerEnPreparation(commande.getId());

        List<CommandeClient> enPreparation = commandeClientService.findAll("EN_PREPARATION");

        assertTrue(enPreparation.stream().allMatch(c -> "EN_PREPARATION".equals(c.getStatut())));
    }

    @Test
    void supprimerCommande_existante_disparaitDeLaListe() {
        CreerCommandeClientRequest req = new CreerCommandeClientRequest();
        req.setClientId(3L);
        req.setLignes(List.of(ligne(1L, 1)));
        CommandeClient commande = commandeClientService.creer(req);

        commandeClientService.supprimer(commande.getId());

        assertThrows(ResourceNotFoundException.class,
            () -> commandeClientService.findById(commande.getId()));
    }

    @Test
    void alertesStock_ingredientSousSeuil_estRetourne() {
        // Forcer un stock sous le seuil (oeufs : seuil=6, baisser à 2)
        ingredientDao.decrementerStock(2L, 22.0); // 24 - 22 = 2 oeufs restants

        List<AlerteStockDto> alertes = stockService.getAlertes();

        assertTrue(alertes.stream().anyMatch(a -> a.getIngredientId().equals(2L)));
    }

    // ---- Cas d'erreur (obligatoires) ----

    @Test
    void creerCommande_clientInexistant_leveResourceNotFoundException() {
        CreerCommandeClientRequest req = new CreerCommandeClientRequest();
        req.setClientId(9999L);  // client inexistant
        req.setLignes(List.of(ligne(1L, 1)));

        assertThrows(ResourceNotFoundException.class,
            () -> commandeClientService.creer(req));
    }

    @Test
    void preparerCommande_stockInsuffisant_leveStockInsuffisantException() {
        // Vider totalement le stock d'oeufs
        ingredientDao.decrementerStock(2L, 24.0);

        CreerCommandeClientRequest req = new CreerCommandeClientRequest();
        req.setClientId(1L);
        req.setLignes(List.of(ligne(1L, 1)));  // Quiche Lorraine nécessite 3 oeufs
        CommandeClient commande = commandeClientService.creer(req);

        assertThrows(StockInsuffisantException.class,
            () -> commandeClientService.passerEnPreparation(commande.getId()));
    }

    // Helper pour créer une LigneRequest
    private CreerCommandeClientRequest.LigneRequest ligne(Long platId, int qte) {
        CreerCommandeClientRequest.LigneRequest l = new CreerCommandeClientRequest.LigneRequest();
        l.setPlatId(platId);
        l.setQuantite(qte);
        return l;
    }
}
```

---

## Récapitulatif des fichiers à créer

```
src/main/java/fr/epf/restaurant/
├── model/
│   ├── Client.java
│   ├── Plat.java
│   ├── PlatIngredient.java
│   ├── Ingredient.java
│   ├── Fournisseur.java
│   ├── CommandeClient.java
│   ├── LigneCommandeClient.java
│   ├── CommandeFournisseur.java
│   └── LigneCommandeFournisseur.java
├── dto/
│   ├── CreerCommandeClientRequest.java
│   ├── CreerCommandeFournisseurRequest.java
│   ├── AlerteStockDto.java
│   ├── RecommandationDto.java
│   └── PreparationResultDto.java
├── exception/
│   ├── AppException.java
│   ├── ResourceNotFoundException.java
│   ├── StockInsuffisantException.java
│   └── StatutInvalideException.java
├── dao/
│   ├── ClientDao.java
│   ├── PlatDao.java
│   ├── IngredientDao.java
│   ├── FournisseurDao.java
│   ├── CommandeClientDao.java
│   └── CommandeFournisseurDao.java
├── service/
│   ├── StockService.java
│   ├── CommandeClientService.java
│   └── CommandeFournisseurService.java
└── controller/
    ├── GlobalExceptionHandler.java   ← à faire en premier dans ce package
    ├── ClientController.java
    ├── PlatController.java
    ├── IngredientController.java
    ├── FournisseurController.java
    ├── CommandeClientController.java
    └── CommandeFournisseurController.java

src/test/java/fr/epf/restaurant/
└── CommandeClientServiceTest.java    (+ autres tests)
```

---

## Points d'attention

- **Imports** : `@Repository`, `@Service`, `@RestController`, `@Transactional` viennent tous de Spring. Les annotations HTTP (`@GetMapping`, etc.) viennent de `org.springframework.web.bind.annotation`.
- **JdbcTemplate** : toujours utiliser `?` pour les paramètres, jamais de concaténation de chaînes (risque SQL injection).
- **`@Transactional`** : à mettre sur les méthodes de service qui font plusieurs opérations BDD (si l'une échoue, tout est annulé).
- **Ambiguïté de colonnes** : en SQL avec JOIN, si deux tables ont une colonne `id`, utiliser un alias : `SELECT cc.id AS commande_id, c.id AS client_id, ...` puis `rs.getLong("commande_id")`.
- **`findAll(String statut)`** : si `statut == null`, retourner tout ; sinon filtrer. Utiliser un `if` pour construire la requête SQL ou passer par une condition `WHERE statut = ?`.
