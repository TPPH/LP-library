package com.LP_library.AccountService.Services;

import com.LP_library.AccountService.Entity.Account;
import com.LP_library.AccountService.Repository.AccountRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository AccountRepository) {
        this.accountRepository = AccountRepository;
    }

    public Iterable<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, Account accountDetails) {
        return accountRepository.findById(id).map(account -> {
            account.setUsername(accountDetails.getUsername());
            account.setEmail(accountDetails.getEmail());
            account.setPassword(accountDetails.getPassword());
            return accountRepository.save(account);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
