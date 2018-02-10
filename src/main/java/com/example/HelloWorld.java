/** 
 * This is the first java file used for testing.
 * Git Repo.
 */

package com.example;

import java.io.*;
import java.util.*;

public class HelloWorld {
  public HelloWorld(String name) {
     System.out.println("Hello "+ name);
  }
  public static void main(String[] args) {
     HelloWorld hw = new HelloWorld(args[0]);
  }
}
