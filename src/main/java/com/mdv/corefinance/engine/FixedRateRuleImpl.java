package com.mdv.corefinance.engine;

import com.mdv.corefinance.beans.Account;
import com.mdv.corefinance.beans.Product;
import com.mdv.corefinance.beans.Subscriber;
import com.mdv.corefinance.beans.Transaction;
import com.mdv.corefinance.controllers.LoanRestController;
import com.mdv.corefinance.exceptions.GenericRestException;
import com.mdv.corefinance.repos.AccountRepository;
import com.mdv.corefinance.repos.TransactionRepository;
import com.mdv.corefinance.utils.Constants;
import com.mdv.corefinance.utils.LocalTimer;
import com.mongodb.lang.Nullable;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class FixedRateRuleImpl implements Rule {

    private static final Logger logger = LoggerFactory.getLogger(LoanRestController.class);

    @Autowired
    private AccountRepository accountRepository;


    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${fintech.default.currency}")
    private String defaultCurrency;

    @Value("${fintech.system.credit.account}")
    private String systemCreditAccount;



    @Value("${fintech.product.fixedrate.code}")
    private String oneoffFeeProductType;


    @Value("${fintech.system.recovery.account}")
    private String systemRecoveryAccount;

    public FixedRateRuleImpl(){}

    @Override
    public Double getFee(Double amount){
        //TODO: hardcoded fee, to be fetched by product
        Double d = 0.15;
        Double result = d*amount;
        return (double)Math.round(result * 100d) / 100d;
    }


    public Transaction transfer(String from, String to,
                                  String amount, String type,
                                 String currency, Boolean forceTransfer){
        //TODO: check for mandatory input and make this more resilient to error
        forceTransfer = (null == forceTransfer) ? false : forceTransfer;



        Transaction t = new Transaction();


        t.from = from;
        t.to = to;
        t.amount = amount;
        t.type = type;
        t.currency = (currency.isEmpty()) ? defaultCurrency : currency;
        t.timestamp = LocalTimer.now();


        ObjectId fromId = null;
        ObjectId toId = null;

        try {

            fromId = new ObjectId(from);
            toId = new ObjectId(to);

        } catch (Exception e) {
            String errorMessage = "Error creating ObjectIds";
            logger.error(errorMessage);
            throw new GenericRestException(errorMessage);
        }


        Account source = accountRepository.findAccountById(fromId);
        Account target = accountRepository.findAccountById(toId);

        if (null == source || null == target){
            String errorMessage = "Error fetching source or target account IDs";
            logger.error(errorMessage);
            t.setResult(Constants.TRANSACTION_FAIL);
            t.setErrorMessage(errorMessage);
            return t;

        }

        Double sourceValue;
        Double targetValue;

        switch (type){
            case Constants.PRINCIPAL_TRANSACTION_TYPE :

                sourceValue = (null != source.principal) ? new Double(source.principal) : new Double(0.0);
                targetValue = (null != target.principal) ? new Double(target.principal) : new Double(0.0);
                break;
            case Constants.FEE_TRANSACTION_TYPE :
                sourceValue = (null != source.fee) ? new Double(source.fee) : new Double(0.0);
                targetValue =  (null != target.fee) ? new Double(target.fee) : new Double(0.0);
                break;



            default :
                sourceValue = new Double(source.balance);
                targetValue = new Double(target.balance);

        }

        //check if enough balance
        //targetValue = new Double(target.balance);
        Double requiredValue = new Double(amount);

        //TODO: check currency equalisation

        //TODO: this whole code to be improved across a Rule implementation. It's basically implemented only for one off fee transactions


        if (requiredValue > sourceValue && !forceTransfer) {

            String errorMessage = "Not enough balance in requested account, required: " + requiredValue + " available: " + sourceValue;
            logger.error(errorMessage);
            t.setErrorMessage(errorMessage);
            t.setResult(Constants.TRANSACTION_FAIL);
            return t;

        }

        //If you are transacting an amount higher than your balance BUT you force the transfer THEN you transfer the full balance
        requiredValue = (forceTransfer && requiredValue > sourceValue) ? sourceValue : requiredValue;

        switch (type){
            case Constants.PRINCIPAL_TRANSACTION_TYPE :
                source.principal = Math.round((sourceValue - requiredValue)* 100D) / 100D;
                target.principal = Math.round((targetValue + requiredValue)* 100D) / 100D;
                break;
            case Constants.FEE_TRANSACTION_TYPE :
                source.fee = Math.round((sourceValue - requiredValue)* 100D) / 100D;
                target.fee = Math.round((targetValue + requiredValue)* 100D) / 100D;
                break;
            default :
                source.balance = Math.round((sourceValue - requiredValue)* 100D) / 100D;
                target.balance = Math.round((targetValue + requiredValue)* 100D) / 100D;

        }






        /**
         * TODO: this steps must be rolled back if something goes wrong
         * All the block must be included in a 2 phase commit (MongoDB < 4.0)
         * or using MongoDB transactions (4.0+)
         * https://hackernoon.com/mongodb-transactions-5654cdb8fd24
         */
        {

            accountRepository.save(source);
            accountRepository.save(target);
            transactionRepository.save(t);
        }
        t.setResult(Constants.TRANSACTION_SUCCESS);
        t.timestamp = LocalTimer.now(); // update timestamp before leaving the method.

        return t;
    }

    public Transaction credit(String amount, String sid,
                                    String currency){
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        //First transfer the principal from allowance to credit account

        Account allowance = accountRepository.findAccountByTypeAndSubscriberId(Constants.ALLOWANCE_ACCOUNT_TYPE,new ObjectId(sid));
        Account account = accountRepository.findAccountByTypeAndSubscriberId(Constants.CREDIT_ACCOUNT_TYPE,new ObjectId(sid));

        Transaction t = transfer(allowance.id.toString(),account.id.toString(),amount,Constants.PRINCIPAL_TRANSACTION_TYPE,defaultCurrency,Boolean.FALSE);

        transactions.add(t);
        if (t.getResult() == Constants.TRANSACTION_FAIL) return t;
        //Then create the fee and transfer it from system credit account
        Account sysCredit = accountRepository.findAccountByType(systemCreditAccount);

        //Evaluate Fee

        Double fee = getFee(new Double(amount));
        transfer(sysCredit.id.toString(),account.id.toString(),fee.toString(),Constants.FEE_TRANSACTION_TYPE,defaultCurrency,Boolean.FALSE);

        Transaction ret =  new Transaction();
        ret.setResult("Credited amount ["+amount+"] with a fee of ["+fee+"] to subscriber ["+sid+"]");
        ret.type = Constants.CREDIT_TRANSACTION_TYPE;
        ret.timestamp = LocalTimer.now();
        ret.amount = amount;
        ret.from = allowance.id.toString();
        ret.to = account.id.toString();
        ret.currency = currency;
        //TODO: improve transaction correlation in future with correlation IDs
        ret.id = t.id; //this is the principal transfer transaction ID
        transactions.add(ret);
        return ret;
    }

    public Transaction allow(String sid,
                             String amount,
                             String currency){
        Account to = accountRepository.findAccountByTypeAndSubscriberId(Constants.ALLOWANCE_ACCOUNT_TYPE,new ObjectId(sid));
        Account sys = accountRepository.findAccountByTypeAndProductId(systemCreditAccount,oneoffFeeProductType);



        String syid = sys.id.toString();
        String toid = to.id.toString();
        Transaction t = transfer(syid,toid,amount,Constants.PRINCIPAL_TRANSACTION_TYPE,currency,Boolean.FALSE);
        return t;
    }

    public Transaction recover(String sid,
                               String amount,
                               String currency){


        Account from;
        Account sysRecovery;

        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        try {
            /**
             * recovering action: transfer from credit account to system.recovery account
             *
             */

            //First get credit account
            from = accountRepository.findAccountByTypeAndSubscriberId(Constants.CREDIT_ACCOUNT_TYPE, new ObjectId(sid));

            //assuming sys accounts are created at deployment time
            sysRecovery = accountRepository.findAccountByTypeAndProductId(systemRecoveryAccount, oneoffFeeProductType);
            //sysFee = accountRepository.findAccountByTypeAndProductId(systemFeeAccount, pid);
            //TODO: improve error handling, catch specific exceptions here
        } catch(Exception e){
            throw new GenericRestException("Error fetching account IDs from [sid="+sid+"] [amount="+amount+
                    "] [productType="+oneoffFeeProductType+"]"+"[currency="+currency+"]");
        }



        String syRecoveryId = sysRecovery.id.toString();
        String fromid = from.id.toString();

        Double recoveryAmount = new Double(amount);
        //Remove first the fee value
        String transferValue = null;
        if (recoveryAmount >= from.fee) {
            transferValue = from.fee.toString();
            recoveryAmount -= from.fee;
            //from.fee = 0.0;
        } else {
            transferValue = amount;
            from.fee -= recoveryAmount;
            recoveryAmount = 0.0;
        }


        //Execute recovery fee transfer
        Transaction feeTrans = transfer(fromid,syRecoveryId,transferValue,Constants.FEE_TRANSACTION_TYPE,defaultCurrency,Boolean.TRUE);
        transactions.add(feeTrans);
        if (feeTrans.getResult() == Constants.TRANSACTION_FAIL) return feeTrans;

        //After deducting the fee from recovery amount, you can recover from principal.
        transferValue = recoveryAmount.toString();
        //Execute recovery principal transfer
        Transaction recover = transfer(fromid,syRecoveryId,transferValue,Constants.PRINCIPAL_TRANSACTION_TYPE,defaultCurrency,Boolean.TRUE);
        transactions.add(recover);
        //remaining recovery will be returned to user, otherwise all consumed by outstanding credit
        recoveryAmount = (recoveryAmount > from.principal) ? Math.round((recoveryAmount - from.principal)*100D)/100D : 0.0;



        Transaction t = new Transaction();
        t.currency = defaultCurrency;
        t.amount = recoveryAmount.toString();
        t.timestamp = Date.from(Instant.now()).toString();

        t.type = Constants.REMAINING_RECOVDRY_TRANSACTION_TYPE;
        t.setResult("Remaining recovery");
        transactions.add(t);

        return t;

    }


}
