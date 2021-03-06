# ing-sw-2019-02
**Gruppo 02**

Prova Finale di Ingegneria del Software, Politecnico di Milano, 2019

La prova consiste nell'implementazione in Java del gioco da tavolo Adrenalina di Cranio Creations.

**Funzionalità implementate:**
- Regole complete
- Connessione RMI (compatibilità con socket - ready to implement)
- GUI
- FA: Gestione partite multiple (multimatch)


**Guida all' avvio dei Jar:**

Tutti i Jar eseguibili si trovano nella cartella di progetto "release"

**AdrenalineClient**: 

Distribuito in 3 versioni differenti a seconda del SO (causa dipendenza JavaFX). Nota bene: il jar eseguibile per il client è unico e uguale in tutte e 3 le cartelle di "distribuzione", che cambia è solo la cartella di JavaFX.

All'interno di ogni cartella di distribuzione si trovano:

- L'eseguibile **AdrenalineClient.jar**
- La cartella di **dipendenza JavaFX**: "javafx-sdk-12.0.1"
- Uno script **launcher.sh** o **launcher.bat** a seconda del SO in uso

   
**AdrenalineServer**:

Nella cartella di distribuzione si trova:

- L'eseguibile **AdrenalineServer.jar**
- Il file di properties: **adrenaline.properties**

Per eseguire il jar è necessario lanciare da terminale uno dei due seguenti comandi:


    java -jar AdrenalineServer.jar portNumber
    java -jar AdrenalineServer.jar
    
Nel secondo caso il valore della porta su cui lanciare il server sarà caricato dal file di properties. Viene incluso nel Jar del server anche un launcher.sh per semplificare l'avvio.
