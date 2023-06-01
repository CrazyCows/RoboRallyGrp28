package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.card.*;

import java.util.List;

public class CardDataTemplate {

    private List<ProgrammingCard> programmingCards;
    private List<SpecialProgrammingCard> specialProgrammingCards;
    private List<DamageCard> damageCards;
    private List<UpgradeCard> upgradeCards;
    private List<TempUpgradeCard> tempUpgradeCards;


    public List<ProgrammingCard> getProgrammingCards() {
        return programmingCards;
    }
    public List<SpecialProgrammingCard> getSpecialProgrammingCards() {
        return specialProgrammingCards;
    }

    public List<DamageCard> getDamageCards() {
        return damageCards;
    }

    public List<UpgradeCard> getUpgradeCards() {
        return upgradeCards;
    }

    public List<TempUpgradeCard> getTempUpgradeCards() {
        return tempUpgradeCards;
    }



}
