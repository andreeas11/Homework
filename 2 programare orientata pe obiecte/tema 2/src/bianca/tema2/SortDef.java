package bianca.tema2;

import java.util.Comparator;

public class SortDef implements Comparator<Definition> {

    @Override
    public int compare(Definition a, Definition b)
    {
        return a.year - b.year;
    }
}
