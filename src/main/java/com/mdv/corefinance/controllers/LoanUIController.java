package com.mdv.corefinance.controllers;

import com.mdv.corefinance.beans.Account;
import com.mdv.corefinance.beans.Loan;
import com.mdv.corefinance.repos.DefinitionRepository;
import com.mongodb.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;

import com.mdv.corefinance.repos.LoanRepository;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller

public class LoanUIController {
    private static final Logger logger = LoggerFactory.getLogger(LoanUIController.class);

    @Autowired
    private LoanRepository lr;
    @Autowired
    DefinitionRepository dr;

    @RequestMapping("/home")
    public String home (){

        return "index";
    }

    @Value("${base.url}")
    private String baseUrl;

    @Value("${server.port}")
    private String serverPort;

    @Value("fintech.default.currency")
    private String defaultCurrency;

    @RequestMapping("/")
    public String index (){



        return "index";
    }
    @RequestMapping("/collect")
    public String collect (){

        return "collect";
    }

    @RequestMapping("/account")
    public String account(@RequestParam("aid") String aid, Model model) {
        RestTemplate rt = new RestTemplate();
        String out = rt.getForObject("http://localhost:8081/loan_api/gaccboid?aid="+aid, String.class );
        Account account = rt.getForObject("http://localhost:8081/ln_api/gaccboid?aid="+aid,Account.class);
        model.addAttribute("aid", aid);
        model.addAttribute("account", account);

        return "test";

    }

    @RequestMapping("/transfer")
    public String transfer(@RequestParam("from") String from, @RequestParam("to") String to,
                           @RequestParam("amt") String amount,@RequestParam("type") String type,
                           @Nullable @RequestParam("currency") String currency, Model model) {
        RestTemplate rt = new RestTemplate();

        //check input parameters

        currency = (currency.isEmpty()) ? defaultCurrency : currency;

        //TODO: add exception handling for input parameters
        String url = baseUrl + ":" +
                serverPort +
                "/loan_api/transfer?" +
                "from=" + from +
                "&to="+ to +
                "&type="+ type +
                "&amt="+ amount +
                "&currency=" + currency;


        String out = rt.getForObject(url,String.class );
        model.addAttribute("out",out);

        return "test";

    }

    


    @RequestMapping("/loans")
    public String loans (Model model){
        List<Loan> loans = lr.findAll();
        logger.debug(loans.toString());

        StringBuilder sb = new StringBuilder();
        for (Loan l : loans)
        {
            sb.append("_id: "+l.id+"\n");
            sb.append("productId: "+l.productId+"\n");
            sb.append("fee: "+l.fee+"\n");
            sb.append("type: "+l.type+"\n");
            if (null != l.definition){
                sb.append("Definition:\n\tinterest: "+ l.definition.interest+"\n");
                sb.append("\tdurationMonth: "+l.definition.durationMonths+"\n");
            }


            sb.append("\n");
        }

        model.addAttribute("loans", sb.toString() );
        return "loans";
    }



}