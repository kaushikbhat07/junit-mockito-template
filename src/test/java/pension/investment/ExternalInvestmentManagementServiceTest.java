package pension.investment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import pension.Account;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class ExternalInvestmentManagementServiceTest {
	public static final String FUND_ID = "FUND_ID";

	@Spy
	private ExternalInvestmentManagementService externalInvestmentManagementService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldBeAbleToBuyPensionFundInvestmentIfEnoughCashInAccount() throws IOException {
		// WARNING!
		// the below commented out stub doesnt work for 'spy' for some unknown reason.
//		when(externalInvestmentManagementService.executeInvestmentTransaction(anyString(), any(BigDecimal.class),
//				anyString())).thenReturn(true);

		doReturn(true).when(externalInvestmentManagementService).executeInvestmentTransaction(anyString(),
				any(BigDecimal.class), anyString());

		Account account = new Account();
		account.setInvestments(new HashSet<>());
		final BigDecimal startingAccountBalance = new BigDecimal(10000);
		account.setAvailableCash(startingAccountBalance);

		final BigDecimal desiredInvestmentAmount = new BigDecimal(10000);
		externalInvestmentManagementService.buyInvestmentFund(account, FUND_ID, desiredInvestmentAmount);

		assertEquals(account.getAvailableCash(), startingAccountBalance.subtract(desiredInvestmentAmount));
		assertTrue(account.getInvestments().contains(FUND_ID));
	}
}