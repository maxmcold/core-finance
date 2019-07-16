package com.mdv.corefinance.controllers;

import com.mdv.corefinance.beans.*;
import com.mdv.corefinance.exceptions.GenericUIException;
import com.mdv.corefinance.repos.*;
import com.mdv.corefinance.utils.Constants;
import com.mongodb.lang.Nullable;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;



import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
@RequestMapping("/")
public class UIController {

    private static final Logger logger = LoggerFactory.getLogger(UIController.class);

    @Autowired
    private LoanRepository lr;
    @Autowired
    DefinitionRepository dr;
    @Autowired
    private AccountRepository ar;

    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private SubscriberRepository subscriberRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${fintech.remote.api.host}")
    private String remoteApiHost;

    @Value("${fintech.remote.api.port}")
    private String remoteApiPort;

    @Value("${fintech.default.currency}")
    private String defaultCurrency;

    @RequestMapping("/")
    public String index (Model model){

       return account(model);
    }

    @RequestMapping("/home")
    public String home (Model model){

        return account(model);

    }



    private List<Account> getAccountsBySub(String subId){

        return accountRepository.findAccountBySubscriberId(new ObjectId(subId));


    }
    @RequestMapping("/credit")
    public String credit(@RequestParam("amount") String amount,
                              @RequestParam("sid") String sid,
                              @Nullable @RequestParam("ptype") String ptype,Model model){

        Product p = productRepository.findProductByName(Constants.AIRTIME_PRODUCT_NAME);


        //TODO: hardcoded query string for now
        String query = remoteApiHost+":"+
                remoteApiPort+
                "/loan_api"+
                "/credit?"+
                "amt="+amount+
                "&pid="+p.id.toString()+
                "&sid="+ sid +
                "&cur="+defaultCurrency;
        Transaction t = executeRemoteCall(query);



        //t = executeTransfer("transfer",from,to,amount,Constants.CREDIT_TRANSACTION_TYPE,defaultCurrency);
        if (t.getResult().equals(Constants.TRANSACTION_FAIL)){
            model.addAttribute("message","Error crediting amount: " + t.getErrorMessage());

        } else {
            model.addAttribute("message", "Credited [" + amount + "] " + t.currency + " to subscriber [" + sid +"]");
        }


        Subscriber subscriber = subscriberRepository.findSubscriberById(new ObjectId(sid));
        subscriber.accounts = accountRepository.findAccountBySubscriberId(new ObjectId(sid));
        model.addAttribute("subscriber", subscriber);
        model.addAttribute("ledger",getLedgerBySubscriber(sid));
        return "subscriber";


    }



    




    @RequestMapping("/accounts")
    public String account(Model model) {

        logger.info("Remote host configuration ["+remoteApiHost+":"+remoteApiPort+"]");

        List<Account> accs = ar.findAll();
        model.addAttribute("accounts", accs);


        return "accounts";

    }

    @RequestMapping("/account")
    public String accountDetails(@RequestParam("aid") String aid,Model model) {


        Account acc = ar.findAccountById(new ObjectId(aid));
        model.addAttribute("accounts", acc);


        return "account";

    }

    @RequestMapping("/subscriber")
    public String subs(@RequestParam("sub") String sub,Model model) {

        ObjectId oid = new ObjectId(sub);
        Subscriber subscriber = subscriberRepository.findSubscriberById(oid);

        //TODO: manage missing subscriber
        subscriber.accounts = accountRepository.findAccountBySubscriberId(oid);



        model.addAttribute("subscriber",subscriber);
        model.addAttribute("ledger",getLedgerBySubscriber(sub));




        return "subscriber";
    }

    private List<Transaction> getLedgerBySubscriber(String sid){

        RestTemplate rt = new RestTemplate();

        //TODO: hardcoded query string for now
        String query = remoteApiHost+":"+
                remoteApiPort+
                "/ldg_api"+
                "/ledger?"+
                "sid="+sid;
        logger.info("Invoking "+query+"...");
        List<Transaction> lt = null;
        try {
            lt = rt.getForObject(query, List.class);

        }catch (HttpClientErrorException e){
            logger.error(e.getMessage(),e);
            throw new GenericUIException(e.getMessage());
        }catch (RuntimeException e) {
            logger.error(e.getMessage(),e);
            throw new GenericUIException(e.getMessage());
        }
        return lt;


    }
    @RequestMapping("/accountBySub")
    public String accountBySub(@RequestParam("sub") String sub,Model model) {

        ObjectId oid = new ObjectId(sub);
        List<Account> accs = ar.findAccountBySubscriberId(oid);
        model.addAttribute("accounts", accs);
        model.addAttribute("message","List of accounts for subscriber "+sub);

        return "accounts";

    }

    @RequestMapping("/createAccount")
    public String createAccount(@RequestParam("sid") String sid,
                                @RequestParam("type") String type,
                                @RequestParam("pid") String pid,
                                Model model){

        RestTemplate rt = new RestTemplate();
        //TODO: hardcoded query string for now



        String url = remoteApiHost+":"+ remoteApiPort+"/acc_api/account";
        Account account = new Account();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
            map.add("sid", sid);
            map.add("type", type);
            map.add("pid", pid);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            account = rt.postForObject(url,request,Account.class);
            //account = rt.getForObject(query, Account.class);

        }catch (HttpClientErrorException e){
            logger.error(e.getMessage(),e);
            model.addAttribute("message","Error creating account: "+e.getMessage());
        }catch (RuntimeException e){
            logger.error(e.getMessage(),e);
            model.addAttribute("message","Error creating account: "+e.getMessage());

        }
        model.addAttribute("message","created new account ["+account.id+"]");
        return subs(sid,model);
    }


    @RequestMapping("/allow")
    public String allow(@RequestParam("sid") String sid,
                           @RequestParam("amt") String amount, @RequestParam("pid") String pid,
                           @Nullable @RequestParam("currency") String currency, Model model){



        Transaction t = allow(sid,amount,pid,currency);

        //TODO: this should be fetched by Rest API
        ObjectId oid = new ObjectId(sid);
        Subscriber subscriber = subscriberRepository.findSubscriberById(oid);
        subscriber.accounts = accountRepository.findAccountBySubscriberId(oid);
        model.addAttribute("subscriber", subscriber);
        model.addAttribute("message","allowed " + amount + " " + currency + " to " + sid);
        model.addAttribute("ledger",getLedgerBySubscriber(sid));

        return "subscriber";






    }


    private Transaction executeRemoteCall(String query){
        RestTemplate rt = new RestTemplate();


        Transaction t = new Transaction();
        try {
            t = rt.getForObject(query, Transaction.class);

        }catch (HttpClientErrorException e){
            t.setResult(Constants.TRANSACTION_FAIL);
            t.setErrorMessage(e.getMessage());
        }catch (RuntimeException e){
            t.setResult(Constants.TRANSACTION_FAIL);
            t.setErrorMessage(e.getMessage());
        }
        return t;


    }
    private Transaction allow( String sid,
                                String amount, String pid,
                                String currency){
        RestTemplate rt = new RestTemplate();
        //TODO: hardcoded query string for now


        String query = remoteApiHost+":"+
                remoteApiPort+
                "/loan_api"+
                "/allow?"+
                "&sid="+sid+
                "&amt="+amount+
                "&pid="+pid+
                "&currency="+currency;

        Transaction t = rt.getForObject(query,Transaction.class);
        return t;

    }

    @RequestMapping("/transactions")
    public String transactions(@RequestParam("aid") String aid, @Nullable @RequestParam("sid") String sid,Model model){

        List<Transaction> transactionList =
                transactionRepository.findTransactionsByFromOrTo(
                        aid,
                        aid,
                        Sort.by(DESC, "timestamp")
                );

        Subscriber subscriber = null;
        if (null != sid && !sid.equals("")) {
            subscriber = subscriberRepository.findSubscriberById(new ObjectId(sid));

        }

        model.addAttribute("transactions",transactionList);
        model.addAttribute("subscriber", subscriber);

        return "transactions";
    }


    @RequestMapping("/recover")
    public String recover(@RequestParam("sid") String sid,
        @RequestParam("amt") String amount, @RequestParam("pid") String pid,
        @Nullable @RequestParam("cur") String currency, Model model){



        //TODO: hardcoded query string for now


        String query = remoteApiHost+":"+
                remoteApiPort+
                "/loan_api"+
                "/recover?"+
                "sid="+sid+
                "&amt="+amount+
                "&pid="+pid+
                "&currency="+currency;

        Transaction t = executeRemoteCall(query);

        //TODO: this should be fetched by Rest API
        ObjectId oid = new ObjectId(sid);
        Subscriber subscriber = subscriberRepository.findSubscriberById(oid);
        subscriber.accounts = accountRepository.findAccountBySubscriberId(oid);
        model.addAttribute("subscriber", subscriber);
        model.addAttribute("message",t.getResult());
        model.addAttribute("ledger",getLedgerBySubscriber(sid));
        return "subscriber";


        }


    @RequestMapping("/test")
    public String test(@Nullable @RequestParam(value = "error", defaultValue = "0") Boolean error,
            Model model){

        RestTemplate rt = new RestTemplate();

        String query = remoteApiHost+":"+
                remoteApiPort+
                "/loan_api"+
                "/test";

        ResponseMessage rm = new ResponseMessage();
        try {
            rm = rt.getForObject(query, ResponseMessage.class);

        }catch (HttpClientErrorException e){
            rm.errorDescription = e.getMessage();

        }catch (RuntimeException e){
            rm.errorDescription = e.getMessage();
        }
        model.addAttribute("out",rm);
        model.addAttribute("test","provaciccio");

        return "test";


    }

}