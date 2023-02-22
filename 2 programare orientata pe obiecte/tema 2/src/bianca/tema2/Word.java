package bianca.tema2;

import java.util.List;

public class Word {
    public String word;
    public String word_en;
    public String type;
    public String[] singular;
    public String[] plural;
    public List<Definition> definitions;

    Word(String word, String word_en, String type, String[] singular, String[] plural, List<Definition> definitions){
        this.word = word;
        this.word_en = word_en;
        this.type = type;
        this.singular = singular;
        this.plural = plural;
        this.definitions = definitions;
    }
}
