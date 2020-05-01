# ![logo](doc/images/bookmark.png) Marque-pages pour Freeplane

[Read in english](README.md) ![langage flag](doc/images/english_flag_small.png)

Ce module complémentaire (add-on) permet de mettre des marque-pages sur des nœuds d'une carte Freeplane, et de passer facilement de l'un à l'autre.

Les marque-pages sont de deux sortes :

- Des **marque-pages standard**, signalés sur la carte par l'icône de marque-page violette.
- Des **marque-pages nommés** (associés à un raccourci clavier), signalés sur la carte par l'icône de marque-page verte.

![demo](doc/images/demo.gif)

Ce module crée un nouveau menu nommé "Marque-pages" dans la barre de menu. On y trouve les actions disponibles :

- **Placer/Supprimer un marque-page** : Placer un marque-page sur le nœud sélectionné, ou en effacer le marque-page. Pour la création on choisit alors si on veut affecter un raccourci clavier au marque-page.
- **Atteindre un marque-page** : Sauter à un marque-page déjà défini.
- **Basculer un marque-page** : Selon l'état du nœud
  - convertit un marque-page nommé en marque-page standard
  - effacer un marque-page standard
  - place un marque-page standard
  
En plus de ces 3 actions principales, il est aussi possible de créer facilement des liens vers ou dans les nœuds marqués.

## Retours et contributions

Tout commentaire sur ce module est grandement encouragé :smile:. Vos retours d'expérience, idées et avis permettent d'améliorer ce module.
- Vous pouvez écrire vos commentaires dans [la discussion dédiée](https://sourceforge.net/p/freeplane/discussion/758437/thread/ec280c4e/) sur le forum de Freeplane.
- Vous pouvez aussi [soumettre ici](../../issues) des rapports de boggue ou des suggestions d'amélioration.

De même, toute proposition d'amélioration du code et toute pull request est la très bienvenue. Le code du module est documenté, mais n'hésitez pas à me demander des précisions si besoin.

## Installation

- Télécharger le fichier *bookmarks.addon.mm* de [la dernière release](../../releases).
- Ouvrir ce fichier avec Freeplane, et suivre les instructions.

## Mise-à-jour

Pour mettre à jour le module il suffit d'installer la dernière version. Inutile de désinstaller la version précédente.

**Important :** Si vous avez créé des marque-pages avec une version de ce module antérieure à la version v0.5.1, vous devez mettre vos cartes contenant des marque-pages à jour. Pour cela utiliser le menu *"Marque-pages>Outils>Mettre à jour les marque-pages d'une version antérieure du module"*.

## Avertissement

J'utilise quotidiennement ce module pour mon usage personnel depuis un an sans aucun problème, mais je préfère prévenir et déclarer : à utiliser à ses propres risques.

Pourquoi cette précaution ? Pour ces deux choses détaillées ci-dessous :

- Le module modifie légèrement le fonctionnement de FreePlane, pour s'assurer que l'ensemble des marque-pages nommé reste cohérent. Il doit être impossible pour l'utilisateur d'ajouter de la façon classique l'icône de marque-page nommé à un nœud, car sinon il n'aurait pas de raccourci clavier associé. Il doit aussi être impossible de copier un marque-page nommé en même temps que son nœud, car ces marque-pages doivent être uniques. Dans ce cas le nœud est donc copié sans son marque-page.  
Pour cela, un peu de code est exécuté chaque fois que l'icône d'un nœud est modifiée par l'utilisateur, et chaque fois qu'un nouveau nœud est créé. Dans l'absolu, ceci pourrait avoir une conséquence négative sur le fonctionnement de FreePlane.

- Le module modifie une carte mentale quand on l'utilise, en y ajoutant des icônes, et en stockant dans le fichier de la carte (dans la *map storage area*) les raccourcis claviers associés aux marque-pages nommés. Ces données sont sauvegardées directement dans le fichier de la carte. Au pire, on peut donc envisager le risque qu'un bug dans le module en vienne à corrompre le fichier de la carte.

Je le redis : je n'ai eu aucun problème en un an d'utilisation. Mais tous les retours sur ces points particuliers sont les bienvenus !

