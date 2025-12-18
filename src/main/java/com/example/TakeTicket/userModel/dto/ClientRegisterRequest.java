package com.example.TakeTicket.userModel.dto;

public record ClientRegisterRequest(String firstName,
                                    String lastName,
                                    String email,
                                    String password) {
}
