package ie.revalue.authenticatedbackend.controllers;

import com.stripe.Stripe;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostMapping("/create-express-account")
    public ResponseEntity<?> createExpressAccount() {
        try {



            Stripe.apiKey = stripeApiKey;

            AccountCreateParams params = AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.EXPRESS)
                    .build();

            Account account = Account.create(params);

            AccountLinkCreateParams linkParams = AccountLinkCreateParams.builder()
                    .setAccount(account.getId())
                    .setRefreshUrl("https://your-website.com/reauth")
                    .setReturnUrl("https://your-website.com/return")
                    .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                    .build();

            AccountLink accountLink = AccountLink.create(linkParams);

            return ResponseEntity.ok(Map.of("url", accountLink.getUrl(), "accountId", account.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating Stripe account");
        }
    }
}
