/** 
 * This is the first java file used for testing.
 * Git Repo.
 */

package com.example;

import java.io.*;
import java.util.*;

public class HelloWorld {
  private String name;
  public HelloWorld(String name) {
     this.name = name;
  }

  public void wish() {
     System.out.println("Hello "+ name);
  }

  public static void main(String[] args) {
     HelloWorld hw = new HelloWorld(args[0]);
     hw.wish();
  }

}
