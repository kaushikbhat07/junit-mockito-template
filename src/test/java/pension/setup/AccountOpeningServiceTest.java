package pension.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pension.AccountRepository;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pension.setup.AccountOpeningService.ACCEPTABLE_RISK_PROFILE;
import static pension.setup.AccountOpeningService.UNACCEPTABLE_RISK_PROFILE;

class AccountOpeningServiceTest {
	public static final String FIRST_NAME = "Kaushik";
	public static final String LAST_NAME = "Bhat";
	public static final String TAX_ID = "123";
	public static final LocalDate DOB = LocalDate.now();
	public static final String MIDDLE_NAME = "";

	private AccountOpeningService accountOpeningService;

	private final BackgroundCheckService backgroundCheckService = mock(BackgroundCheckService.class);
	private final ReferenceIdsManager referenceIdsManager = mock(ReferenceIdsManager.class);
	private final AccountRepository accountRepository = mock(AccountRepository.class);
	private final AccountOpeningEventPublisher accountOpeningEventPublisher = mock(AccountOpeningEventPublisher.class);

	@BeforeEach
	void setup() {
		accountOpeningService = new AccountOpeningService(backgroundCheckService, referenceIdsManager, accountRepository,
				accountOpeningEventPublisher);
	}

	@Test
	void shouldOpenAccount() throws IOException {
		when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
				.thenReturn(new BackgroundCheckResults(ACCEPTABLE_RISK_PROFILE, 100));

		when(referenceIdsManager.obtainId(FIRST_NAME, MIDDLE_NAME,LAST_NAME, TAX_ID, DOB))
				.thenReturn("random_id");

		final AccountOpeningStatus accountOpeningStatus = accountOpeningService.openAccount(FIRST_NAME, LAST_NAME, TAX_ID, DOB);
		assertEquals(accountOpeningStatus, AccountOpeningStatus.OPENED);
	}

	@Test
	void shouldDeclineAccountIfUnacceptableRiskProfileBackgroundCheckResponseReceived() throws IOException {
		when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
				.thenReturn(new BackgroundCheckResults(UNACCEPTABLE_RISK_PROFILE, 0));

		final AccountOpeningStatus accountOpeningStatus = accountOpeningService.openAccount(FIRST_NAME, LAST_NAME, TAX_ID, DOB);
		assertEquals(accountOpeningStatus, AccountOpeningStatus.DECLINED);
	}

	@Test
	void shouldDeclineAccountIfNullBackgroundCheckResponseReceived() throws IOException {
		when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
				.thenReturn(null);

		final AccountOpeningStatus accountOpeningStatus = accountOpeningService.openAccount(FIRST_NAME, LAST_NAME, TAX_ID, DOB);
		assertEquals(accountOpeningStatus, AccountOpeningStatus.DECLINED);
	}

	@Test
	void shouldDeclineAccountIfReferenceIdNull() throws IOException {
		when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
				.thenReturn(new BackgroundCheckResults(ACCEPTABLE_RISK_PROFILE, 100));

		when(referenceIdsManager.obtainId(FIRST_NAME, MIDDLE_NAME, LAST_NAME, TAX_ID, DOB))
				.thenReturn(null);

		final AccountOpeningStatus accountOpeningStatus = accountOpeningService.openAccount(FIRST_NAME, LAST_NAME, TAX_ID, DOB);
		assertEquals(accountOpeningStatus, AccountOpeningStatus.DECLINED);
	}
}