package pension.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pension.AccountRepository;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static pension.setup.AccountOpeningService.ACCEPTABLE_RISK_PROFILE;
import static pension.setup.AccountOpeningService.UNACCEPTABLE_RISK_PROFILE;

class AccountOpeningServiceTest {
	public static final String FIRST_NAME = "Kaushik";
	public static final String LAST_NAME = "Bhat";
	public static final String TAX_ID = "123";
	public static final LocalDate DOB = LocalDate.now();
	public static final String MIDDLE_NAME = "";
	public static final String ACCOUNT_ID = "random_id";

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
		BackgroundCheckResults acceptableBackgroundCheckResults =
				new BackgroundCheckResults(ACCEPTABLE_RISK_PROFILE, 100);

		when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
				.thenReturn(acceptableBackgroundCheckResults);

		when(referenceIdsManager.obtainId(eq(FIRST_NAME), anyString(), eq(LAST_NAME), eq(TAX_ID), eq(DOB)))
				.thenReturn(ACCOUNT_ID);

		final AccountOpeningStatus accountOpeningStatus = accountOpeningService.openAccount(FIRST_NAME, LAST_NAME, TAX_ID, DOB);
		assertEquals(accountOpeningStatus, AccountOpeningStatus.OPENED);

		verify(accountRepository).save(ACCOUNT_ID, FIRST_NAME, LAST_NAME, TAX_ID, DOB, acceptableBackgroundCheckResults);
		verify(accountOpeningEventPublisher).notify(ACCOUNT_ID);
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

	@Test
	void shouldDeclineAccountIfBackgroundCheckThrowsIOException() throws IOException {
		when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
				.thenThrow(new IOException());
		assertThrows(IOException.class, () -> accountOpeningService.openAccount(FIRST_NAME, LAST_NAME, TAX_ID, DOB));
	}
}