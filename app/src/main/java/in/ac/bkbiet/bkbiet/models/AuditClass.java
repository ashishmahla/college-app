package in.ac.bkbiet.bkbiet.models;

/**
 * Created by Ashish on 12/9/2017.
 */

public class AuditClass {
    AuditRecord[] allAudits;

    public AuditRecord[] getAllAudits() {
        return allAudits;
    }

    public void setAllAudits(AuditRecord[] allAudits) {
        this.allAudits = allAudits;
    }
}
