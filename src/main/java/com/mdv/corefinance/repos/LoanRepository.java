package com.mdv.corefinance.repos;


import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.mdv.corefinance.beans.Loan;

public interface LoanRepository extends MongoRepository<Loan, ObjectId>{


    List<Loan> findLoansByProductId(String pid);

    @Override
    List<Loan> findAll();

    Loan findLoanById(ObjectId id);




}
