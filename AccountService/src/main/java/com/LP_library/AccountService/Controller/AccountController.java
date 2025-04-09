    package com.LP_library.AccountService.Controller;

    import com.LP_library.AccountService.Entity.Account;
    import com.LP_library.AccountService.Kafka.KafkaProducer;
    import com.LP_library.AccountService.Services.AccountService;
    import org.springframework.web.bind.annotation.*;
    import java.util.Optional;

    @RestController
    @RequestMapping("/api/account")
    public class AccountController {
        private final AccountService accountService;
        private final KafkaProducer kafkaProducer;  // Inject KafkaProducer

        // Constructor-based injection of accountService and kafkaProducer
        public AccountController(AccountService accountService, KafkaProducer kafkaProducer) {
            this.accountService = accountService;
            this.kafkaProducer = kafkaProducer;
        }

        @GetMapping
        public Iterable<Account> getAllAccounts() {
            Iterable<Account> accounts = accountService.getAllAccounts();

            // Send a message to Kafka after fetching all accounts
            String message = "Fetched all accounts.";
            kafkaProducer.sendMessage(message);  // Send the message to Kafka

            return accounts;
        }

        @GetMapping("/{id}")
        public Optional<Account> getAccountById(@PathVariable Long id) {
            return accountService.getAccountById(id);
        }

        @PostMapping
        public Account createAccount(@RequestBody Account account) {
            return accountService.createAccount(account);
        }

        @PutMapping("/{id}")
        public Account updateAccount(@PathVariable Long id, @RequestBody Account accountDetails) {
            return accountService.updateAccount(id, accountDetails);
        }

        @DeleteMapping("/{id}")
        public void deleteAccount(@PathVariable Long id) {
            accountService.deleteAccount(id);
        }
    }
