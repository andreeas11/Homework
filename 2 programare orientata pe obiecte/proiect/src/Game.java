import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Game {
    PokemonFactory factory = new PokemonFactory();
    ObiectFactory obiectFactory = new ObiectFactory();
    Trainer trainer1, trainer2;
    static final Logger LOGGER = Logger.getAnonymousLogger();
    static Random random = new Random();
    public static Game game;

    public static Game Instance() { //folosim singleton
        if (game == null) game = new Game();
        return game;
    }

    public void parse() throws IOException { // citeste din file si initializeaza cei 2 traineri apeland parseTrainer
        File file = new File("input/1.in");
        BufferedReader br;
        br = new BufferedReader(new FileReader(file));
        trainer1 = parseTrainer(br);
        trainer2 = parseTrainer(br);
    }

    public Trainer parseTrainer(BufferedReader br) throws IOException { //initializeaza numele, varsta si pokemonii (apeland parsePokemon) ai unui trainer
        Trainer trainer = new Trainer();
        trainer.name = br.readLine();
        trainer.age = Integer.parseInt(br.readLine());
        for (int i = 0; i < 3; i++) {
            trainer.pokemons.add(i, parsePokemon(br, trainer));
        }
        return trainer;
    }

    public Pokemon parsePokemon(BufferedReader br, Trainer trainer) throws IOException { //initializeaza un pokemon (folosind factoryul si obiectele lui)
        PokemonType type = PokemonType.valueOf(br.readLine());
        Pokemon pokemon = factory.createPokemon(type);
        pokemon.trainer = trainer;
        for (int i = 0; i < 3; i++) {
            ObiectType obiectType = ObiectType.valueOf(br.readLine());
            pokemon.objects[i] = obiectFactory.createObiect(obiectType);
        }
        return pokemon;
    }

    public static int random(int i) { // returneaza un nr random intre 0 si i (inclusiv 0; fara i)
        return random.nextInt(i);
    }

    void playGame(Trainer trainer1, Trainer trainer2) { //apeleaza arena pentru cele 3 perechi de pokemoni, iar apoi meciul intre cei mai buni pokemoni
        for (int i = 0; i < 3; i++) {
            Pokemon pokemon1 = trainer1.pokemons.get(i);
            Pokemon pokemon2 = trainer2.pokemons.get(i);
            pokemon1.initObiectCombined();
            pokemon2.initObiectCombined();
            while (pokemon1.liveHp > 0 && pokemon2.liveHp > 0) { // se va apela playArena pana cand unul dintre pokemoni ramane fara hp
                pokemon1.resetStats(); //se reseteaza liveHp si usedCoolDown dupa fiecare match
                pokemon2.resetStats();
                playArena(pokemon1, pokemon2);
            }
        }
        Pokemon bestPokemon1 = trainer1.bestPokemon(); //se calculeaza cel mai bun pokemon al fiecarui antrenor
        Pokemon bestPokemon2 = trainer2.bestPokemon();
        bestPokemon1.liveHp = bestPokemon1.hp;
        bestPokemon2.liveHp = bestPokemon2.hp;
        playMatch(bestPokemon1, bestPokemon2); //meciul intre cei mai buni pokemoni
    }

    void playArena(Pokemon pokemon1, Pokemon pokemon2) { //se va alege random tipul meciului (cu neutreli sau duel) si se apeleaza playMatch pt desfasurarea lui
        int matchType = random(3);
        switch (matchType) {
            case 0, 1 -> {
                PokemonType type = matchType == 0 ? PokemonType.Neutrel1 : PokemonType.Neutrel2;
                Pokemon p1 = factory.createPokemon(type); //crearea neutrelilor (si a obiectului corespunzator)
                Pokemon p2 = factory.createPokemon(type);
                p1.obiectCombined = obiectFactory.createObiect(ObiectType.obiectNeutreli);
                p2.obiectCombined = obiectFactory.createObiect(ObiectType.obiectNeutreli);
                playMatch(pokemon1, p1);
                playMatch(pokemon2, p2);
            }
            case 2 -> playMatch(pokemon1, pokemon2);
        }
    }

    void playMatch(Pokemon pokemon1, Pokemon pokemon2) { //apeleaza playRound pana ramane un pokemon fara HP, iar apoi apeleaza increaseWinnerStats pt castigator
        while (pokemon1.liveHp > 0 && pokemon2.liveHp > 0) {
            playRound(pokemon1, pokemon2);
        }
        if (pokemon1.hp > 0) increaseWinnerStats(pokemon1);
        else if (pokemon2.hp > 0) increaseWinnerStats(pokemon2);
        else LOGGER.info("DRAW\n");
    }

    void increaseWinnerStats(Pokemon pokemon) { //creste staturile castigatorului + afisarea
        if (pokemon.type != PokemonType.Neutrel1 && pokemon.type != PokemonType.Neutrel2) {
            ++pokemon.hp;
            ++pokemon.attack;
            ++pokemon.defense;
            ++pokemon.specialDefense;
        }
        LOGGER.info("Antrenorul 1 (" + pokemon.type + ") castiga lupta.\nAtribute " + pokemon.type + ": HP " + pokemon.hp +
                ", Attack: " + pokemon.attack + ", Defense " + pokemon.defense + ", Special Defense " + pokemon.specialDefense + "\n");
    }

    void playRound(Pokemon pokemon1, Pokemon pokemon2) {
        pokemon1.lowerUsedCoolDown();
        pokemon2.lowerUsedCoolDown();
        pokemon1.calcNextMove(); //calculam mutarea pokemonului 1
        pokemon2.calcNextMove(); //daca pokemonul e un neutrel acesta va ataca de fiecare data (nu are abilitati), daca nu e, calculam mutarea cu getMove

        pokemon1.setAdversary(pokemon2);
        pokemon2.setAdversary(pokemon1);
//        pokemon1.useMove(pokemon2);
//        pokemon2.useMove(pokemon1);

        String attackType1 = attackType(pokemon1);
        String attackType2 = attackType(pokemon2);

        LOGGER.info("\n\n" + pokemon1.type + " " + attackType1 + " / " + pokemon2.type + " " + attackType2 + " -> Rezultat:\n");
        LOGGER.info(pokemon1.type + " HP: " + pokemon1.liveHp);
        roundDisplayAbility(pokemon1);
        LOGGER.info(pokemon2.type + " HP: " + pokemon2.liveHp);
        roundDisplayAbility(pokemon2);
    }

    void roundDisplayAbility(Pokemon pokemon) { //afiseaza abilitatile care au fost folosite si inca nu pot fi folosite din nou
        if (pokemon.ability1 == null) {
            LOGGER.info("\n");
            return;
        }
        if (pokemon.ability1.usedCoolDown != 0)
            LOGGER.info("; abilitatea 1 cooldown: " + pokemon.ability1.usedCoolDown);
        if (pokemon.ability2 == null) return;
        if (pokemon.ability2.usedCoolDown != 0)
            LOGGER.info("; abilitatea 2 cooldown: " + pokemon.ability2.usedCoolDown);
        LOGGER.info("\n");
    }

    String attackType(Pokemon pokemon) { //intoarce un String cu ce mutate e folosita acea runda
        String attackType = null;
        if (pokemon.isStunnedShow) {
            pokemon.isStunnedShow = false;
            return "nu face nimic";
        }
        if (pokemon.move == 0) {
            if (pokemon.isSpecial) attackType = "atac special";
            else
                attackType = "atac normal";
        } else if (pokemon.move == 1) {
            if (pokemon.ability1.dodge) attackType = "abilitate 1 (+dodge)";
            else attackType = "abilitate 1";
        } else if (pokemon.move == 2) {
            if (pokemon.ability2.dodge) attackType = "abilitate 2 (+dodge)";
            else attackType = "abilitate 1";
        }
        return attackType;
    }

    public static void main(String[] args) throws IOException {
        FileHandler fh;
        try {
            fh = new FileHandler("output.log");
            LOGGER.addHandler(fh);
            System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s");
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        LOGGER.setUseParentHandlers(false);

        Game game = Instance();

        game.parse(); //initializarea
        game.playGame(game.trainer1, game.trainer2); //jocul
        System.exit(0);//se opresc
    }
}
