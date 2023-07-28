package com.example.demoapp

class User {
    var gmail:String?=null
    var name:String?=null
    var profileImage:String?=null
    var uid:String?=null
    constructor(){}
    constructor(
        gmail:String?,
        name:String?,
        profileImage:String?,
        uid:String?,
    ){
        this.gmail=gmail
        this.name=name
        this.profileImage=profileImage
        this.uid=uid

    }
}