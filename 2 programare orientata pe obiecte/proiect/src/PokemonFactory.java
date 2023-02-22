public class PokemonFactory {

    Pokemon createPokemon(PokemonType type) {
        switch (type) {
            case Neutrel1:
                return new Pokemon(type, 10, false, 3, 1, 1, null, null);
            case Neutrel2:
                return new Pokemon(type, 20, false, 4, 1, 1, null, null);
            case Pikachu:
                return new Pokemon(type, 35, true, 4, 2, 3, new Ability(6, false, false, 4), new Ability(4, true, true, 5));
            case Bulbasaur:
                return new Pokemon(type, 42, true, 5, 3, 1, new Ability(6, false, false, 4), new Ability(5, false, false, 3));
            case Charmander:
                return new Pokemon(type, 50, false, 4, 3, 2, new Ability(4, true, false, 4), new Ability(7, false, false, 6));
            case Squirtle:
                return new Pokemon(type, 60, true, 3, 5, 5, new Ability(4, false, false, 3), new Ability(2, true, false, 2));
            case Snorlax:
                return new Pokemon(type, 62, false, 3, 6, 4, new Ability(4, true, false, 5), new Ability(0, false, true, 5));
            case Vulpix:
                return new Pokemon(type, 36, false, 5, 2, 4, new Ability(8, true, false, 6), new Ability(2, false, true, 7));
            case Eevee:
                return new Pokemon(type, 39, true, 4, 3, 3, new Ability(5, false, false, 3), new Ability(3, true, false, 3));
            case Jigglypuff:
                return new Pokemon(type, 34, false, 4, 2, 3, new Ability(4, true, false, 4), new Ability(3, true, false, 4));
            case Meowth:
                return new Pokemon(type, 41, false, 3, 4, 2, new Ability(5, false, true, 4), new Ability(1, false, true, 3));
            case Psyduck:
                return new Pokemon(type, 43, false, 3, 3, 3, new Ability(2, false, false, 4), new Ability(2, true, false, 5));
        }
        return null;
    }
}
