package com.mdv.corefinance.controllers;


import com.mdv.corefinance.beans.*;
import com.mdv.corefinance.engine.Evaluator;
import com.mdv.corefinance.engine.Rule;
import com.mdv.corefinance.exceptions.GenericRestException;
import com.mdv.corefinance.exceptions.LoanNotFoundException;
import com.mdv.corefinance.repos.*;
import com.mdv.corefinance.utils.Constants;
import com.mdv.corefinance.utils.LocalTimer;
import com.mongodb.lang.Nullable;
import org.bson.types.ObjectId;
import org.hibernate.validator.internal.constraintvalidators.hv.EANValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/loan_api")
public class LoanRestController {
    private static final Logger logger = LoggerFactory.getLogger(LoanRestController.class);


    @Autowired
    Evaluator evaluator;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SubscriberRepository subrepo;


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private TransactionRepository transactionRepository;


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
        Rule rule = evaluator.evaluate(p.id.toString());

        Transaction transaction = rule.credit(amount,sid,currency);
       return transaction;

    }

    @RequestMapping("/allow")
    public Transaction allow(@RequestParam("sid") String sid,
                              @RequestParam("amt") String amount, @RequestParam("pid") String ptype,
                              @RequestParam("currency") String currency){

        //TODO: check for mandatory input and make this more resilient to error
        Product p = productRepository.findProductByType(ptype);
        Rule rule = evaluator.evaluate(p.id.toString());
        Transaction t = rule.allow(sid,amount,currency);

        return  t;

    }

    @RequestMapping("/recover")
    public Transaction recover(@RequestParam("sid") String sid,
                               @RequestParam("amt") String amount, @RequestParam("pid") String ptype,
                               @Nullable @RequestParam("cur") String currency){

        Product p = productRepository.findProductByType(ptype);
        Rule rule = evaluator.evaluate(p.id.toString());

        return rule.recover(sid, amount, currency);

    }

    @RequestMapping("/prd")
    public Double getProduct(@RequestParam("pid") String pid){

        ObjectId oid = new ObjectId(pid);

        Rule rule = evaluator.evaluate(pid);


        return new Double(1);

    }

    @RequestMapping("getledgerbysid")
    public List<Transaction> getLedgerBySubs(@RequestParam("sid") String sid){

        //TODO: manage missing subscriber or wrong input
        ObjectId oid = new ObjectId(sid);
        Subscriber subscriber = subscriberRepository.findSubscriberById(oid);
        subscriber.accounts = accountRepository.findAccountBySubscriberId(oid);

        ArrayList<Transaction> ledger = new ArrayList<>();

        for (Account account : subscriber.accounts){
            List<Transaction> lt = transactionRepository.findTransactionsByFromOrTo(account.id.toString(),account.id.toString());
            ledger.addAll(lt);
        }
        return ledger;

    }

}