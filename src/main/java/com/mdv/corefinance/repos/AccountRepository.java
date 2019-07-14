package com.mdv.corefinance.repos;

import java.util.List;
import java.util.Optional;

import com.mdv.corefinance.beans.Account;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.mdv.corefinance.beans.Loan;

public interface AccountRepository extends MongoRepository<Account, ObjectId>{




    @Override
    List<Account> findAll();

    List<Account> findAccountBySubscriberId(ObjectId id);

    Account findAccountByAccountid(String id);

    Account findAccountById(ObjectId id);

    Account findAccountByTypeAndProductId(String type,String pid);

    Account findAccountByTypeAndSubscriberId(String type,ObjectId sid);

    Account findAccountByType(String type);








}
