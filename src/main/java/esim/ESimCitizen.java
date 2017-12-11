package esim;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;

public class ESimCitizen {
    public static final HashMap<String, Double> RANKS = new HashMap<String, Double>() {
        {
            put("Rookie", 1.0);
            put("Private", 1.1);
            put("Private First Class", 1.2);
            put("Corporal", 1.3);
            put("Sergeant", 1.4);
            put("Staff Sergeant", 1.5);
            put("Sergeant First Class", 1.6);
            put("Master Sergeant", 1.65);
            put("First Sergeant", 1.7);
            put("Sergeant Major", 1.75);
            put("Command Sergeant Major", 1.8);
            put("Sergeant Major of the Army", 1.85);
            put("Second Lieutenant", 1.9);
            put("First Lieutenant", 1.93);
            put("Captain", 1.96);
            put("Major", 2.0);
            put("Lieutenant Colonel", 2.03);
            put("Colonel", 2.06);
            put("Brigadier General", 2.1);
            put("Major General", 2.13);
            put("Lieutenant General", 2.16);
            put("General", 2.19);
            put("General of the Army", 2.21);
            put("Marshall", 2.24);
            put("Field Marshall", 2.27);
            put("Supreme Marshall", 2.3);
            put("Generalissimus", 2.33);
            put("Supreme Generalissimuss", 2.36);
            put("Imperial Generalissimus", 2.4);
            put("Legendary Generalissimuss", 2.42);
            put("Imperator", 2.44);
            put("Imperator Caesar", 2.46);
            put("Deus Dimidiam", 2.48);
            put("Deus", 2.5);
            put("Summi Deus", 2.52);
            put("Deus Imperialis", 2.54);
            put("Deus Fabuloso", 2.56);
            put("Deus Ultimum", 2.58);
        }
    };
    public static final DecimalFormat formatter = new DecimalFormat("#,###");
    double eqCriticalHit;
    int strength;
    double eqIncreaseEcoSkill;
    String login;
    int citizenshipId;
    int eqIncreaseStrength;
    double eqFindAWeapon;
    long totalDamage;
    double eqFreeFlight;
    int premiumDays;
    double eqIncreaseHit;
    String rank;
    int id;
    double economySkill;
    int level;
    String citizenship;
    int militaryUnitId;
    double eqAvoidDamage;
    int currentLocationRegionId;
    double eqIncreaseDamage;
    double eqReduceMiss;
    double eqLessWeapons;
    int companyId;
    double eqIncreaseMaxDamage;
    boolean organization;
    int xp;
    int damageToday;
    String status;

    ESimCitizen(
            double eqCriticalHit,
            int strength,
            double eqIncreaseEcoSkill,
            String login,
            int citizenshipId,
            int eqIncreaseStrength,
            double eqFindAWeapon,
            long totalDamage,
            double eqFreeFlight,
            int premiumDays,
            double eqIncreaseHit,
            String rank,
            int id,
            double economySkill,
            int level,
            String citizenship,
            int militaryUnitId,
            double eqAvoidDamage,
            int currentLocationRegionId,
            double eqIncreaseDamage,
            double eqReduceMiss,
            double eqLessWeapons,
            int companyId,
            double eqIncreaseMaxDamage,
            boolean organization,
            int xp,
            int damageToday,
            String status
    ) {
        this.eqCriticalHit = eqCriticalHit;
        this.strength = strength;
        this.eqIncreaseEcoSkill = eqIncreaseEcoSkill;
        this.login = login;
        this.citizenshipId = citizenshipId;
        this.eqIncreaseStrength = eqIncreaseStrength;
        this.eqFindAWeapon = eqFindAWeapon;
        this.totalDamage = totalDamage;
        this.eqFreeFlight = eqFreeFlight;
        this.premiumDays = premiumDays;
        this.eqIncreaseHit = eqIncreaseHit;
        this.rank = rank;
        this.id = id;
        this.economySkill = economySkill;
        this.level = level;
        this.citizenship = citizenship;
        this.militaryUnitId = militaryUnitId;
        this.eqAvoidDamage = eqAvoidDamage;
        this.currentLocationRegionId = currentLocationRegionId;
        this.eqIncreaseDamage = eqIncreaseDamage;
        this.eqReduceMiss = eqReduceMiss;
        this.eqLessWeapons = eqLessWeapons;
        this.companyId = companyId;
        this.eqIncreaseMaxDamage = eqIncreaseMaxDamage;
        this.organization = organization;
        this.xp = xp;
        this.damageToday = damageToday;
        this.status = status;
    }

    public String printLicz(double amount, int weaponQuality) {
        double avoid = this.eqAvoidDamage * 0.01;
        double crit = this.eqCriticalHit * 0.01;
        double miss = this.eqReduceMiss * 0.01;
        int strength = this.strength + this.eqIncreaseStrength;
        double hit = strength * RANKS.get(this.rank);
        double dmgBonus = this.eqIncreaseDamage * 0.01;
        double maxDmgBonus = this.eqIncreaseMaxDamage * 0.01;
        double minDmg = hit * 0.8 * (dmgBonus + 1);
        double maxDmg = hit * 1.2 * (dmgBonus + maxDmgBonus + 1);
        hit = (minDmg + maxDmg) / 2 + this.eqIncreaseHit;
        double hits = amount * 5 / (1 - avoid);
        double hitsCrit = hits * crit;
        double hitsMiss = hits * miss;
        double weaponInf = weaponQuality == 0 ? 0.5 : (1 + 0.2 * weaponQuality);


        long damage = Math.round(Math.round(hits + hitsCrit - hitsMiss) * hit * weaponInf * 1.2 * 1.2);
        hits = Math.round(hits);
        String msg = this.login + ": " + ESimCitizen.formatter.format(damage) + " dmg with " + (long)hits + " hits";
        return msg;
    }

    public String printLink(String server){
        String msg = this.login + ": https://" + server + ".e-sim.org/profile.html?id=" + this.id;
        return msg;
    }

    public String printDmg() {

        String msg = this.login + ": obrażenia zadane dziś: " + ESimCitizen.formatter.format(this.damageToday) +
                ", obrażenia całkowite: " + ESimCitizen.formatter.format(this.totalDamage - this.damageToday);
        return msg;
    }

    public String printToday() {
        String msg = this.login + ": dzisiejsze obrażenia: " + ESimCitizen.formatter.format(this.damageToday);
        return msg;
    }

    public String printEq(){
        String msg = String.format(Locale.ROOT,
                "%s: crit: %.2f%%, miss: %.2f%%, avoid: %.2f%%, max dmg: %.2f%%, dmg: %.2f%%,",
                this.login, this.eqCriticalHit, this.eqReduceMiss, this.eqAvoidDamage, this.eqIncreaseMaxDamage, this.eqIncreaseDamage);

        return msg;
    }
}
