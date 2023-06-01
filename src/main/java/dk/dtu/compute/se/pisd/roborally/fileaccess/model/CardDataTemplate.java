package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.DamageCard;
import dk.dtu.compute.se.pisd.roborally.model.ProgrammingCard;
import dk.dtu.compute.se.pisd.roborally.model.TempUpgradeCard;
import dk.dtu.compute.se.pisd.roborally.model.UpgradeCard;

import java.util.List;

public class CardDataTemplate {

    private List<ProgrammingCard> programmingCards;
    private List<DamageCard> damageCards;
    private List<UpgradeCard> upgradeCards;
    private List<TempUpgradeCard> tempUpgradeCards;

    public List<DamageCard> getDamageCards() {
        return damageCards;
    }

    public List<ProgrammingCard> getProgrammingCards() {
        return programmingCards;
    }


    public List<UpgradeCard> getUpgradeCards() {
        return upgradeCards;
    }

    public List<TempUpgradeCard> getTempUpgradeCards() {
        return tempUpgradeCards;
    }



}
