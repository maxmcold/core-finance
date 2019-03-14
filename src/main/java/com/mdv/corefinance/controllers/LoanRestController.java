package com.mdv.corefinance.controllers;


import com.mdv.corefinance.beans.*;
import com.mdv.corefinance.exceptions.AccountNotFoundException;
import com.mdv.corefinance.exceptions.LoanNotFoundException;
import com.mdv.corefinance.repos.AccountRepository;
import com.mdv.corefinance.repos.LoanRepository;
import com.mdv.corefinance.repos.SubscriberRepository;
import com.mdv.corefinance.repos.TransactionRepository;
import com.mongodb.lang.Nullable;
import org.bson.types.ObjectId;
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
    private SubscriberRepository subrepo;

    @Autowired
    private LoanRepository loanRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private TransactionRepository transactionRepository;


    @Value("fintech.default.currency")
    private String defaultCurrency;


    @RequestMapping("/subsApi")
    public List<Subscriber> subscribers (){

        return subrepo.findAll();
    }

    @RequestMapping("/loans")
    public List<Loan> loans(){
        List<Loan> ln = loanRepo.findAll();
        return ln;
    }

    @RequestMapping("/glnboid")
    public Loan getLoanByOid(@RequestParam("_id") String id)  throws LoanNotFoundException {
        ObjectId oid = null;

        if (null != id && !id.equals("")) {
            oid = new ObjectId(id);
            Loan loan = loanRepo.findLoanById(oid);
            if (null != loan)
                return loan;
            else
                throw new LoanNotFoundException("No loan found with object ID[" + oid + "]");

        } else {
            throw new LoanNotFoundException("No object ID provided - received NULL or empty string");
        }
    }

    @RequestMapping("/gaccboid")
    public Account getAccountById(@RequestParam("aid") String id) throws AccountNotFoundException {
        ObjectId oid = null;
        if (null != id && !id.equals("")) {
            oid = new ObjectId(id);
            Account account = accountRepo.findAccountById(oid);
            if (null != account)
                return account;
            else
                throw new LoanNotFoundException("No account found with object ID[" + oid + "]");

        } else {
            throw new LoanNotFoundException("No object ID provided - received NULL or empty string");
        }

    }




    @RequestMapping("/glnbpid")
    public List<Loan> getLoanByPid(@RequestParam("pid") String pid) throws LoanNotFoundException {

        if (null != pid && !pid.equals("")) {
            List<Loan> loans = loanRepo.findLoansByProductId(pid);
            if (loans.isEmpty()){
                throw new LoanNotFoundException("Load with product ID ["+pid+"] not found");
            }
            return loans;
        } else {

            throw new LoanNotFoundException("No product ID provided - received NULL or empty string");
        }


    }



    @RequestMapping("/errTest")
    public List<ErrorDetails> getErrDetails(){

        ErrorDetails errorDetails = new ErrorDetails(new Date(), "test message",
                "test details");
        ArrayList<ErrorDetails> al = new ArrayList<>();
        al.add(errorDetails);
        logger.debug(al.toString());
        return al;
    }


    @RequestMapping("/transfer")
    public Transaction transfer(@RequestParam("from") String from, @RequestParam("to") String to,
                                @RequestParam("amt") String amount, @RequestParam("type") String type,
                                @RequestParam("currency") String currency){

        //TODO: check for mandatory input and make this more resilient to error

        Transaction t = new Transaction();
        t.from = from;
        t.to = to;
        t.amount = amount;
        t.type = type;

        t.currency = (currency.isEmpty()) ? defaultCurrency : currency;


        Account source = accountRepo.findAccountById(new ObjectId(from));
        Account target = accountRepo.findAccountById(new ObjectId(to));

        //check if enough balance
        Double sourceValue = new Double(source.balance);
        Double targetValue = new Double(target.balance);
        Double requiredValue = new Double(amount);

        //TODO: check currency equalisation


        if (requiredValue > sourceValue)
            logger.error("Not enough balance in wallet, required: "+ requiredValue+ " available: " +sourceValue); //TODO: move from error to debug
        else {
            source.balance = sourceValue - requiredValue;
            target.balance = targetValue + requiredValue;

        }

        t.timestamp = Date.from(Instant.now()).toString();

        //TODO: this steps must be rolled back if something goes wrong

        {
            accountRepo.save(source);
            accountRepo.save(target);
            transactionRepository.save(t);
        }
        return t;

    }
    @RequestMapping("/credit")
    public Transaction credit(@RequestParam("account") String account,
                              @RequestParam("amt") String amount, @RequestParam("pid") String pid,
                              @RequestParam("currency") String currency){
        //TODO: check for mandatory input and make this more resilient to error

        Transaction t = new Transaction();
        return t;

    }





}