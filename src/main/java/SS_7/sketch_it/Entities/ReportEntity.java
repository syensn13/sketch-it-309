package SS_7.sketch_it.Entities;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Holds information for a report.
 */
@Document(collection = "reports")
public class ReportEntity {

    @Id
    private String reportedUser;


    public ReportEntity() {
    }

    /**
     * Sets reported user.
     * @param username Username of user.
     */
    public void setreportedUser(String username) {
        this.reportedUser = reportedUser;
    }
}

