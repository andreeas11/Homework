public class Ability {
    int damage;
    boolean stun;
    boolean dodge;
    int coolDown;
    int usedCoolDown = 0;

    public Ability(int damage, boolean stun, boolean dodge, int coolDown) {
        this.damage = damage;
        this.stun = stun;
        this.dodge = dodge;
        this.coolDown = coolDown;
    }
}
