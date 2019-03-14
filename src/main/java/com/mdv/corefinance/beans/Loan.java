package com.mdv.corefinance.beans;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Document(collection = "loan")
public class Loan {

    @Id
    public ObjectId id;

    public String type;

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String fee;
    public String productId;

    public Definition definition;




    public Loan(String pid, String t, String f) {
        this.productId = pid;
        this.type = t;
        this.fee = f;


    }

    public Loan(ObjectId oid, String pid, String t, String f) {
        this.id = oid;
        this.productId = pid;
        this.type = t;
        this.fee = f;


    }

    public Loan(ObjectId oid, String pid, String t, String r, String f, Definition def) {
        this.id = oid;
        this.productId = pid;
        this.type = t;
        this.fee = f;
        this.definition = def;


    }//*/

    public Loan(){

    }










}
