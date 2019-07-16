package com.mdv.corefinance.controllers;


import com.mdv.corefinance.beans.Account;
import com.mdv.corefinance.beans.Subscriber;
import com.mdv.corefinance.beans.Transaction;
import com.mdv.corefinance.exceptions.AccountNotFoundException;
import com.mdv.corefinance.exceptions.GenericRestException;
import com.mdv.corefinance.repos.AccountRepository;
import com.mdv.corefinance.repos.SubscriberRepository;
import com.mdv.corefinance.repos.TransactionRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ldg_api")
public class TransactionRestController {

    private static final Logger logger = LoggerFactory.getLogger(LoanRestController.class);


    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @RequestMapping(
            value = "/ledger",
            method = RequestMethod.GET
    )
    public List<Transaction> ledger(@RequestParam("sid") String sid){
        return getLedgerByURISubs(sid);

    }

    @RequestMapping(
            value = "/ledger",
            method = RequestMethod.POST
    )
    public List<Transaction> ledger(List<Transaction> transactionList){
        transactionRepository.saveAll(transactionList);
        return null;

    }
    /**
     * Gets all stored transactions for a subscriber
     * @param sid subscriber Id
     * @return
     */
    @RequestMapping(value = {
            "/ledger/{sid}",
            "/ledger/*"

    },
        method = RequestMethod.GET)
    public List<Transaction> getLedgerByURISubs(@PathVariable("sid") String sid){

        ArrayList<Transaction> ledger = new ArrayList<>();

        ObjectId oid = new ObjectId(sid);
        if (null == oid) return ledger;
        Subscriber subscriber = subscriberRepository.findSubscriberById(oid);
        subscriber.accounts = accountRepository.findAccountBySubscriberId(oid);

        for (Account account : subscriber.accounts) {
            List<Transaction> lt = transactionRepository.findTransactionsByFromOrTo(account.id.toString(), account.id.toString());
            ledger.addAll(lt);
        }


        return ledger;

    }








}
