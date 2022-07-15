package com.example.automaticirrigationsystem.exception;

public class PlotHasAlreadyStartedToBeIrrigated extends
    RuntimeException {

  public PlotHasAlreadyStartedToBeIrrigated(String msg) {
    super(msg);
  }
}
