package com.example.automaticirrigationsystem.exception;

public class SensorCantBeReachedException extends
    RuntimeException {

  public SensorCantBeReachedException(String s) {
    super(s);
  }
}
