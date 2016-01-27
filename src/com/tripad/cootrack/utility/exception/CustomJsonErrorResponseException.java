package com.tripad.cootrack.utility.exception;

public class CustomJsonErrorResponseException extends Exception {
  //Parameterless Constructor
  public CustomJsonErrorResponseException() {
  }

  //Constructor that accepts a message
  public CustomJsonErrorResponseException(String message) {
    super(message);
  }

  public CustomJsonErrorResponseException(Throwable cause) {
    super(cause);
    //getLogger().error(cause.getMessage(), cause);
  }
}