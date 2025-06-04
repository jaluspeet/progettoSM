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

La struttura principale della repository è la seguente 

- **app/**: contiene il modulo principale dell’applicazione.
- **feature/**: contiene funzionalità specifiche e modulari dell’applicazione, suddivise per caratteristiche.
- **common/**: contiene codice condiviso, utility e componenti riutilizzabili tra più moduli o feature.

## Punti di forza

- **Architettura modulare:** Separazione tra codice comune e funzionalità specifiche tramite le cartelle `common` e `feature`.
- **Gestione delle dipendenze e build automatizzata** tramite Gradle e relative configurazioni.
- **UI moderna:** Utilizzo di Jetpack Compose per un’interfaccia reattiva e di semplice manutenzione.
- **Integrazione di tecnologie avanzate:** Uso di PyTorch per il riconoscimento dei gesti e delle Google Sign-In API per l’autenticazione.
- **Buona base per scalabilità:** La suddivisione in moduli permette di aggiungere facilmente nuove feature o estendere quelle esistenti.

## Possibili migliorie

- Scrittura di una documentazione più dettagliata per ogni modulo/feature.
- Aggiunta di test automatici per migliorare l’affidabilità del software.
- Miglioramento delle interfacce utente e dell’esperienza d’uso.
- Ottimizzazione delle performance nelle parti critiche dell’applicazione.
- Introduzione di strumenti di analisi statica del codice e code coverage.
- Possibile migrazione o supporto a ulteriori piattaforme.

---
