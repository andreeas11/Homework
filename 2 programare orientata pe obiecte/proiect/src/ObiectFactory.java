public class ObiectFactory {
    Obiect createObiect(ObiectType type) {
        switch (type) {
            case Scut:
                return new Obiect(0, 0, 0, 2, 2);
            case Vesta:
                return new Obiect(10, 0, 0, 0, 0);
            case Sabiuta:
                return new Obiect(0, 3, 0, 0, 0);
            case BaghetaMagica:
                return new Obiect(0, 0, 3, 0, 0);
            case Vitamine:
                return new Obiect(2, 2, 2, 0, 0);
            case BradDeCraciun:
                return new Obiect(0, 3, 0, 1, 0);
            case Pelerina:
                return new Obiect(0, 0, 0, 0, 3);
            case obiectNeutreli:
                return new Obiect(0, 0, 0, 0, 0);
        }
        return null;
    }
}
