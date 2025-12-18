package com.example.TakeTicket.userModel.dto;

import lombok.Data;


public record AgentRegisterRequest (
    String firstName,
String lastName,
 String email,
 String password,
  String matricule
){}
