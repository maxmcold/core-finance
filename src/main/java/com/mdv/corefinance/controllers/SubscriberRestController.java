package com.mdv.corefinance.controllers;


import com.mdv.corefinance.beans.Account;
import com.mdv.corefinance.beans.Loan;
import com.mdv.corefinance.beans.Subscriber;
import com.mdv.corefinance.exceptions.GenericRestException;
import com.mdv.corefinance.exceptions.SubscriberAlreadyRegisteredException;
import com.mdv.corefinance.exceptions.SubscriberNotFoundException;
import com.mdv.corefinance.repos.AccountRepository;
import com.mdv.corefinance.repos.LoanRepository;
import com.mdv.corefinance.repos.SubscriberRepository;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/sub_api")
public class SubscriberRestController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberRestController.class);

    @Autowired
    private AccountRepository accountRepository;


    @Autowired
    private SubscriberRepository subrepo;


    @RequestMapping("/getAll")
    public List<Subscriber> subscribers (){

        return subrepo.findAll();
    }

    @RequestMapping("/getById")
    public Subscriber getSubscriberById(@RequestParam("sid") String sid) {
        ObjectId oid = null;
        try{
            oid = new ObjectId(sid);
        }catch(Exception e){
            throw new GenericRestException("No correct Object ID provided for subscriber ID["+sid+"]");
        }

        //final String uri = "http://"+base+":"+port+"/acc_api/getAccBySub?sid="+sid;
        //RestTemplate restTemplate = new RestTemplate();

        Subscriber sub = subrepo.findSubscriberById(oid);
        List<Account> accounts = accountRepository.findAccountBySubscriberId(oid); //restTemplate.getForObject(uri,List.class);

        sub.accounts = accounts;
        

        return sub;



    }

    @RequestMapping(value = "/subscriber", method = RequestMethod.POST)
    public Subscriber create(
            @RequestParam(value = "type", defaultValue = "user") String type,
            @RequestParam("msisdn") String msisdn
            ){



        Subscriber sub = subrepo.findSubscriberByMsisdnAndType(msisdn,type);
        if (null != sub) throw new SubscriberAlreadyRegisteredException("MSISDN ["+msisdn+"] already present into our records");
        sub =  new Subscriber();
        sub.type = type;
        sub.msisdn = msisdn;
        sub.createDate = new Date();
        subrepo.save(sub);

        return sub;

    }

    @RequestMapping("/fetch")
    public Subscriber fetch(@RequestParam(value = "type", defaultValue = "user") String type,@RequestParam("msisdn") String msisdn){

        Subscriber sub = subrepo.findSubscriberByMsisdnAndType(msisdn,type);
        if (null == sub) throw new SubscriberNotFoundException("MSISDN ["+msisdn+"] not found into our records for user type ["+type+"]");
        return sub;

    }








}