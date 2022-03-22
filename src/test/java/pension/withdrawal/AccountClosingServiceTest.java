package pension.withdrawal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pension.Account;
import pension.setup.BackgroundCheckService;

class AccountClosingServiceTest {
	@Mock
	private BackgroundCheckService backgroundCheckService;

//	private AccountClosingService accountClosingService = new AccountClosingService(backgroundCheckService);

	@BeforeEach
	void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldDeclineAccountClosingIfHolderHasNotReachedRetirementAge() {
		Account account = new Account();

	}
}