package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.Repository.AccountRepository;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.utils.AuthUtil;
import com.example.tomatomall.utils.SecurityUtil;
import com.example.tomatomall.vo.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private SecurityUtil securityUtil;

    public AccountVO getUser(String username) {
        Account account = accountRepository.findByUserName(username);
        if (account == null) {
            throw TomatoMallException.nameNotFound();
        }
        return account.toVO();
    }


    public Boolean createUser(AccountVO accountVO){
        Account account = accountRepository.findByName(accountVO.getName());
        if (account != null) {
            throw TomatoMallException.UsernameAlreadyExists();
        }

        String encodedPassword = passwordEncoder.encode(accountVO.getPassword());
        accountVO.setPassword(encodedPassword);

        Account newUser = accountVO.toPO();

        accountRepository.save(newUser);
        return true;
    }


    public Boolean updateUser(AccountVO user){
        Account account = securityUtil.getCurrentUser();

        if(user.getUsername() != null){
            account.setName(user.getUsername());
        }

        if(user.getPassword() != null){
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            account.setPassword(encodedPassword);
        }

        if(user.getName() != null){
            account.setName(user.getName());
        }

        if(user.getAvatar() != null){
            account.setAvatar(user.getAvatar());
        }

        if(user.getRole() != null){
            account.setRole(user.getRole());
        }

        if(user.getTelephone() != null){
            account.setTelephone(user.getTelephone());
        }

        if(user.getEmail() != null){
            account.setEmail(user.getEmail());
        }

        if(user.getLocation() != null){
            account.setLocation(user.getLocation());
        }

        accountRepository.save(account);
        return true;
    }


    public String login(String username, String password){

        if(!checkLogin(username,password)){
            throw TomatoMallException.loginFailed();
        }

        return authUtil.getToken(accountRepository.findByUserName(username));
    }

    private Boolean checkLogin(String username, String password){
        Account account = accountRepository.findByUserName(username);
        if (account == null) {
            return false;
        }
        return passwordEncoder.matches(password, account.getPassword());
    }


//    public Boolean ExistsByUsername(String name){
//        return accountRepository.existsByUserName(name);
//    }

}
