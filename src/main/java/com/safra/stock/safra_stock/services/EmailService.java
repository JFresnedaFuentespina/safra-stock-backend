package com.safra.stock.safra_stock.services;

public interface EmailService {

    public void sendEmail(String to, String subject, String text, boolean html);
}
