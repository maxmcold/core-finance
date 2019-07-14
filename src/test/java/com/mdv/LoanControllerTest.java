package com.mdv;

import com.mdv.corefinance.beans.Account;
import com.mdv.corefinance.repos.AccountRepository;
import com.mdv.corefinance.repos.LoanRepository;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class LoanControllerTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void whenCreditPrincipalAmount_increase(){

        //given
        Account account = accountRepository.findAccountByTypeAndSubscriberId("credit",new ObjectId("5be2a1f966ca121f54fda71b"));

        //when

        //then
    }

}
