package com.example.tomatomall.service;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.vo.AccountVO;

public interface AccountService {

    public AccountVO getUser(String username);
    public Boolean createUser(AccountVO user);
    public Boolean updateUser(AccountVO user);
    public String login(String phone, String password);


}
