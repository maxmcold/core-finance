package com.mdv.corefinance.controllers;


import com.mdv.corefinance.beans.Account;
import com.mdv.corefinance.beans.Loan;
import com.mdv.corefinance.beans.Subscriber;
import com.mdv.corefinance.exceptions.GenericRestException;
import com.mdv.corefinance.repos.LoanRepository;
import com.mdv.corefinance.repos.SubscriberRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/subscriber")
public class SubscriberRestController {
    private static final Logger logger = LoggerFactory.getLogger(SubscriberRestController.class);

    @Value("${base.url}")
    String base;
    @Value("${server.port}")
    String port;

    @Autowired
    private Environment env;

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
        //TODO: this is fully hardcoded to test remote API call. Use internal for execution.
        final String uri = "http://"+base+":"+port+"/acc_api/getAccBySub?sid="+sid;
        RestTemplate restTemplate = new RestTemplate();
        Subscriber sub = subrepo.findSubscriberById(oid);
        List<Account> accounts = restTemplate.getForObject(uri,List.class);
        sub.accounts = accounts;
        

        return sub;



    }








}