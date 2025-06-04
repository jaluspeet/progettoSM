# Relazione progetto Android

## Scopo del Progetto

Lo scopo di questo progetto è realizzare un'applicazione Android che permette di giocare a Sasso-Carta-Forbici contro l'applicazione, tramite riconoscimento dei gesti della mano rilevati dalla webcam del dispositivo.  
L’obiettivo è mettere in pratica principi di progettazione del software, modularità e riusabilità del codice, sfruttando le potenzialità di Kotlin e tutte le tecniche viste durante il corso.

## Funzionalità principali

- **Login con Google:**  
  L'app utilizza l'integrazione delle Google Sign-In API per consentire agli utenti di accedere in modo semplice e sicuro. Dopo il login, ogni utente ha accesso a uno storico personale delle partite che sono state disputate.

- **Accesso alla fotocamera:**  
  Dopo aver ottenuto i permessi, l'app attiva la fotocamera per acquisire in tempo reale l'immagine della mano dell'utente.

- **Interfaccia moderna:**  
  L’interfaccia è realizzata in Jetpack Compose, che permette una UI moderna, responsive e facilmente manutenibile.

- **Riconoscimento dei gesti con PyTorch:**  
  Viene utilizzato un modello preallenato in PyTorch per classificare il gesto mostrato nella fotocamera come sasso, carta o forbice.

- **Storico delle partite:**  
  Le partite vengono salvate in locale. Ogni partita registra la mossa del giocatore, quella del computer e il conseguente esito. Lo storico è sempre accessibile tramite una sezione dedicata della UI, realizzata in Jetpack Compose.

## Descrizione della struttura dei sorgenti

La struttura principale della progetto è la seguente 

```
progettoSM/
│
├── app/
│   └── MainActivity.kt
│
├── common/
│   ├── Game.kt
│   ├── Login.kt
│   └── Permission.kt
│
└── feature/
    ├── camera.kt
    ├── scoreboard.kt
    └── settings.kt
```
 ## APP
 La cartella `app` rappresenta il modulo principale dell’applicazione Android. All’interno di questa cartella si trovano:
  - **build.gradle.kts** e file di configurazione per la build
  - **src/** che contiene il codice sorgente vero e proprio
  - Nel percorso `progettoSM/app/src/main/java/com/example/pingu/MainActivity.kt` troviamo il file principale dell’applicazione:
   **MainActivity.kt**
    
    Il file `MainActivity.kt` rappresenta il punto di ingresso dell’applicazione  e  Gestisce la 
    navigazione tra le tre schermate principali dell’app:
    
    - **Camera**: per giocare tramite la fotocamera (serve permesso).
    - **Scoreboard**: mostra una lista di partite giocate 
    - **Settings**: permette di accedere alle impostazioni di sistema dell'app

## FEATURE
  La cartella feature rappresenta tutte quelle funzionalità tangibili, che l'utente può usare per sfruttare l'applicazione.
  è composto da :

  #### 1. `camera/`
  Questa sottocartella contiene il codice relativo alla gestione della fotocamera e al riconoscimento dei gesti della mano.  
  *Ruolo principale:**
- Attivare la fotocamera del dispositivo.
- Acquisire in tempo reale le immagini della mano dell’utente.
- Interfacciarsi con il modello PyTorch per riconoscere il gesto (sasso, carta o forbice).
- Fornire una UI reattiva e accessibile tramite Jetpack Compose per la parte di gioco.

#### 2. `scoreboard/`
  Questa sottocartella contiene la logica e la UI per la gestione dello storico delle partite e dei risultati.  
  **Ruolo principale:**
  - Salvare localmente i dati di ogni partita giocata (mossa utente, mossa computer, esito).
  - Visualizzare lo storico delle partite tramite un’interfaccia dedicata.
  - Permettere all’utente di consultare le proprie performance nel tempo.

#### 3. `settings/`
  Questa sottocartella gestisce le impostazioni e le preferenze dell’utente.  
  **Ruolo principale:**
  - Offrire una schermata dove l’utente può modificare le proprie preferenze ( tema, lingua, ecc.).
  - Gestire eventuali configurazioni relative al login e alla gestione dei permessi.

---

## COMMON
   questa sottocartella contiene codice riutilizzabile e condiviso tra più parti dell'app, come utility, gestione delle permissioni, autenticazione (es. Google Sign-In), 
   estensioni e costanti, per mantenere il progetto organizzato e modulare.



#### 1. `login/`
  Questa sottocartella gestisce tutte le funzionalità relative all’autenticazione degli utenti, in particolare tramite Google Sign-In.

  - Implementare il flusso di login con Google, gestendo la richiesta, la risposta e il salvataggio delle informazioni dell’utente autenticato.
  - Fornire funzioni o componenti riutilizzabili per mostrare lo stato di autenticazione e gestire la sessione utente.
 

#### 2. `permission/`
   Questa sottocartella centralizza la logica per la gestione dei permessi Android, in particolare per l’accesso alla fotocamera.  
  - Gestire la richiesta di permessi runtime come CAMERA.
  - Fornire utilities per controllare lo stato dei permessi e reagire ai cambiamenti (es. permesso negato, permanentemente negato, ecc.).
  

## Punti di forza

### 1. Architettura modulare evoluta
Il progetto è organizzato in moduli distinti (`app`, `feature`, `common`), ciascuno con responsabilità chiare. Questa suddivisione facilita la scalabilità, la manutenzione e il lavoro collaborativo, permettendo di aggiungere o modificare funzionalità senza impattare l’intero sistema.

### 2. Centralizzazione e riuso delle logiche comuni
Il modulo `common` raccoglie codice condiviso e riutilizzabile (es. gestione permessi, login, logica di gioco), riducendo la duplicazione e rendendo più semplice la manutenzione e l’evoluzione del progetto.

### 3. Utilizzo di Jetpack Compose per la UI
L’interfaccia è realizzata interamente con Jetpack Compose, garantendo una UI dichiarativa, moderna, facilmente reattiva e testabile. L’adozione di pattern come `Scaffold` e `NavigationBar` permette una navigazione fluida e coerente.

### 4. Integrazione di machine learning su dispositivo mobile
Il progetto integra un modello PyTorch per il riconoscimento dei gesti tramite la fotocamera, mostrando la capacità di eseguire inferenza ML direttamente su dispositivi mobili.

### 5. Autenticazione sicura con Google Sign-In
L’autenticazione tramite Google Sign-In offre sicurezza, praticità e una gestione personalizzata dello storico partite per ogni utente.


## Possibili migliorie

- Scrittura di una documentazione più dettagliata per ogni modulo/feature.
- Aggiunta di test automatici per migliorare l’affidabilità del software.
- Miglioramento delle interfacce utente e dell’esperienza d’uso.
- Ottimizzazione delle performance nelle parti critiche dell’applicazione.
- Introduzione di strumenti di analisi statica del codice e code coverage.
- Possibile migrazione o supporto a ulteriori piattaforme.

---
