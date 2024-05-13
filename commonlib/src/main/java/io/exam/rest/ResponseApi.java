package io.exam.rest;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ResponseApi<T> {

  private int code;
  private String message;
  private T data;


  public ResponseApi(int code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public ResponseApi(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public static <T> ResponseApi<T> success(T data) {
    return new ResponseApi<>(200, "", data);
  }

  public static ResponseApi<Void> error(int code, String message) {
    return new ResponseApi<>(code, message);
  }
}
