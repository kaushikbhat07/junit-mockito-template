package pension;

import pension.setup.BackgroundCheckResults;

import java.time.LocalDate;

public interface AccountRepository {
    boolean save(String id, String firstName, String lastName, String taxId, LocalDate dob, BackgroundCheckResults backgroundCheckResults);

    boolean isExpired(pension.Account account);

    boolean save(pension.Account account);
}
