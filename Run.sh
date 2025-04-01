#!/bin/bash

# Définir les répertoires contenant les fichiers .java
SRC_DIRS="src/Model src/Model/GameStruct src/Model/Machines src/geometry src/GUI src/GUI/Inv src/GUI/Reglage src/Helper src/Control"

# Compilation des fichiers .java
echo "Compilation des fichiers Java..."
javac $(find $SRC_DIRS -name "*.java")
if [ $? -ne 0 ]; then
    echo "Échec de la compilation. Sortie."
    exit 1
fi

# Exécution du programme
echo "Exécution du programme..."
cd src
java Model.GameStruct.Run
if [ $? -ne 0 ]; then
    echo "Erreur lors de l'exécution du programme. Sortie."
    cd ..
    exit 1
fi
cd ..

# Nettoyage des fichiers .class
echo "Nettoyage des fichiers .class..."
find $SRC_DIRS -name "*.class" -delete

echo "Terminé."
