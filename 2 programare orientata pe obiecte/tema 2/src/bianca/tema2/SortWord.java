package bianca.tema2;

import java.util.Comparator;

public class SortWord implements Comparator<Word> {
    @Override
    public int compare(Word a, Word b) {
        return a.word.compareTo(b.word);
    }
}
