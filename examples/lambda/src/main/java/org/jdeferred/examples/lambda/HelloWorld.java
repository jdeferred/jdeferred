package org.jdeferred.examples.lambda;

import org.jdeferred.*;
import org.jdeferred.impl.DefaultDeferredManager;

public class HelloWorld {
  public static void main(String... args) {
    DefaultDeferredManager dm = new DefaultDeferredManager();

    Promise p1 = dm.when(
      () -> {
        return "Hello";
      },
      () -> {
        return "Ray";
      }
      ).done(rs -> 
        rs.forEach(r -> 
          System.out.println(r.getResult())
      )
    );

    Promise p2 = dm.when(() -> {
      return "Hey!";
    }).done(r -> System.out.println(r));

    try {
      dm.when(p1, p2).waitSafely();
    } catch (InterruptedException e) {}

    dm.shutdown();
  }
}

