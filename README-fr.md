# ![bookmark-img](bookmark.png) Marque-pages pour Freeplane

Ce module complémentaire (add-on) permet de mettre des marque-pages sur des noeuds d'une carte Freeplane, et de passer facilement de l'un à l'autre.

Les marque-pages sont de deux sortes :

- Des **marque-pages standard**, signalés sur la carte par l'icône de marque-page violette.
- Des **marque-pages nommés** (associés à un raccourci clavier), signalés sur la carte par l'icône de marque-page verte.

Ce module crée un nouveau menu nommé "Marque-pages" dans la barre de menu. On y trouve les actions disponibles :

- **Placer/Supprimer un marque-page** : Placer un marque-page sur le nœud sélectionné, ou en effacer le marque-page. Pour la création on choisit alors si on veut affecter un raccourci clavier au marque-page.
- **Atteindre un marque-page** : Sauter à un marque-page déjà défini.
- **Basculer un marque-page** : Selon l'état du nœud
  - convertit un marque-page nommé en marque-page standard
  - effacer un marque-page standard
  - place un marque-page standard
  
En plus de ces 3 actions principales, il est aussi possible de créer facilement des liens vers ou dans les nœuds marqués.

N'hésitez pas à contribuer et à [signaler des erreurs](../../issues).

# Installation

### Avertissement

J'utilise ce module pour mon usage personnel depuis un an sans aucun problème, mais je préfère prévenir et déclarer : à utiliser à son propre risque.

Pourquoi cette précaution ? Pour ces deux choses détaillées ci-dessous :

- Le module modifie légèrement le fonctionnement de FreePlane, pour s'assurer que l'ensemble des marque-pages nommé reste cohérent. Il doit être impossible pour l'utilisateur d'ajouter de la façon classique l'icône de marque-page nommé à un nœud, car sinon il n'aurait pas de raccourci clavier associé. Il doit aussi être impossible de copier un marque-page nommé en même temps que son nœud, car ces marque-pages doivent être uniques. Dans ce cas le nœud est donc copié sans son marque-page.  
Pour cela, un peu de code est exécuté chaque fois que l'icône d'un nœud est modifiée par l'utilisateur, et chaque fois qu'un nouveau nœud est créé. Dans l'absolu, ceci pourrait avoir une conséquence négative sur le fonctionnement de FreePlane.

- Le module modifie une carte mentale quand on l'utilise, en y ajoutant des icônes, et en stockant dans le fichier de la carte (dans la *map storage area*) les raccourcis claviers associés aux marque-pages nommés. Ces données sont sauvegardées directement dans le fichier de la carte. Au pire, on peut donc envisager le risque qu'un bug dans le module en vienne à corrompre le fichier de la carte.

Je le redis : je n'ai eu aucun problème en un an d'utilisation. Mais tous les retours sur ces points particuliers sont les bienvenus !

### Méthode 1

- Faire un clic droit sur le fichier `bookmarks.addon.mm` ci-dessus. Choisir "Enregistrer la cible sous...", ou quelque chose d'approchant (selon votre navigateur).
- Ouvrir le fichier téléchargé avec Freeplane, et suivez les instructions.

### Méthode 2

- [Télécharger la dernière release](../../releases) sur son  ordinateur et extraire les fichiers.
- Parmi les fichiers extraits, ouvrir le fichier nommé "bookmarks.addon.mm" avec Freeplane, et suivez les instructions.


