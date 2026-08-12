# CraftpickFiltrer

CraftpickFiltrer est un plugin public de modération du chat pour les serveurs Bukkit, Spigot et Paper. Il bloque les mots interdits, les expressions personnalisées, les liens, les abus de majuscules et le spam, tout en alertant discrètement le staff.

## Compatibilité

- Minecraft 1.8.8 à 1.8.9
- Minecraft 1.8.9 à 1.20.x
- Minecraft 1.21.x
- Minecraft 26.2

Le plugin utilise uniquement l'API Bukkit historique afin de conserver une grande compatibilité. Les builds GitHub Actions sont testés avec les versions de Java adaptées à chaque génération de serveur.

## Fonctionnalités

- Liste de mots interdits configurable
- Expressions régulières personnalisées
- Blocage des liens et publicités
- Anti-spam avec délai configurable
- Détection des messages en majuscules
- Alertes réservées au staff
- Permission de contournement
- Rechargement sans redémarrage
- Aucun addon ni base de données

## Installation

1. Téléchargez le JAR correspondant à votre serveur depuis les artefacts GitHub Actions.
2. Placez-le dans le dossier `plugins`.
3. Redémarrez le serveur.
4. Modifiez `plugins/CraftpickFiltrer/config.yml` puis exécutez `/filtre reload`.

## Commandes et permissions

| Commande/permission | Description |
|---|---|
| `/filtre reload` | Recharge la configuration |
| `craftpickfiltrer.admin` | Autorise la commande de rechargement |
| `craftpickfiltrer.bypass` | Ignore le filtre |
| `craftpickfiltrer.alerts` | Reçoit les alertes du filtre |

## Compilation

```bash
mvn clean package
```

Le JAR est créé dans `target/`. Chaque push ou pull request déclenche aussi les quatre builds dans `.github/workflows/build.yml`.

## Description publique

> Filtre de chat Minecraft léger, configurable et multi-version. Bloquez insultes, liens, spam et majuscules, avec alertes staff et permissions, de Minecraft 1.8.8 à 26.2.

## Licence

Ce projet est distribué sous la licence présente dans [LICENSE](LICENSE).
