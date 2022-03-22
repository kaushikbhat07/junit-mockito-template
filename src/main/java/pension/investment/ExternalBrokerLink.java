package pension.investment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import static pension.investment.ExternalConnectionDetails.INTERNAL_PROXY_URL;

public class ExternalBrokerLink {
    protected boolean executeInvestmentTransaction(String fundId,
                                                   BigDecimal investmentAmount,
                                                   String direction) throws IOException {
        URL url = new URL(INTERNAL_PROXY_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("DIRECTION", direction);
        con.setRequestProperty("FUND", fundId);
        con.setRequestProperty("AMOUNT", investmentAmount.toPlainString());

        StringBuffer sb = new StringBuffer();
        String result;

        InputStream is = new BufferedInputStream(con.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
        }
        result = sb.toString();

        return Boolean.valueOf(result);
    }
}
