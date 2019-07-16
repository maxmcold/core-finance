package com.mdv.corefinance;


import com.mdv.corefinance.beans.Account;
import com.mdv.corefinance.beans.Subscriber;
import com.mdv.corefinance.repos.AccountRepository;
import com.mdv.corefinance.repos.SubscriberRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.annotation.PostConstruct;


@SpringBootApplication
public class CoreFinanceApplication extends SpringBootServletInitializer{

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    private static final Logger logger = LoggerFactory.getLogger(CoreFinanceApplication.class);

    @Override
    protected SpringApplicationBuilder configure (SpringApplicationBuilder builder){
        return builder.sources(CoreFinanceApplication.class);
    }
    public static void main(String[] args) {

        SpringApplication.run(CoreFinanceApplication.class, args);

    }


    @PostConstruct
    private void init() {

        logger.info("Custom Fintech Application initialization logic ...");

        ObjectId subsId = systemSubscriberInit();

        airtimeAccountInit(subsId);

    }

    private ObjectId systemSubscriberInit(){

        Subscriber subscriber = subscriberRepository.findSubscriberByType("system");
        if (null != subscriber) logger.info("system subscriber ----> OK");
        else{
            logger.info("system subscriber ----> KO!!! Creating...");
            subscriber = new Subscriber();
            subscriber.msisdn = "00000000000";
            subscriberRepository.save(subscriber);


        }
        return subscriber.id;

    }

    private void airtimeAccountInit(ObjectId sysSubId){
        Account account = accountRepository.findAccountByTypeAndProductId("system.credit","1");
        if (null != account) logger.info("system.credit ----> OK");
        else{
            logger.info("system.credit ----> KO!!! Creating...");
            account = new Account();
            account.type = "system.credit";
            account.currency = "USD";
            account.principal = new Double(99999999.0);
            account.subscriberId = sysSubId;
            account.productId = "1"; //TODO: extend for all other product types in future
            accountRepository.save(account);

        }

        account = accountRepository.findAccountByTypeAndProductId("system.recovery","1");
        if (null != account) logger.info("system.recovery ----> OK");
        else{
            logger.info("system.recovery ----> KO!!! Creating...");
            account = new Account();
            account.type = "system.recovery";
            account.currency = "USD";
            account.principal = new Double(0.0);
            account.subscriberId = sysSubId;
            account.productId = "1"; //TODO: extend for all other product types in future
            accountRepository.save(account);

        }

    }







}