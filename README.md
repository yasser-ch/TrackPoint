# 📍 TrackPoint — Lab 11 : Localisation d'un Smartphone et Envoi des Coordonnées

## Objectif

Construire une application Android capable de **récupérer la position GPS** d'un smartphone, d'**afficher les coordonnées** à l'écran et d'**envoyer les données** vers un serveur PHP/MySQL distant via une requête HTTP POST.

---

## Concepts Abordés

- Permissions Android (localisation, réseau, état du téléphone)
- `LocationManager` et `LocationListener`
- Mise à jour de position GPS en temps réel
- Envoi de requêtes HTTP POST avec **Volley**
- Intégration d'un backend PHP/MySQL
- Récupération de l'identifiant de l'appareil avec `TelephonyManager`

---

## Architecture du Système

```
Smartphone Android
       │
       │  HTTP POST (latitude, longitude, date, imei)
       ▼
Serveur PHP (createPosition.php)
       │
       │  INSERT INTO position
       ▼
Base de données MySQL (localisation)
```

---

## Aperçu de l'Application

**TrackPoint** est une application de géolocalisation avec une interface épurée sur fond vert clair :

| Élément              | Description                                      |
|---------------------|--------------------------------------------------|
| Titre               | Affiché en haut en vert                          |
| Carte de position   | Affiche latitude, longitude, altitude, précision |
| Statut              | Indique si l'envoi est en cours ou terminé       |

---

## Structure du Projet Android

```
TrackPoint/
├── java/com/example/trackpoint/
│   └── MainActivity.java
├── res/
│   ├── layout/
│   │   └── activity_main.xml
│   └── values/
│       ├── colors.xml
│       ├── strings.xml
│       └── themes.xml
└── AndroidManifest.xml
```

---

## Permissions Requises

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

---

## Détails Clés de l'Implémentation

### Écoute GPS
```java
locationManager.requestLocationUpdates(
    LocationManager.GPS_PROVIDER,
    60000,  // 60 secondes minimum entre deux mises à jour
    150,    // 150 mètres minimum de déplacement
    locationListener
);
```

### Envoi des Coordonnées avec Volley
```java
private void sendCoordinates(final double lat, final double lon) {
    StringRequest request = new StringRequest(
        Request.Method.POST,
        serverEndpoint,
        response -> tvStatus.setText(getString(R.string.send_success)),
        error -> tvStatus.setText(getString(R.string.send_error))
    ) { ... };
    requestQueue.add(request);
}
```

### Paramètres envoyés au serveur
| Paramètre      | Valeur                        |
|---------------|-------------------------------|
| `latitude`    | Coordonnée GPS nord-sud       |
| `longitude`   | Coordonnée GPS est-ouest      |
| `date_position` | Date et heure de la position |
| `imei`        | Identifiant de l'appareil     |

---

## Structure du Serveur PHP (référence)

```
localisation/
├── classe/Position.php
├── connexion/Connexion.php
├── dao/IDao.php
├── service/PositionService.php
└── createPosition.php
```

### Table MySQL
```sql
CREATE TABLE position (
    id INT AUTO_INCREMENT PRIMARY KEY,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    date_position DATETIME NOT NULL,
    imei VARCHAR(50) NOT NULL
);
```

---

## Choix de Design

- **Thème :** Clair / Vert Moderne
- **Palette de couleurs :** Vert foncé (`#2E7D32`), Vert clair (`#A5D6A7`), Fond (`#F1F8E9`)
- **Interface minimaliste** centrée sur l'affichage des données GPS

---

## Comment Exécuter

1. Cloner ou ouvrir le projet dans **Android Studio**
2. Vérifier que le Min SDK est défini à **24**
3. Lancer sur un émulateur ou un appareil physique (Android 7.0+)
4. Accepter les permissions de localisation au démarrage
5. Activer le GPS ou simuler une position via **Extended Controls → Location** sur l'émulateur

---

## Référence du Lab

- **Numéro du lab :** 11
- **Titre :** Localisation d'un Smartphone et Envoi des Coordonnées vers un Serveur Distant
- **Langage :** Java
- **Min SDK :** 24 (Android 7.0 Nougat)
- **Dépendance :** Volley 1.2.1
