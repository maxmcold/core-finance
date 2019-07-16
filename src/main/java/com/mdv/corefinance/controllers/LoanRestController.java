package com.mdv.corefinance.controllers;


import com.mdv.corefinance.beans.*;
import com.mdv.corefinance.engine.RuleFactory;
import com.mdv.corefinance.engine.Rule;
import com.mdv.corefinance.exceptions.GenericRestException;
import com.mdv.corefinance.repos.*;

import com.mongodb.lang.Nullable;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/loan_api")
public class LoanRestController {
    private static final Logger logger = LoggerFactory.getLogger(LoanRestController.class);


    @Autowired
    RuleFactory evaluator;

    @Autowired
    private ProductRepository productRepository;





    @Value("${fintech.default.currency}")
    private String defaultCurrency;

    @Value("${fintech.system.credit.account}")
    private String systemCreditAccount;

    @Value("${fintech.system.recovery.account}")
    private String systemRecoveryAccount;

    @RequestMapping("/credit")
    public Transaction credit(@RequestParam("amt") String amount, @RequestParam("pid") String pid, @RequestParam("sid") String sid,
                              @Nullable @RequestParam("cur") String currency){

        //TODO: check for mandatory input and make this more resilient to error
        currency = (null ==  currency || currency.isEmpty()) ? defaultCurrency : currency;

        Product p = productRepository.findProductById(new ObjectId(pid));
        Rule rule = evaluator.getRule(p.id.toString());

        Transaction transaction = rule.credit(amount,sid,currency);
       return transaction;

    }

    @RequestMapping("/allow")
    public Transaction allow(@RequestParam("sid") String sid,
                              @RequestParam("amt") String amount, @RequestParam("pid") String ptype,
                              @RequestParam("currency") String currency){


        Product p = productRepository.findProductByType(ptype);
        Rule rule = evaluator.getRule(p.id.toString());
        Transaction t = rule.allow(sid,amount,currency);

        return  t;

    }

    @RequestMapping("/recover")
    public Transaction recover(@RequestParam("sid") String sid,
                               @RequestParam("amt") String amount, @RequestParam("pid") String ptype,
                               @Nullable @RequestParam("cur") String currency){

        Product p = productRepository.findProductByType(ptype);
        Rule rule = evaluator.getRule(p.id.toString());

        return rule.recover(sid, amount, currency);

    }

    @RequestMapping("/test")
    public ResponseMessage invokeMe(@Nullable @RequestParam(value = "error", defaultValue = "0") Boolean error){

        ResponseMessage rm = new ResponseMessage();
        try {
            if (error) throw new GenericRestException("invoked method with error");

            ArrayList<Account> accountList = new ArrayList<>();
            Account account = new Account();
            account.productId = "ciccio";
            account.principal = 1000.1;
            accountList.add(account);

            account = new Account();

            account.productId = "bacicchio";
            account.principal = 2000.2;
            accountList.add(account);
            rm.id = new ObjectId();
            rm.message = "successfully invoked test method";
            rm.out = accountList;

        }catch(GenericRestException e){
            rm.errorCode = "101";
            rm.errorDescription = e.getMessage();

        }
        return rm;

    }




}