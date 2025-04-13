package com.example.tomatomall.service;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.vo.AccountVO;

public interface AccountService {

    public AccountVO getUser(String username);
    public String createUser(AccountVO user);
    public String updateUser(AccountVO user);
    public String login(String phone, String password);


}
