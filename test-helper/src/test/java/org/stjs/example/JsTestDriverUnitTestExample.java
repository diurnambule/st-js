package org.stjs.example;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.stjs.testing.jstestdriver.JSTestDriverRunner;

@RunWith(JSTestDriverRunner.class)
public class JsTestDriverUnitTestExample {
   
  @Test 
  public void shouldCompainThatFooIsNotBar() throws Exception {
    MyPojo pojo = new MyPojo("Foo");
    assertEquals("bar", pojo.y);
  }
  
  @Test 
  public void shouldRetreiveString() throws Exception {
    MyPojo pojo = new MyPojo("Foo");
    assertEquals("Foo", pojo.y);
  }
  
}