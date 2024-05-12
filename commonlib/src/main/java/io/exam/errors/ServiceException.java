package io.exam.errors;

public class ServiceException extends RuntimeException {

  public ServiceException(String msg) {
    super(msg);
  }

}
