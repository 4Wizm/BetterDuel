================================================================
                          BETTERDUEL
================================================================
Versione : 1.0.1
Autore   : 4wizm
API      : Spigot/Paper 1.21+
Descrizione: Sistema di duelli realistici con kit personalizzabili,
             matchmaking basato sulle statistiche, leaderboard e
             scoreboard in tempo reale.
================================================================


----------------------------------------------------------------
                        CARATTERISTICHE
----------------------------------------------------------------
- Sistema di duelli 1v1 con countdown e PvP controllato
- Matchmaking intelligente basato sul numero di kill dei giocatori
- Kit personalizzabili da config (armature, armi, offhand, cibo)
- Statistiche complete (duelli, vittorie, sconfitte, kill, death, KDR)
- Leaderboard su database MySQL con classifica top 10
- Menu GUI interattivo per sfidare i giocatori in coda
- Scoreboard dinamica durante il duello (HP avversario, kit, ecc.)
- Salvataggio inventario pre-duello e ripristino completo post-duello
- Arena dedicata generata automaticamente se assente
- Ricompense configurabili al vincitore (comandi console)
- Protezione arena (blocco rottura/piazzamento blocchi)


----------------------------------------------------------------
                        INSTALLAZIONE
----------------------------------------------------------------
1. Scarica il file BetterDuel-1.0.1.jar
2. Metti il file nella cartella plugins/ del tuo server
3. Riavvia il server (o usa /reload)
4. Configura il file plugins/BetterDuel/config.yml
5. Configura il database MySQL (opzionale ma raccomandato)
6. Usa /duel setspawn 1 <arena> e /duel setspawn 2 <arena>
   per impostare gli spawn delle arene


----------------------------------------------------------------
                           COMANDI
----------------------------------------------------------------

--- Comando principale: /duel ---
Alias: /bd, /betterduel

/duel                          Apre il menu duelli
/duel menu                     Apre il menu duelli
/duel queue <kit>              Entra in coda con il kit specificato
/duel leave                    Esce dalla coda o dal duello
/duel quit                     Alias di leave
/duel stats [player]           Statistiche tue o di un altro giocatore
/duel kits                     Lista kit disponibili con stato permesso
/duel setspawn <1|2> <arena>   Imposta spawn 1 o 2 di un'arena  [ADMIN]
/duel reload                   Ricarica la configurazione       [ADMIN]


--- Comando: /duels ---
Alias: /duelmenu, /duellist

/duels                          Apre il menu duelli


--- Comando: /duelleaderboard ---
Alias: /dtop, /duelrank

/duelleaderboard                        Classifica per vittorie (default)
/duelleaderboard <duelli|vittorie|sconfitte|kill>   Classifica specifica


----------------------------------------------------------------
                          PERMESSI
----------------------------------------------------------------

betterduel.use              Uso base del plugin (menu duelli)     [true]
betterduel.queue            Entrare in coda per un duello         [true]
betterduel.kit.soldato      Accesso al kit Soldato                [true]
betterduel.kit.tank         Accesso al kit Tank                   [op]
betterduel.kit.assassino    Accesso al kit Assassino              [op]
betterduel.admin            Comandi admin + tutti i kit           [op]
betterduel.stats.others     Statistiche di altri giocatori        [op]

NOTA: I kit personalizzati aggiunti in config.yml generano
      automaticamente il permesso betterduel.kit.<nome-kit>


----------------------------------------------------------------
                        CONFIGURAZIONE
----------------------------------------------------------------

--- Sezione impostazioni ---
impostazioni:
  mondo-duelli: "betterduel_arena"     # Mondo dedicato ai duelli
  crea-mondo-se-assente: true          # Crea il mondo se non esiste
  tempo-attesa-coda-secondi: 30        # (non usato attualmente)
  ritorno-dopo-secondi: 2              # Secondi prima del ripristino


--- Sezione arena ---
arena:
  blocco-secondi: 3                    # Durata del countdown
  blocco-messaggio: "..."              # Messaggio countdown (%tempo%)
  blocco-messaggio-fine: "..."         # Messaggio di inizio
  blocca-rompere-blocchi: true         # Blocca rottura blocchi
  blocca-piazzare-blocchi: true        # Blocca piazzamento blocchi
  blocca-danno-durante-countdown: true # Blocca PvP durante countdown
  elenco:                              # Arene definite con /duel setspawn
    arena1:
      spawn1: { x, y, z, yaw, pitch }
      spawn2: { x, y, z, yaw, pitch }


--- Sezione matchmaking ---
matchmaking:
  abilitato: true
  range-kill-iniziale: 5               # Diff kill max (< 50 kill)
  range-kill-medio: 10                 # Diff kill max (50-200 kill)
  range-kill-alto: 25                  # Diff kill max (> 200 kill)
  soglia-kill-medio: 50
  soglia-kill-alto: 200
  tempo-allargamento-range: 20         # Secondi dopo cui si allarga range


--- Sezione ricompense ---
ricompense:
  abilitato: true
  comandi:
    - "eco give %player% 100"
    - "xp add %player% 50 points"
  messaggio:
    - "&a&lHAI VINTO!"


--- Sezione gui ---
titolo-menu       Titolo dell'inventario
righe             Altezza del menu (1-6)
nessun-giocatore  Item mostrato se la coda è vuota
giocatore         Item per ogni giocatore in coda
sfida-diretta     Pulsante per sfida rapida


--- Sezione scoreboard ---
scoreboard:
  abilitata: true
  titolo: "&6&lBETTERDUEL"
  righe:
    - "&7&m----------------"
    - "&eAvversario: &f%avversario%"
    - "&7"
    - "&aTuoi HP: &f%tuoi_hp%"
    - "&cSuoi HP: &f%suoi_hp%"
    - "&7"
    - "&eKit: &f%kit%"
    - "&7&m----------------"


--- Sezione titoli ---
Personalizza i title mostrati a fine duello (vittoria/sconfitta)
con: titolo, sottotitolo, fade-in, stay, fade-out


----------------------------------------------------------------
                      KIT PREDEFINITI
----------------------------------------------------------------

SOLDATO
  Icona    : IRON_SWORD
  Contenuto: Spada ferro, armatura ferro completa,
             4 mele d'oro, 16 bistecche
  Permesso : betterduel.kit.soldato

TANK
  Icona    : DIAMOND_CHESTPLATE
  Contenuto: Spada diamante, armatura diamante completa,
             8 mele d'oro, scudo, 32 bistecche
  Permesso : betterduel.kit.tank

ASSASSINO
  Icona    : NETHERITE_SWORD
  Contenuto: Spada netherite, armatura mista,
             4 perle di ender, 6 mele d'oro, 24 bistecche
  Permesso : betterduel.kit.assassino


--- Aggiungere un kit personalizzato ---
kit:
  mio_kit:
    nome: "&dMio Kit"
    permesso: "betterduel.kit.mio_kit"
    icona: "NETHERITE_SWORD"
    contenuto:
      - "NETHERITE_SWORD:1"
      - "NETHERITE_HELMET:1"
      - "NETHERITE_CHESTPLATE:1"
      - "NETHERITE_LEGGINGS:1"
      - "NETHERITE_BOOTS:1"
      - "GOLDEN_APPLE:16"
      - "COOKED_BEEF:64"

Formato item: MATERIALE:QUANTITA
Le armature vengono equipaggiate automaticamente,
lo scudo va in offhand.


----------------------------------------------------------------
                          DATABASE
----------------------------------------------------------------
Il plugin supporta MySQL tramite HikariCP per statistiche
persistenti.

database:
  abilitato: true
  host: "localhost"
  porta: 3306
  nome: "betterduel"
  utente: "root"
  password: "password"
  pool-size: 10

Se abilitato: false, le statistiche vengono tenute solo in
memoria (persa al riavvio) e la leaderboard non funzionera.

La tabella betterduel_stats viene creata automaticamente al
primo avvio.


----------------------------------------------------------------
                    PLACEHOLDER MESSAGGI
----------------------------------------------------------------

%player%          GUI, ricompense
%kit%             Messaggi coda, GUI, scoreboard
%tempo%           Countdown
%avversario%      Messaggi duello, scoreboard
%vincitore%       Messaggi fine duello
%perdente%        Messaggi fine duello
%tipo%            Comando setspawn
%arena%           Comando setspawn
%duelli%          Statistiche
%vittorie%        Statistiche
%sconfitte%       Statistiche
%kill%            Statistiche
%death%           Statistiche
%kdr%             Statistiche
%tuoi_hp%         Scoreboard
%suoi_hp%         Scoreboard
%giocatore%       Statistiche

I colori usano il formato & (es. &a, &l, &c).


----------------------------------------------------------------
                          SUPPORTO
----------------------------------------------------------------
Per bug, suggerimenti o richieste: contatta freegean su discord

              Buon divertimento con BetterDuel!
================================================================
