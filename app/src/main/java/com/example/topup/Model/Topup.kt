package com.example.paymenttopup.Model

class Topup {
    var id:String = ""
    var money:Int = 0

    constructor(){}

    constructor(id:String,money:Int){
        this.id = id
        this.money = money
    }
}