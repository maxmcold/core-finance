package com.mdv.corefinance.engine;

import com.mdv.corefinance.beans.Account;
import com.mdv.corefinance.beans.Product;
import com.mdv.corefinance.beans.Subscriber;
import com.mdv.corefinance.beans.Transaction;
import com.mdv.corefinance.exceptions.AccountNotFoundException;
import com.mdv.corefinance.repos.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeeWithLatePaymentRuleImpl implements Rule {

    @Autowired
    AccountRepository accountRepository;



    @Override
    public Double getFee(Double amount){
        return new Double(0);
    }


    @Override
    public Transaction transfer(String from, String to,
                                String amount, String type,
                                String currency, Boolean forceTransfer){
        return null;
    }
    @Override
    public Transaction credit(String amount, String sid,
                                    String currency){
        return null;
    }
    @Override
    public Transaction allow(String sid,
                             String amount,
                             String currency){
        return null;
    }
    @Override
    public Transaction recover(String sid,
                                     String amount,
                                     String currency){
        return null;
    }


}
