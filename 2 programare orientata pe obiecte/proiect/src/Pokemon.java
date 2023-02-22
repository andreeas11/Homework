//Factory - creare pokemoni si obiecte
//Singleton - Game
//Facade - dupa initializari, din main trebuie sa apelez doar metoda de playGame, aceasta va apela toate metodele de care are nevoie
//DRY

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Pokemon extends Thread implements Comparable<Pokemon> {
    public Trainer trainer;
    PokemonType type;
    int hp; //hpul pokemonului
    int liveHp; //hpul actualizat in timpul luptei
    boolean isSpecial;  // attack is special
    int attack; //avaloarea damageului pe care il face
    int defense;
    int specialDefense;
    Obiect[] objects = new Obiect[3]; //cele 3 obiecte
    Ability ability1;
    Ability ability2;
    boolean isStunned; //flagul care arata daca este stuned sau nu
    boolean isStunnedShow;
    Obiect obiectCombined = new Obiect(0, 0, 0, 0, 0); //obiectul care e suma atributelor celor 3 obiecte ale pokemonului
    int move;
    Pokemon adversary;

    public Pokemon(PokemonType type, int hp, boolean isSpecial, int attack, int defense, int specialDefense, Ability ability1, Ability ability2) {
        this.type = type;
        this.hp = hp;
        this.liveHp = hp;
        this.isSpecial = isSpecial;
        this.attack = attack;
        this.defense = defense;
        this.specialDefense = specialDefense;
        this.ability1 = ability1;
        this.ability2 = ability2;

        this.start(); //se da startul threadului aferent pokemonului
    }

    int sum() { //aduna gp, attack, defense si special defense pentru a afla cel mai bun pokemon
        return hp + attack + defense + specialDefense;
    }

    synchronized void setAdversary(Pokemon adversary) { //notifica cealalt thread si asteapta notificarea lui
        this.adversary = adversary;
        notify();
        while (this.adversary != null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void run() { //notifica cealalt thread si asteapta notificarea lui
        while (true) {
            if (adversary != null) {
                useMove(adversary); //apeleaza useMove pentru a efectua mutarea
                this.adversary = null;
                notify();
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int compareTo(Pokemon that) { //functia de comparare pe care o folosim pentru a afla cel mai bun pokemon
        return Integer.compare(this.sum(), that.sum()) * 100000 - this.type.compareTo(that.type);
        //compara cele 2 sume, iar apoi se inmulteste cu un numar mare rezultatul (a.i. acesta sa conteze doar daca sunt egale), iar apoi scade comparatia lexicografica
    }

    void calcNextMove() { //calculeaza mutarea pe care o va face pokemonul
        move = type == PokemonType.Neutrel1 || type == PokemonType.Neutrel2 ? 0 :
                switch (Integer.signum(ability1.usedCoolDown) + Integer.signum(ability2.usedCoolDown)) { //mutarea e calculata in functie de cate abilitati pot fi folosite in acea runda
                    case 0 -> Game.random(3); //daca toate sunt cooled down (+attack)
                    case 1 -> Game.random(2); //daca doar una e disponibila (+attack)
                    case 2 -> 0; //daca doar atacul e disponibil
                    default -> throw new IllegalStateException("Unexpected value: " + Integer.signum(ability1.usedCoolDown) +
                            Integer.signum(ability2.usedCoolDown));
                };
        if (move == 1 && ability1.usedCoolDown != 0)
            move = 2; //daca abilitatea 1 nu e disponibila, iar cea de a doua da (si suntem numarul random este 1), vom folosi cazul in care cea de a 2 abilitate e folosita
    }

    void useMove(Pokemon that) {
        if ((that.move == 1 && that.ability1.dodge) || (that.move == 2 && that.ability2.dodge))
            return; //daca doar adversarul a folosit o abilitate cu dodge da return
        if (this.isStunned) {
            this.isStunnedShow = true;
            this.isStunned = false;
            return; // daca este stuned de la abilitatea inamicului nu va face nimic aceasta runda
        }
        switch (this.move) { //mutarea din aceasta tura (0-atac, 1-abilitatea1, 2-abilitatea2)
            case 0 -> {
                int newHp;
                if (!this.isSpecial) // atac normal
                    newHp = that.liveHp - this.attack - this.obiectCombined.attack + that.defense + that.obiectCombined.defense;
                else // atac special
                    newHp = that.liveHp - this.attack - this.obiectCombined.specialAttack + that.specialDefense + that.obiectCombined.specialDefense;
                if (newHp < that.liveHp) that.liveHp = newHp;
            }
            case 1 -> {
                if (this.ability1 != null) this.useAbility(that, this.ability1);
            }
            case 2 -> {
                if (this.ability2 != null) this.useAbility(that, this.ability2);
            }
        }
    }

    void useAbility(Pokemon pokemon2, Ability ability) { //foloseste apilitatea pe pokemonul 2
        pokemon2.liveHp -= ability.damage; //ii scade hp (fara sa tina cont de defensuri)
        if (ability.stun) pokemon2.isStunned = true; //daca are stunt se va folosi tura viitoare
        ability.usedCoolDown = ability.coolDown; //ii activeaza usedCoolDown
    }

    void lowerUsedCoolDown() { //scade din usedCoolDown la fiecare runda (daca abilitatea a fost folosita, pana poate fi folosita din nou)
        if (ability1 == null) return;
        if (ability1.usedCoolDown != 0) --ability1.usedCoolDown;
        if (ability2.usedCoolDown != 0) --ability2.usedCoolDown;
    }

    void resetStats() {
        liveHp = hp;
        ability1.usedCoolDown = 0;
        ability2.usedCoolDown = 0;
    }

    public void initObiectCombined() { //initializeaza obiectCombined
        for (int i = 0; i < 3; i++) {
            obiectCombined.hp += objects[i].hp;
            obiectCombined.attack += objects[i].attack;
            obiectCombined.specialAttack += objects[i].specialAttack;
            obiectCombined.defense += objects[i].defense;
            obiectCombined.specialDefense += objects[i].specialDefense;
        }
    }
}
