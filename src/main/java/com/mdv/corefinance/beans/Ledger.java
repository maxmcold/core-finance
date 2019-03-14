package com.mdv.corefinance.beans;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "ledger")
public class Ledger {

    @Id
    public ObjectId id;

    public String ttype;
    public String source;
    public String target;
    public String amount;
    public String currency;




    public Ledger(String tt, String so, String ta, String am, String cy) {
        this.ttype = tt;
        this.source = so;
        this.target = ta;
        this.amount = am;
        this.currency = cy;


    }
    public Ledger(ObjectId index, String tt, String so, String ta, String am, String cy) {
        this.id = index;
        this.ttype = tt;
        this.source = so;
        this.target = ta;
        this.amount = am;
        this.currency = cy;


    }





    public Ledger(){

    }










}