package pension.setup;

import pension.AccountRepository;

import java.io.IOException;
import java.time.LocalDate;

public class AccountOpeningService {

	public static final String UNACCEPTABLE_RISK_PROFILE = "HIGH";
	public static final String ACCEPTABLE_RISK_PROFILE = "LOW";
	private final BackgroundCheckService backgroundCheckService;
	private final ReferenceIdsManager referenceIdsManager;
	private final AccountRepository accountRepository;
	private final AccountOpeningEventPublisher eventPublisher;


	public AccountOpeningService(BackgroundCheckService backgroundCheckService,
								 ReferenceIdsManager referenceIdsManager,
								 AccountRepository accountRepository,
								 AccountOpeningEventPublisher eventPublisher) {
		this.backgroundCheckService = backgroundCheckService;
		this.referenceIdsManager = referenceIdsManager;
		this.accountRepository = accountRepository;
		this.eventPublisher = eventPublisher;
	}


	public AccountOpeningStatus openAccount(String firstName, String lastName, String taxId, LocalDate dob)
			throws IOException {

		final BackgroundCheckResults backgroundCheckResults = backgroundCheckService.confirm(firstName,
				lastName,
				taxId,
				dob);

		if (backgroundCheckResults == null ||
				backgroundCheckResults.getRiskProfile().equals(UNACCEPTABLE_RISK_PROFILE)) {
			return AccountOpeningStatus.DECLINED;
		} else {
			final String id = referenceIdsManager.obtainId(firstName, "", lastName, taxId, dob);
			if (id != null) {
				accountRepository.save(id, firstName, lastName, taxId, dob, backgroundCheckResults);
				eventPublisher.notify(id);
				return AccountOpeningStatus.OPENED;
			} else {
				return AccountOpeningStatus.DECLINED;
			}
		}
	}
}