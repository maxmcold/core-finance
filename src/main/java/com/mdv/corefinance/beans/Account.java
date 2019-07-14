package com.mdv.corefinance.beans;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "account")
public class Account {

    @Id
    public ObjectId id;

    public String accountid;
    public String currency;

    @Field("sid")
    public ObjectId subscriberId;
    public String type;
    public String productId;
    public Double balance;
    public Double principal;
    public Double fee;

    public Account(Double cu, String aid, String cy) {
        this.balance = cu;
        this.accountid = aid;
        this.currency = cy;


    }
    public Account(ObjectId index, Double cu, String aid, String currency) {
        this.id = index;
        this.balance = cu;
        this.accountid = aid;
        this.currency = currency;
        this.id = index;


    }
    public Account(ObjectId index, Double principal, Double fee, String aid, String currency) {
        this.id = index;
        this.principal = principal;
        this.fee = fee;
        this.accountid = aid;
        this.currency = currency;
        this.id = index;


    }





    public Account(){

    }
    public String toString(){
        String out = "id: " + this.id + "\n" +
                "accountId: "+this.accountid + "\n" +
                "subscriberId: "+this.subscriberId+ "\n" +
                "currency: "+this.currency+ "\n" +
                "balance: "+this.balance+ "\n" +
                "productId: "+this.productId+ "\n" +
                "type: "+this.type+ "\n";

        return out;
    }












}
