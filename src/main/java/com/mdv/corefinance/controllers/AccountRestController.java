package com.mdv.corefinance.controllers;


import com.mdv.corefinance.beans.Account;
import com.mdv.corefinance.exceptions.AccountNotFoundException;
import com.mdv.corefinance.exceptions.GenericRestException;
import com.mdv.corefinance.repos.AccountRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/acc_api")
public class AccountRestController {

    private static final Logger logger = LoggerFactory.getLogger(LoanRestController.class);

    @Autowired
    public AccountRepository accountRepository;

    @RequestMapping("getAccounts")
    public List<Account> getAllAccounts(){
        List<Account> accounts = accountRepository.findAll();
        return accounts;
    }

    @RequestMapping("getAccBySub")
    public List<Account> getAccountBySid(@RequestParam("sid") String sid) throws GenericRestException{

        ObjectId oid = null;

        if (null != sid && !sid.equals("")) {

            try {
                   oid = new ObjectId(sid);
            } catch (Exception e){
                String errorMessage = "No well formed Object ID provided";
                logger.error(errorMessage);
               throw new GenericRestException(errorMessage);
            }
            List<Account> accounts = accountRepository.findAccountBySubscriberId(oid);
            if (null != accounts && !accounts.isEmpty())
                return accounts;
            else
                throw new GenericRestException("No account found with subscirber ID["+oid+"]");

        } else {
            throw new GenericRestException("No subscriber ID provided - received NULL or empty string");
        }
    }

    @RequestMapping("/gaccboid")
    public Account getAccountById(@RequestParam("aid") String id) throws AccountNotFoundException {

        ObjectId oid = null;

        try{
            oid = new ObjectId(id);
        }catch(Exception e){
            throw new GenericRestException("Provided oid ["+oid+"] not valid object id");

        }
        Account account = accountRepository.findAccountById(oid);
        if (null != account)
            return account;
        else
            throw new AccountNotFoundException("No account found with object ID[" + oid + "]");
    }

    @RequestMapping("/gacctypebyid")
    public String getAccountTypeById(@RequestParam("aid") String aid){
        ObjectId oid = null;

        try{
            oid = new ObjectId(aid);
        }catch(Exception e){
            throw new GenericRestException("Provided oid ["+oid+"] not valid object id");

        }
        Account account = accountRepository.findAccountById(oid);
        if (null != account)
            return account.type;
        else
            throw new AccountNotFoundException("No account found with object ID[" + oid + "]");

    }









}
