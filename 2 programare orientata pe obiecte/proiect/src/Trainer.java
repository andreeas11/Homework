import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Trainer {
    String name;
    int age;
    List<Pokemon> pokemons = new ArrayList<>(3);

    public Pokemon bestPokemon() { //returneaza maximul folosind functia de compareTo din clasa Pokemon
        return Collections.max(pokemons);
    }
}
