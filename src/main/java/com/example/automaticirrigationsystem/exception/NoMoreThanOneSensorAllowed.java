package com.example.automaticirrigationsystem.exception;

public class NoMoreThanOneSensorAllowed extends
    RuntimeException {

  public NoMoreThanOneSensorAllowed(String message) {
    super(message);
  }
}
